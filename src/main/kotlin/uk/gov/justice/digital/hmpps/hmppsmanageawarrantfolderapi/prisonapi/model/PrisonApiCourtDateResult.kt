package uk.gov.justice.hmpps.prison.api.model.digitalwarrant

import com.fasterxml.jackson.annotation.JsonInclude
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model.PrisonApiCharge
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PrisonApiCourtDateResult(
  val id: Long,
  val date: LocalDate?,
  val resultCode: String?,
  val resultDescription: String?,
  val charge: PrisonApiCharge,
  val bookingId: Long
)
