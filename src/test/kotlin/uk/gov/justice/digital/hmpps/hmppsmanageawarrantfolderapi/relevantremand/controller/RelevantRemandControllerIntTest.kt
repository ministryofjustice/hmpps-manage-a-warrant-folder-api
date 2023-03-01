package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.integration.wiremock.PrisonApiExtension
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandPeriod
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandResult
import java.time.LocalDate

class RelevantRemandControllerIntTest : IntegrationTestBase() {

  @Test
  fun `Run calculation for a imprisoned prisoner`() {
    val result = webTestClient.post()
      .uri("/relevant-remand/${PrisonApiExtension.IMPRISONED_PRISONER}")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_MANAGE_DIGITAL_WARRANT")))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(RemandResult::class.java)
      .returnResult().responseBody!!

    assertThat(result.finalRemand).isNotEmpty
    assertThat(result.finalRemand).isEqualTo(
      listOf(
        Remand(
          from = LocalDate.of(2022, 10, 13),
          to = LocalDate.of(2022, 12, 12),
          sentence = 1,
          bookingId = 1
        )
      )
    )
    assertThat(result.finalRemand[0].days).isEqualTo(61)
  }
  @Test
  fun `Run calculation for a bail prisoner`() {
    val result = webTestClient.post()
      .uri("/relevant-remand/${PrisonApiExtension.BAIL_PRISONER}")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_MANAGE_DIGITAL_WARRANT")))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(RemandResult::class.java)
      .returnResult().responseBody!!

    assertThat(result.finalRemand).isNotEmpty
    assertThat(result.finalRemand).isEqualTo(
      listOf(
        Remand(
          from = LocalDate.of(2015, 3, 25),
          to = LocalDate.of(2015, 4, 8),
          sentence = 1,
          bookingId = 1
        )
      )
    )
    assertThat(result.finalRemand[0].days).isEqualTo(15)
  }
  @Test
  fun `Run calculation for a prisoner with related offences`() {
    val result = webTestClient.post()
      .uri("/relevant-remand/${PrisonApiExtension.RELATED_PRISONER}")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_MANAGE_DIGITAL_WARRANT")))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(RemandResult::class.java)
      .returnResult().responseBody!!

    assertThat(result.finalRemand).isNotEmpty
    assertThat(result.finalRemand).isEqualTo(
      listOf(
        Remand(
          from = LocalDate.of(2015, 3, 20),
          to = LocalDate.of(2015, 4, 10),
          sentence = 1,
          bookingId = 1
        )
      )
    )
    assertThat(result.finalRemand[0].days).isEqualTo(22)
  }
  @Test
  fun `Run calculation for a prisoner with multiple offences`() {
    val result = webTestClient.post()
      .uri("/relevant-remand/${PrisonApiExtension.MULTIPLE_OFFENCES_PRISONER}")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_MANAGE_DIGITAL_WARRANT")))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(RemandResult::class.java)
      .returnResult().responseBody!!

    assertThat(result).isEqualTo(
      RemandResult(
        remandPeriods = listOf(
          RemandPeriod(from = LocalDate.of(2019, 7, 6), to = LocalDate.of(2020, 9, 24), offenceDate = LocalDate.of(2019, 5, 1), offenceEndDate = LocalDate.of(2019, 5, 21), offenceCode = "MD71130C", offenceDescription = "An offence", chargeId = 3),
          RemandPeriod(from = LocalDate.of(2021, 6, 14), to = LocalDate.of(2021, 6, 14), offenceDate = LocalDate.of(2019, 5, 1), offenceEndDate = LocalDate.of(2019, 5, 21), offenceCode = "MD71130C", offenceDescription = "An offence", chargeId = 3),
          RemandPeriod(from = LocalDate.of(2019, 7, 6), to = LocalDate.of(2020, 9, 24), offenceDate = LocalDate.of(2019, 5, 1), offenceEndDate = LocalDate.of(2019, 7, 5), offenceCode = "MD71145C", offenceDescription = "An other offence", chargeId = 2),
          RemandPeriod(from = LocalDate.of(2019, 7, 6), to = LocalDate.of(2020, 9, 24), offenceDate = LocalDate.of(2019, 5, 1), offenceEndDate = LocalDate.of(2019, 5, 21), offenceCode = "MD71145C", offenceDescription = "An other offence", chargeId = 4),
          RemandPeriod(from = LocalDate.of(2021, 6, 14), to = LocalDate.of(2021, 6, 14), offenceDate = LocalDate.of(2019, 5, 1), offenceEndDate = LocalDate.of(2019, 5, 21), offenceCode = "MD71145C", offenceDescription = "An other offence", chargeId = 4)
        ),
        finalRemand = listOf(
          Remand(from = LocalDate.of(2019, 7, 6), to = LocalDate.of(2020, 9, 24), sentence = 1, bookingId = 2),
          Remand(from = LocalDate.of(2021, 6, 14), to = LocalDate.of(2021, 6, 14), sentence = 1, bookingId = 2)
        )
      )
    )
  }
}
