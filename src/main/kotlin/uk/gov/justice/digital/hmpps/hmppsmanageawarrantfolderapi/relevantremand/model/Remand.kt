package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

import java.time.LocalDate

data class Remand(
  override val from: LocalDate,
  override val to: LocalDate,
  val charge: Charge
) : Period
