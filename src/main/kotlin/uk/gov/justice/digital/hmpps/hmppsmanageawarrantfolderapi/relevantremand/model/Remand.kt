package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Remand(
  val from: LocalDate,
  val to: LocalDate,
  val charge: Charge
) {
  val days: Long get() {
    return ChronoUnit.DAYS.between(from, to) + 1
  }
}
