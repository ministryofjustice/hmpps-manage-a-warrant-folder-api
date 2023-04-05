package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model

import java.time.LocalDate

data class RelevantRemand(
  val from: LocalDate,
  val to: LocalDate,
  val days: Int,
  val sentenceSequence: Int
)
