package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.calculatereleasedatesapi.model

import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Sentence

data class RelevantRemandCalculationRequest(
  val relevantRemands: List<RelevantRemand>,
  val sentence: Sentence
)
