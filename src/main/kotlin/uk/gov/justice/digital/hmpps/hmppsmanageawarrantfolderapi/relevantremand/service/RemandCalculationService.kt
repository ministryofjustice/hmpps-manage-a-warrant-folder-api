package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RelatedCharge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandPeriod
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandResult
import java.time.LocalDate

@Service
class RemandCalculationService {

  fun calculate(remandCalculation: RemandCalculation): RemandResult {
    if (remandCalculation.charges.isEmpty()) {
      throw UnsupportedCalculationException("There are no charges to calculate")
    }
    val charges = combineRelatedCharges(remandCalculation)
    val remandPeriods = remandClock(charges)
    val remand = extractFinalRemand(remandPeriods)
    return RemandResult(remandPeriods.map { it.second }, remand)
  }

  private fun remandClock(charges: List<Charge>): List<Pair<Charge, RemandPeriod>> {
    val remand = mutableListOf<Pair<Charge, RemandPeriod>>()
    charges.forEach { charge ->
      if (hasAnyRemandEvent(charge.courtDates)) {
        var from: LocalDate? = null
        charge.courtDates.forEach {
          if (it.type == CourtDateType.START && from == null) {
            from = it.date
          }
          if (it.type == CourtDateType.STOP && from != null) {
            remand.add(charge to RemandPeriod(from!!, getToDate(it), charge.offenceDate, charge.offenceEndDate, charge.offence.code, charge.offence.description, charge.courtCaseRef, charge.chargeId))
            from = null
          }
        }
      }
    }
    return remand
  }

  private fun extractFinalRemand(remandPeriods: List<Pair<Charge, RemandPeriod>>): List<Remand> {
    val remands = mutableListOf<Remand>()
    val sortedRemand = remandPeriods.sortedBy { it.second.from }
    sortedRemand.forEachIndexed { index, it ->
      val charge = it.first
      val period = it.second
      if (charge.sentenceSequence != null && !remandAlreadyCovered(remands, period)) {
        val start = listOfNotNull(period.from, startOfLastRemand(remands)).max()
        val nextPeriod = nextPeriodOrNull(index, sortedRemand)
        val end = if (nextRemandOverlaps(period, nextPeriod)) {
          nextPeriod!!.from.minusDays(1)
        } else {
          period.to
        }
        remands.add(Remand(start, end, charge.sentenceSequence))
      }
    }
    return remands
  }
  private fun startOfLastRemand(remands: List<Remand>): LocalDate? = remands.lastOrNull()?.from

  private fun remandAlreadyCovered(remands: List<Remand>, it: RemandPeriod): Boolean = remands.isNotEmpty() && remands.last().to >= it.to

  private fun nextRemandOverlaps(it: RemandPeriod, nextPeriod: RemandPeriod?): Boolean = nextPeriod != null && nextPeriod.from != it.from && nextPeriod.from < it.to

  private fun nextPeriodOrNull(index: Int, sortedRemand: List<Pair<Charge, RemandPeriod>>): RemandPeriod? = if (index == sortedRemand.size - 1) null else sortedRemand[index + 1].second

  private fun combineRelatedCharges(remandCalculation: RemandCalculation): List<Charge> {
    val mapOfRelatedCharges = remandCalculation.charges.groupBy { RelatedCharge(it.offenceDate, it.offenceEndDate, it.offence.code) }
    return mapOfRelatedCharges.map {
      pickMostAppropriateCharge(it.value).copy(
        courtDates = flattenCourtDates(it.value)
      )
    }
  }

  private fun pickMostAppropriateCharge(relatedCharges: List<Charge>): Charge {
    // Pick the charge with a sentence attached, otherwise just the first charge. This logic may change.
    return relatedCharges.find { it.sentenceSequence != null } ?: relatedCharges.first()
  }

  private fun flattenCourtDates(relatedCharges: List<Charge>) = relatedCharges.flatMap { it.courtDates }.sortedBy { it.date }

  private fun hasAnyRemandEvent(courtDates: List<CourtDate>) = courtDates.any { it.type == CourtDateType.START }

  private fun getToDate(courtDate: CourtDate) = if (courtDate.final) courtDate.date.minusDays(1) else courtDate.date
}
