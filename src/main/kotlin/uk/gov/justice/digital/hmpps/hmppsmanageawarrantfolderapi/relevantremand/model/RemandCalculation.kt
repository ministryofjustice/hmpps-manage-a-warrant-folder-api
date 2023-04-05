package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

data class RemandCalculation(
  val prisonerId: String,
  val charges: List<ChargeAndEvents>
)
