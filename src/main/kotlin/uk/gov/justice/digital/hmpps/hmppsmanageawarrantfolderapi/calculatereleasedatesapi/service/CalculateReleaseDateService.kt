package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationRequest
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Sentence
import java.time.LocalDate

@Service
class CalculateReleaseDateService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient
) {

  fun calculateReleaseDate(prisonerId: String, remand: List<Remand>, sentence: Sentence): LocalDate {
    val request = RelevantRemandCalculationRequest(
      remand.map { RelevantRemand(it.from, it.to, it.days.toInt(), it.charge.sentenceSequence!!) },
      sentence
    )
    return calculateReleaseDatesApiClient.calculateReleaseDates(prisonerId, request).releaseDate
  }
}
