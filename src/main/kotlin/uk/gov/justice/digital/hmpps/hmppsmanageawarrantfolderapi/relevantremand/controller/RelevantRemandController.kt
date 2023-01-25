package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.service.PrisonService
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.prisonapi.transform.transform
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service.RemandCalculationService

@RestController
@RequestMapping("/relevant-remand", produces = [MediaType.APPLICATION_JSON_VALUE])
// @Tag(name = "calculation-controller", description = "Operations involving a calculation")
class RelevantRemandController(
  private val remandCalculationService: RemandCalculationService,
  private val prisonService: PrisonService
) {
  @PostMapping(value = ["/{prisonerId}"])
//  @PreAuthorize("hasAnyRole('SYSTEM_USER', 'RELEASE_DATES_CALCULATOR')")
  @ResponseBody
//  @Operation(
//    summary = "Calculate release dates for a prisoner - preliminary calculation, this does not publish to NOMIS",
//    description = "This endpoint will calculate release dates based on a prisoners latest booking - this is a " +
//      "PRELIMINARY calculation that will not be published to NOMIS"
//  )
//  @ApiResponses(
//    value = [
//      ApiResponse(responseCode = "200", description = "Returns calculated dates"),
//      ApiResponse(responseCode = "401", description = "Unauthorised, requires a valid Oauth2 token"),
//      ApiResponse(responseCode = "403", description = "Forbidden, requires an appropriate role")
//    ]
//  )
  fun calculate(
//    @Parameter(required = true, example = "A1234AB", description = "The prisoners ID (aka nomsId)")
    @PathVariable("prisonerId")
    prisonerId: String
  ): List<Remand> {
    log.info("Request received to calculate relevant remand for $prisonerId")
    val courtDateResults = prisonService.getCourtDateResults(prisonerId)
    return remandCalculationService.calculate(transform(courtDateResults))
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
