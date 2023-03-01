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
          it.value.first().bookingId,
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
    "4012" -> CourtDateType.START
    "4588" -> CourtDateType.START
    "4534" -> CourtDateType.START
    "4537" -> CourtDateType.START
    "2053" -> CourtDateType.STOP
    "1501" -> CourtDateType.STOP
    "1115" -> CourtDateType.STOP
    "1116" -> CourtDateType.STOP
    "4530" -> CourtDateType.STOP
    "1002" -> CourtDateType.STOP
    "4015" -> CourtDateType.STOP
    "1510" -> CourtDateType.STOP
    "2006" -> CourtDateType.STOP
    "2005" -> CourtDateType.STOP
    "2004" -> CourtDateType.STOP
    "4542" -> CourtDateType.STOP
    "1024" -> CourtDateType.STOP
    "1007" -> CourtDateType.STOP
    "4559" -> CourtDateType.STOP
    "1015" -> CourtDateType.STOP
    "2050" -> CourtDateType.STOP
    "1507" -> CourtDateType.STOP
    "1106" -> CourtDateType.STOP
    "1057" -> CourtDateType.STOP
    "5600" -> CourtDateType.STOP
    "1081" -> CourtDateType.STOP
    "1018" -> CourtDateType.STOP
    "1105" -> CourtDateType.STOP
    "4529" -> CourtDateType.STOP
    "2051" -> CourtDateType.STOP
    "1019" -> CourtDateType.STOP
    "1022" -> CourtDateType.STOP
    "3012" -> CourtDateType.STOP
    "3058" -> CourtDateType.STOP
    "1017" -> CourtDateType.STOP
    "4543" -> CourtDateType.STOP
    "4558" -> CourtDateType.STOP
    "4014" -> CourtDateType.STOP
    "2068" -> CourtDateType.STOP
    "2066" -> CourtDateType.STOP
    "4589" -> CourtDateType.STOP
    "2067" -> CourtDateType.STOP
    "2065" -> CourtDateType.STOP
    "NC" -> CourtDateType.STOP
    "2501" -> CourtDateType.STOP
    "4572" -> CourtDateType.CONTINUE
    "4506" -> CourtDateType.CONTINUE
    "5500" -> CourtDateType.CONTINUE
    "G" -> CourtDateType.CONTINUE
    "1046" -> CourtDateType.CONTINUE
    "3045" -> CourtDateType.CONTINUE
    "2007" -> CourtDateType.CONTINUE
    "3044" -> CourtDateType.CONTINUE
    "3056" -> CourtDateType.CONTINUE
    "1029" -> CourtDateType.CONTINUE
    "1021" -> CourtDateType.CONTINUE
    "5502" -> CourtDateType.CONTINUE
    "5501" -> CourtDateType.CONTINUE

    else -> {
      throw UnsupportedCalculationException("${courtDateResult.resultCode}: ${courtDateResult.resultDescription} is unsupported")
    }
  }
}
