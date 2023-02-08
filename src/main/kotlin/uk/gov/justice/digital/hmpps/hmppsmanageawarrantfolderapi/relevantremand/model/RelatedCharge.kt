package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate

data class RelatedCharge(
  val offenceDate: LocalDate,
  val offenceEndDate: LocalDate?,
  val offenceCode: String
)
