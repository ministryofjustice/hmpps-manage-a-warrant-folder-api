package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.transform

import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model.PrisonApiCourtDateResult
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType

fun mapCourtDateResult(courtDateResult: PrisonApiCourtDateResult): CourtDateType {
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
    "1003" -> CourtDateType.STOP
    "1004" -> CourtDateType.STOP
    "1008" -> CourtDateType.STOP
    "1009" -> CourtDateType.STOP
    "1010" -> CourtDateType.STOP
    "1012" -> CourtDateType.STOP
    "1013" -> CourtDateType.STOP
    "1016" -> CourtDateType.STOP
    "1028" -> CourtDateType.STOP
    "1032" -> CourtDateType.STOP
    "1040" -> CourtDateType.STOP
    "1087" -> CourtDateType.STOP
    "1089" -> CourtDateType.STOP
    "1097" -> CourtDateType.STOP
    "1102" -> CourtDateType.STOP
    "1103" -> CourtDateType.STOP
    "1108" -> CourtDateType.STOP
    "1109" -> CourtDateType.STOP
    "1110" -> CourtDateType.STOP
    "1111" -> CourtDateType.STOP
    "1113" -> CourtDateType.STOP
    "1114" -> CourtDateType.STOP
    "1508" -> CourtDateType.STOP
    "1509" -> CourtDateType.STOP
    "2008" -> CourtDateType.STOP
    "2009" -> CourtDateType.STOP
    "2052" -> CourtDateType.STOP
    "2060" -> CourtDateType.STOP
    "2061" -> CourtDateType.STOP
    "2063" -> CourtDateType.STOP
    "2064" -> CourtDateType.STOP
    "2507" -> CourtDateType.CONTINUE
    "2511" -> CourtDateType.CONTINUE
    "2514" -> CourtDateType.CONTINUE
    "3006" -> CourtDateType.STOP
    "3007" -> CourtDateType.CONTINUE
    "3008" -> CourtDateType.CONTINUE
    "3011" -> CourtDateType.CONTINUE
    "3019" -> CourtDateType.STOP
    "3042" -> CourtDateType.CONTINUE
    "3047" -> CourtDateType.STOP
    "3054" -> CourtDateType.CONTINUE
    "3063" -> CourtDateType.STOP
    "3067" -> CourtDateType.STOP
    "3070" -> CourtDateType.CONTINUE
    "3072" -> CourtDateType.CONTINUE
    "3080" -> CourtDateType.STOP
    "3081" -> CourtDateType.STOP
    "3083" -> CourtDateType.CONTINUE
    "3086" -> CourtDateType.STOP
    "3088" -> CourtDateType.STOP
    "3091" -> CourtDateType.CONTINUE
    "3096" -> CourtDateType.CONTINUE
    "3101" -> CourtDateType.STOP
    "3102" -> CourtDateType.STOP
    "3105" -> CourtDateType.STOP
    "3107" -> CourtDateType.STOP
    "3108" -> CourtDateType.STOP
    "3109" -> CourtDateType.STOP
    "3112" -> CourtDateType.STOP
    "3501" -> CourtDateType.STOP
    "3502" -> CourtDateType.STOP
    "4005" -> CourtDateType.CONTINUE
    "4007" -> CourtDateType.CONTINUE
    "4008" -> CourtDateType.CONTINUE
    "4010" -> CourtDateType.STOP
    "4011" -> CourtDateType.STOP
    "4013" -> CourtDateType.STOP
    "4017" -> CourtDateType.STOP
    "4018" -> CourtDateType.STOP
    "4020" -> CourtDateType.STOP
    "4021" -> CourtDateType.STOP
    "4022" -> CourtDateType.STOP
    "4508" -> CourtDateType.CONTINUE
    "4509" -> CourtDateType.CONTINUE
    "4532" -> CourtDateType.START
    "4533" -> CourtDateType.STOP
    "4535" -> CourtDateType.START
    "4536" -> CourtDateType.START
    "4538" -> CourtDateType.STOP
    "4539" -> CourtDateType.START
    "4541" -> CourtDateType.CONTINUE
    "4548" -> CourtDateType.STOP
    "4550" -> CourtDateType.STOP
    "4552" -> CourtDateType.STOP
    "4553" -> CourtDateType.START
    "4554" -> CourtDateType.CONTINUE
    "4555" -> CourtDateType.STOP
    "4561" -> CourtDateType.STOP
    "4562" -> CourtDateType.STOP
    "4563" -> CourtDateType.START
    "4564" -> CourtDateType.START
    "4570" -> CourtDateType.START
    "4571" -> CourtDateType.START
    "4573" -> CourtDateType.CONTINUE
    "4575" -> CourtDateType.STOP
    "4576" -> CourtDateType.START
    "4577" -> CourtDateType.STOP
    "4582" -> CourtDateType.STOP
    "4584" -> CourtDateType.CONTINUE
    "4587" -> CourtDateType.STOP
    "4590" -> CourtDateType.CONTINUE
    "5601" -> CourtDateType.CONTINUE
    "5602" -> CourtDateType.STOP
    "FPR" -> CourtDateType.STOP

    else -> {
      throw UnsupportedCalculationException("${courtDateResult.resultCode}: ${courtDateResult.resultDescription} is unsupported")
    }
  }
}