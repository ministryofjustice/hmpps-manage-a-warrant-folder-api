package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class RemandPeriod(
  val from: LocalDate,
  val to: LocalDate,
  val offenceDate: LocalDate,
  val offenceEndDate: LocalDate?,
  val offenceCode: String,
  val offenceDescription: String,
  val courtCaseRef: String? = null,
  val chargeId: Long,
) {
  val days: Long get() {
    return ChronoUnit.DAYS.between(from, to) + 1
  }
}
