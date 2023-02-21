package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model

import java.time.LocalDate

data class PrisonApiCharge(
  val chargeId: Long,
  val offenceCode: String,
  val offenceStatue: String,
  val offenceDate: LocalDate?,
  val offenceEndDate: LocalDate?,
  val offenceDescription: String,
  val guilty: Boolean,
  val courtCaseId: Long,
  val courtCaseRef: String?,
  val sentenceSequence: Int?
)
