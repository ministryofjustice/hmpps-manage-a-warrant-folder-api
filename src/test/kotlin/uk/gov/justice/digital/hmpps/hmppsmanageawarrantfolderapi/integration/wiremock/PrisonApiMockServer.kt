package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/*
    This class mocks the prison-api.
 */
class PrisonApiExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {
  companion object {
    @JvmField
    val prisonApi = PrisonApiMockServer()
    const val IMPRISONED_PRISONER = "IMP"
    const val BAIL_PRISONER = "BAIL"
  }
  override fun beforeAll(context: ExtensionContext) {
    prisonApi.start()
    prisonApi.stubImprisonedCourtCaseResults()
    prisonApi.stubBailPrisoner()
  }

  override fun beforeEach(context: ExtensionContext) {
    prisonApi.resetRequests()
  }

  override fun afterAll(context: ExtensionContext) {
    prisonApi.stop()
  }
}

class PrisonApiMockServer : WireMockServer(WIREMOCK_PORT) {
  companion object {
    private const val WIREMOCK_PORT = 8332
  }

  fun stubImprisonedCourtCaseResults(): StubMapping =
    stubFor(
      get("/api/digital-warrant/court-date-results/${PrisonApiExtension.IMPRISONED_PRISONER}")
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              [
                 {
                    "id":1,
                    "date":"2022-10-13",
                    "resultCode":"4531",
                    "resultDescription":"Remand in Custody (Bail Refused)",
                    "charge":{
                       "chargeId":1,
                       "offenceCode":"SX03163A",
                       "offenceStatue":"SX03",
                       "offenceDate":"2021-05-05",
                       "guilty":false,
                       "courtCaseId":1,
                       "sentenceSequence":1
                    },
                    "bookingId":1
                 },
                 {
                    "id":2,
                    "date":"2022-12-13",
                    "resultCode":"1002",
                    "resultDescription":"Imprisonment",
                    "charge":{
                       "chargeId":1,
                       "offenceCode":"SX03163A",
                       "offenceStatue":"SX03",
                       "offenceDate":"2021-05-05",
                       "guilty":false,
                       "courtCaseId":1,
                       "sentenceSequence":1
                    },
                    "bookingId":1
                 }
              ]
              """.trimIndent()
            )
            .withStatus(200)
        )
    )
  fun stubBailPrisoner(): StubMapping =
    stubFor(
      get("/api/digital-warrant/court-date-results/${PrisonApiExtension.BAIL_PRISONER}")
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              [
                 {
                    "id":1,
                    "date":"2015-03-25",
                    "resultCode":"4565",
                    "resultDescription":"Commit to Crown Court for Trial (Summary / Either Way Offences)",
                    "resultDispositionCode": "I",
                    "charge":{
                       "chargeId":1,
                       "offenceCode":"SX03163A",
                       "offenceStatue":"SX03",
                       "offenceDate":"2021-05-05",
                       "guilty":false,
                       "courtCaseId":1,
                       "sentenceSequence":1
                    },
                    "bookingId":1
                 },
                 {
                    "id":2,
                    "date":"2015-04-08",
                    "resultCode":"4530",
                    "resultDescription":"Remand on Conditional Bail",
                    "resultDispositionCode": "I",
                    "charge":{
                       "chargeId":1,
                       "offenceCode":"SX03163A",
                       "offenceStatue":"SX03",
                       "offenceDate":"2021-05-05",
                       "guilty":false,
                       "courtCaseId":1,
                       "sentenceSequence":1
                    },
                    "bookingId":1
                 },
                 {
                    "id":3,
                    "date":"2015-09-18",
                    "resultCode":"1002",
                    "resultDescription":"Imprisonment",
                    "resultDispositionCode": "F",
                    "charge":{
                       "chargeId":1,
                       "offenceCode":"SX03163A",
                       "offenceStatue":"SX03",
                       "offenceDate":"2021-05-05",
                       "guilty":false,
                       "courtCaseId":1,
                       "sentenceSequence":1
                    },
                    "bookingId":1
                 }
              ]
              """.trimIndent()
            )
            .withStatus(200)
        )
    )
}
