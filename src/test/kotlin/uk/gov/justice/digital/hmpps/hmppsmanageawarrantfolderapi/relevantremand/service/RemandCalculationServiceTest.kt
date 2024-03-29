package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.TestUtil
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.service.CalculateReleaseDateService
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandResult
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Sentence

class RemandCalculationServiceTest {
  private val calculateReleaseDateService = mock<CalculateReleaseDateService>()
  private val sentenceRemandService = SentenceRemandService(calculateReleaseDateService)
  private val remandCalculationService = RemandCalculationService(sentenceRemandService)

  @ParameterizedTest
  @CsvFileSource(resources = ["/data/tests.csv"], numLinesToSkip = 1)
  fun `Test Examples`(exampleName: String, error: String?) {
    log.info("Testing example $exampleName")

    val example = TestUtil.objectMapper().readValue(ClassPathResource("/data/RemandCalculation/$exampleName.json").file, TestExample::class.java)

    example.releaseDates.forEach {
      log.info("Stubbing release dates for $exampleName: $it")
      whenever(
        calculateReleaseDateService.calculateReleaseDate(
          eq(example.remandCalculation.prisonerId),
          any(),
          eq(Sentence(it.sentenceSequence, it.sentenceAt, it.bookingId))
        )
      ).thenReturn(it.release)
    }

    val remandResult: RemandResult
    try {
      remandResult = remandCalculationService.calculate(example.remandCalculation)
    } catch (e: Exception) {
      if (!error.isNullOrEmpty()) {
        Assertions.assertEquals(error, e.javaClass.simpleName)
        return
      } else {
        throw e
      }
    }

    val expected = TestUtil.objectMapper().readValue(ClassPathResource("/data/RemandResult/$exampleName.json").file, RemandResult::class.java)
    assertThat(remandResult).isEqualTo(expected)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
