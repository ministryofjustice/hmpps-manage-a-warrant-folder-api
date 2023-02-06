package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.model.PrisonApiCourtDateResult

@Service
class PrisonService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getCourtDateResults(prisonerId: String): List<PrisonApiCourtDateResult> {
    return prisonApiClient.getCourtDateResults(prisonerId)
  }
}
