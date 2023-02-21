package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.transform

import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model.PrisonApiCharge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model.PrisonApiCourtDateResult
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Offence
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation

fun transform(results: List<PrisonApiCourtDateResult>): RemandCalculation {
  return RemandCalculation(
    results.groupBy { it.charge.chargeId }
      .map {
        val charge = it.value.first().charge
        if (charge.offenceDate == null) {
          throw UnsupportedCalculationException("The charge ${charge.chargeId} has no offence date.")
        }
        Charge(
          it.key,
          transform(charge),
          charge.offenceDate,
          charge.offenceEndDate,
          charge.sentenceSequence,
          charge.courtCaseRef,
          it.value.map { result -> transformToCourtDate(result) }
        )
      }
  )
}

private fun transform(prisonApiCharge: PrisonApiCharge): Offence {
  return Offence(prisonApiCharge.offenceCode, prisonApiCharge.offenceStatue, prisonApiCharge.offenceDescription)
}

private fun transformToCourtDate(courtDateResult: PrisonApiCourtDateResult): CourtDate {
  return CourtDate(
    courtDateResult.date!!,
    transformToType(courtDateResult),
    courtDateResult.resultDispositionCode == "F"
  )
}

private fun transformToType(courtDateResult: PrisonApiCourtDateResult): CourtDateType {
  if (courtDateResult.resultCode == null) {
    throw UnsupportedCalculationException("The court event ${courtDateResult.id} has no outcome.")
  }
  return when (courtDateResult.resultCode) {
    "4531" -> CourtDateType.START
    "4560" -> CourtDateType.START
    "4565" -> CourtDateType.START
    "4004" -> CourtDateType.START
    "4016" -> CourtDateType.START
    "4001" -> CourtDateType.START
    "1501" -> CourtDateType.STOP
    "1115" -> CourtDateType.STOP
    "1116" -> CourtDateType.STOP
    "4530" -> CourtDateType.STOP
    "1002" -> CourtDateType.STOP
    "4015" -> CourtDateType.STOP
    "4572" -> CourtDateType.CONTINUE
    "4506" -> CourtDateType.CONTINUE
    "1510" -> CourtDateType.CONTINUE
    "5500" -> CourtDateType.CONTINUE
    "4012" -> CourtDateType.CONTINUE
    "G" -> CourtDateType.CONTINUE
    else -> {
      throw UnsupportedCalculationException("${courtDateResult.resultCode}: ${courtDateResult.resultDescription} is unsupported")
    }
  }
}
