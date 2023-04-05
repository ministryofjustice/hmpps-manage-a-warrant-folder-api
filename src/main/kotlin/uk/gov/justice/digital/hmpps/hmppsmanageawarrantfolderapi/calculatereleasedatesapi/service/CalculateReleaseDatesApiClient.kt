package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationRequest
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationResult

@Service
class CalculateReleaseDatesApiClient(@Qualifier("calculateReleaseDatesApiWebClient") private val webClient: WebClient) {
  private val log = LoggerFactory.getLogger(this::class.java)

  fun calculateReleaseDates(prisonerId: String, request: RelevantRemandCalculationRequest): RelevantRemandCalculationResult {
    log.info("Requesting a release date calculation for $prisonerId")
    return webClient.post()
      .uri("/api/calculation/relevant-remand/$prisonerId")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(RelevantRemandCalculationResult::class.java)
      .block()!!
  }
}
