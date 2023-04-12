package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand

class UnsupportedCalculationException(
  override val message: String,
  override val cause: Throwable? = null
) : Exception(message, cause)
