package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model.RelevantRemandCalculationRequest
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import java.time.LocalDate

@Service
class CalculateReleaseDateService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient
) {

  fun calculateReleaseDate(prisonerId: String, remand: List<Remand>, date: LocalDate): LocalDate {
    val request = RelevantRemandCalculationRequest(
      remand.map { RelevantRemand(it.from, it.to, it.days.toInt(), it.charge.sentenceSequence!!) },
      date
    )
    return calculateReleaseDatesApiClient.calculateReleaseDates(prisonerId, request).releaseDate
  }
}
