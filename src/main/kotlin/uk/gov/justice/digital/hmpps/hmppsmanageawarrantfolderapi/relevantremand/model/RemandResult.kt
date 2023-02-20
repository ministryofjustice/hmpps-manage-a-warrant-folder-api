package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

data class RemandResult(
  val remandPeriods: List<RemandPeriod>,
  val finalRemand: List<Remand>
)
