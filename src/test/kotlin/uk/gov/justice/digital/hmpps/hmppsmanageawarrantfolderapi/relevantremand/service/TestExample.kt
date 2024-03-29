package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import java.time.LocalDate

data class TestExample(
  val remandCalculation: RemandCalculation,
  val releaseDates: List<ReleaseDates> = listOf()
)

data class ReleaseDates(
  val bookingId: Long,
  val sentenceSequence: Int,
  val sentenceAt: LocalDate,
  val release: LocalDate,
)
