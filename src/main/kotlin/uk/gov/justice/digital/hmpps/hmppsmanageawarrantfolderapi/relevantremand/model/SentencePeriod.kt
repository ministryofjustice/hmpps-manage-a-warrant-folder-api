package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate

data class SentencePeriod(
  override val from: LocalDate,
  override val to: LocalDate
) : Period
