package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.controller


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.integration.wiremock.PrisonApiExtension
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import java.time.LocalDate

class RelevantRemandControllerIntTest : IntegrationTestBase() {

  @Test
  fun `Run calculation for a prisoner`() {
    val result = webTestClient.post()
      .uri("/relevant-remand/${PrisonApiExtension.PRISONER_ID}")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_RELEASE_DATES_CALCULATOR")))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(object : ParameterizedTypeReference<List<Remand>>() {})
      .returnResult().responseBody

    assertThat(result).isNotEmpty
    assertThat(result).isEqualTo(listOf(Remand(
      from = LocalDate.of(2022, 10, 13),
      to = LocalDate.of(2022, 12, 12),
      sentence = 1
    )))
    assertThat(result!![0].days).isEqualTo(61)
  }


}
