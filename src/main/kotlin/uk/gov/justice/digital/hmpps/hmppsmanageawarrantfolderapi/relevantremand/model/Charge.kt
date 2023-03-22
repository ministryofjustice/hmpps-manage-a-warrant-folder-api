package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate

data class Charge(
  val chargeId: Long,
  val offence: Offence,
  val offenceDate: LocalDate,
  val offenceEndDate: LocalDate?,
  val sentenceSequence: Int?,
  val bookingId: Long,
  val courtCaseRef: String?
)
