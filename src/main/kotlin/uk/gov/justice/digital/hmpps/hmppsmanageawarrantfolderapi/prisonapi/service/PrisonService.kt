package uk.gov.justice.digital.hmpps.calculatereleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.hmpps.prison.api.model.digitalwarrant.PrisonApiCourtDateResult

@Service
class PrisonService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getCourtDateResults(prisonerId: String): List<PrisonApiCourtDateResult> {
    return prisonApiClient.getCourtDateResults(prisonerId)
  }
}
