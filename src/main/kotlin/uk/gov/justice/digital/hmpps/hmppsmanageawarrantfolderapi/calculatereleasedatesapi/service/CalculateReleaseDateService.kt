package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationRequest
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationResult
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Sentence
import java.time.LocalDate

@Service
class CalculateReleaseDateService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient
) {

  fun calculateReleaseDate(prisonerId: String, remand: List<Remand>, sentence: Sentence): LocalDate {
    log.info("Calculating release dates for $sentence")
    val request = RelevantRemandCalculationRequest(
      remand.map { RelevantRemand(it.from, it.to, it.days.toInt(), it.charge.sentenceSequence!!) },
      sentence
    )
    val result: RelevantRemandCalculationResult
    try {
      result = calculateReleaseDatesApiClient.calculateReleaseDates(prisonerId, request)
    } catch (e: Exception) {
      throw UnsupportedCalculationException("Error calling CRD service $sentence", e)
    }
    if (result.validationMessages.isNotEmpty()) {
      throw UnsupportedCalculationException(
        "Validation error from calling CRD service $sentence, \n ${
        result.validationMessages.joinToString(
          separator = "\n"
        ) { it.message }
        }"
      )
    }
    return result.releaseDate!!
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
