package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.ChargeAndEvents
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RelatedCharge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandResult
import java.time.LocalDate

@Service
class RemandCalculationService {

  fun calculate(remandCalculation: RemandCalculation): RemandResult {
    if (remandCalculation.charges.isEmpty()) {
      throw UnsupportedCalculationException("There are no charges to calculate")
    }
    val charges = combineRelatedCharges(remandCalculation)
    val chargeRemand = remandClock(charges)
    val sentenceRemand = extractSentenceRemand(chargeRemand)
    return RemandResult(chargeRemand, sentenceRemand)
  }

  private fun remandClock(remandCalculation: RemandCalculation): List<Remand> {
    val remand = mutableListOf<Remand>()
    remandCalculation.charges.forEach { chargeAndEvent ->
      if (hasAnyRemandEvent(chargeAndEvent.dates)) {
        var from: LocalDate? = null
        chargeAndEvent.dates.forEach {
          if (it.type == CourtDateType.START && from == null) {
            from = it.date
          }
          if (it.type == CourtDateType.STOP && from != null) {
            remand.add(Remand(from!!, getToDate(it), chargeAndEvent.charge))
            from = null
          }
        }
      }
    }
    return remand
  }

  private fun extractSentenceRemand(remandPeriods: List<Remand>): List<Remand> {
    val remands = mutableListOf<Remand>()
    val sortedPeriods = remandPeriods.filter { it.charge.sentenceSequence != null }.sortedBy { it.from }
    sortedPeriods.forEachIndexed { index, it ->
      val charge = it.charge
      if (!remandAlreadyCovered(remands, it)) {
        val start = listOfNotNull(it.from, startOfLastRemand(remands)).max()
        val nextPeriod = nextPeriodOrNull(index, sortedPeriods)
        val end = if (nextRemandOverlaps(it, nextPeriod)) {
          nextPeriod!!.from.minusDays(1)
        } else {
          it.to
        }
        remands.add(Remand(start, end, charge))
      }
    }
    return remands
  }
  private fun startOfLastRemand(remands: List<Remand>): LocalDate? = remands.lastOrNull()?.from

  private fun remandAlreadyCovered(remands: List<Remand>, it: Remand): Boolean = remands.isNotEmpty() && remands.last().to >= it.to

  private fun nextRemandOverlaps(it: Remand, nextPeriod: Remand?): Boolean = nextPeriod != null && nextPeriod.from != it.from && nextPeriod.from < it.to

  private fun nextPeriodOrNull(index: Int, sortedRemand: List<Remand>): Remand? = if (index == sortedRemand.size - 1) null else sortedRemand[index + 1]

  private fun combineRelatedCharges(remandCalculation: RemandCalculation): RemandCalculation {
    val mapOfRelatedCharges = remandCalculation.charges.groupBy { RelatedCharge(it.charge.offenceDate, it.charge.offenceEndDate, it.charge.offence.code) }
    return RemandCalculation(
      mapOfRelatedCharges.map {
        ChargeAndEvents(pickMostAppropriateCharge(it.value), flattenCourtDates(it.value))
      }
    )
  }

  private fun pickMostAppropriateCharge(relatedCharges: List<ChargeAndEvents>): Charge {
    // Pick the charge with a sentence attached, otherwise just the first charge. This logic may change.
    return relatedCharges.find { it.charge.sentenceSequence != null }?.charge ?: relatedCharges.first().charge
  }

  private fun flattenCourtDates(relatedCharges: List<ChargeAndEvents>) = relatedCharges.flatMap { it.dates }.sortedBy { it.date }

  private fun hasAnyRemandEvent(courtDates: List<CourtDate>) = courtDates.any { it.type == CourtDateType.START }

  private fun getToDate(courtDate: CourtDate) = if (courtDate.final) courtDate.date.minusDays(1) else courtDate.date
}
