package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import java.time.LocalDate

@Service
class RemandCalculationService {

  fun calculate(remandCalculation: RemandCalculation): List<Remand> {
    if (remandCalculation.charges.isEmpty()) {
      throw UnsupportedCalculationException("There are no charges to calculate")
    }
    if (remandCalculation.charges.size > 1) {
      throw UnsupportedCalculationException("Relevant remand calculation not supported for prisoners with multiple charges")
    }

    val charge = remandCalculation.charges[0]
    val sortedDates = charge.courtDates.sortedBy { it.date }
    return remandClock(charge, sortedDates)
  }

  private fun remandClock(charge: Charge, dates: List<CourtDate>): List<Remand> {
    val remand = mutableListOf<Remand>()
    if (hasAnyRemandEvent(dates)) {
      var from: LocalDate? = null
      dates.forEach {
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

  private fun hasAnyRemandEvent(courtDates: List<CourtDate>) = courtDates.any { it.type == CourtDateType.START }

  private fun getToDate(courtDate: CourtDate) = if (courtDate.final) courtDate.date.minusDays(1) else courtDate.date
}
