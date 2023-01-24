package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

data class Charge(
  val chargeId: Long,
  val offence: Offence,
  val sentenceSequence: Int,
  val courtDates: List<CourtDate>
)
