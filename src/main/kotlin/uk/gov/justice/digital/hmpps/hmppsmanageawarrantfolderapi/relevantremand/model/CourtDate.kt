package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate

data class CourtDate(
  val date: LocalDate,
  val type: CourtDateType
)
