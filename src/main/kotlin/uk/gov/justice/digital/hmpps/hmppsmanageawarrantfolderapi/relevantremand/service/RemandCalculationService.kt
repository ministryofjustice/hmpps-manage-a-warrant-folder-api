package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RelatedCharge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import java.time.LocalDate

@Service
class RemandCalculationService {

  fun calculate(remandCalculation: RemandCalculation): List<Remand> {
    if (remandCalculation.charges.isEmpty()) {
      throw UnsupportedCalculationException("There are no charges to calculate")
    }
    val charge = combineRelatedCharges(remandCalculation)
    return remandClock(charge)
  }

  private fun remandClock(charge: Charge): List<Remand> {
    val remand = mutableListOf<Remand>()
    if (hasAnyRemandEvent(charge.courtDates)) {
      var from: LocalDate? = null
      charge.courtDates.forEach {
        if (it.type == CourtDateType.START && from == null) {
          from = it.date
        }
        if (it.type == CourtDateType.STOP && from != null) {
          if (charge.sentenceSequence == null) {
            throw UnsupportedCalculationException("A charge should have remand, but no sentence has been added yet")
          }
          remand.add(Remand(from!!, getToDate(it), charge.sentenceSequence))
          from = null
        }
      }
    }
    return remand
  }

  private fun combineRelatedCharges(remandCalculation: RemandCalculation): Charge {
    val mapOfRelatedCharges = remandCalculation.charges.groupBy { RelatedCharge(it.offenceDate, it.offenceEndDate, it.offence.code) }
    if (mapOfRelatedCharges.size > 1) {
      throw UnsupportedCalculationException("Relevant remand calculation not supported for prisoners with multiple unrelated charges")
    } else {
      val relatedCharges = mapOfRelatedCharges.values.first()
      return pickMostAppropriateCharge(relatedCharges).copy(
        courtDates = flattenCourtDates(relatedCharges)
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
