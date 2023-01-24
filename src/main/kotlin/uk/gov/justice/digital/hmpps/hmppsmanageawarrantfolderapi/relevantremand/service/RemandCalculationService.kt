package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import java.time.LocalDate

class RemandCalculationService {

  fun calculate(remandCalculation: RemandCalculation): List<Remand> {
    if (remandCalculation.charges.isEmpty()) {
      throw UnsupportedCalculationException("There are no charges to calculate")
    }
    if (remandCalculation.charges.size > 1) {
      throw UnsupportedCalculationException("Relevant remand calculation not supported for prisoners with multiple charges")
    }

    val charge = remandCalculation.charges[0]

    charge.courtDates.sortedBy { it.date }

    if (charge.courtDates.any { it.type == CourtDateType.START }) {
      var from: LocalDate? = null
      var remand = mutableListOf<Remand>()
      charge.courtDates.forEach {
        if (it.type == CourtDateType.START && from == null) {
          from = it.date
        }
        if (it.type == CourtDateType.STOP && from != null) {
          remand.add(Remand(from!!, it.date.minusDays(1), charge.sentenceSequence))
          from = null
        }
      }
      return remand
    } else {
      return emptyList()
    }
  }
}
