package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model

data class RemandResult(
  val chargeRemand: List<Remand>,
  val sentenceRemand: List<Remand>
)
