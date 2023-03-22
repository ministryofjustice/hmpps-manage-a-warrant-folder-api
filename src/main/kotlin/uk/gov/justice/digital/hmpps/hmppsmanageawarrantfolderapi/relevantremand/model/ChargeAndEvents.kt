package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

data class ChargeAndEvents(
  val charge: Charge,
  val dates: List<CourtDate>
)
