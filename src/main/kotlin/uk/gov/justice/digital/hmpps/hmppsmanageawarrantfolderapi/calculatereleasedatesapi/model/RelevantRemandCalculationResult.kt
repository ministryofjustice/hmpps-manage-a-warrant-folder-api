package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model

import java.time.LocalDate

data class RelevantRemandCalculationResult(
  val releaseDate: LocalDate?,
  val validationMessages: List<CalculateReleaseDatesValidationMessage> = emptyList()
)
