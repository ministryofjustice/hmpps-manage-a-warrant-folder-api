package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model

import java.time.LocalDate

data class RelevantRemandCalculationRequest(
  val relevantRemands: List<RelevantRemand>,
  val sentenceDate: LocalDate
)
