package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.ChargeAndEvents
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Offence
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandResult
import java.time.LocalDate

class RemandCalculationServiceTest {
  private val remandCalculationService = RemandCalculationService()

  @Nested
  inner class Calculations {
    @Test
    fun `Error if there is no charges`() {
      assertThrows<UnsupportedCalculationException> { remandCalculationService.calculate(RemandCalculation(emptyList())) }
    }

    @Test
    fun `No remand calculated if there are no start events`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          chargeAndEvents(
            listOf(
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.STOP),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.CONTINUE),
            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEmpty()
    }

    @Test
    fun `Remand is with start and final stop event`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          chargeAndEvents(
            listOf(
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.START),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP, true)
            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            aCharge()
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(31)
    }

    @Test
    fun `Remand is with start and final stop event with court dates in the wrong order`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          chargeAndEvents(
            listOf(
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP, true),
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.START)
            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            aCharge()
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(31)
    }

    @Test
    fun `Remand preprod example `() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          chargeAndEvents(
            listOf(
              CourtDate(LocalDate.of(2021, 5, 13), CourtDateType.START),
              CourtDate(LocalDate.of(2021, 5, 14), CourtDateType.STOP, true)
            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2021, 5, 13),
            LocalDate.of(2021, 5, 13),
            aCharge()
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(1)
    }

    @Test
    fun `Remand with bail (not final stop)`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          chargeAndEvents(
            listOf(
              CourtDate(LocalDate.of(2015, 3, 25), CourtDateType.START),
              CourtDate(LocalDate.of(2015, 4, 8), CourtDateType.STOP),
              CourtDate(LocalDate.of(2015, 9, 18), CourtDateType.STOP, true)

            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2015, 3, 25),
            LocalDate.of(2015, 4, 8),
            aCharge()
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(15)
    }

    @Test
    fun `Remand with related offences`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            ChargeAndEvents(
              aCharge(),
              listOf(
                CourtDate(LocalDate.of(2020, 1, 5), CourtDateType.START)
              )
            ),
            ChargeAndEvents(
              aCharge().copy(chargeId = 2),
              listOf(
                CourtDate(LocalDate.of(2020, 1, 20), CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 19),
            aCharge()
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(15)
    }

    @Test
    fun `Remand with related offences no end date`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            ChargeAndEvents(
              aCharge().copy(offenceEndDate = null),
              listOf(
                CourtDate(LocalDate.of(2020, 1, 5), CourtDateType.START)
              )
            ),
            ChargeAndEvents(
              aCharge().copy(chargeId = 2, offenceEndDate = null),
              listOf(
                CourtDate(LocalDate.of(2020, 1, 20), CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 19),
            aCharge().copy(offenceEndDate = null)
          )
        )
      )
      assertThat(result.sentenceRemand[0].days).isEqualTo(15)
    }

    @Test
    fun `Remand with multiple offences`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            ChargeAndEvents(
              aCharge(),
              listOf(
                CourtDate(date = LocalDate.of(2019, 7, 6), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 9, 24), type = CourtDateType.STOP),
                CourtDate(date = LocalDate.of(2021, 6, 14), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2021, 6, 15), type = CourtDateType.STOP, final = true)
              )
            ),
            ChargeAndEvents(
              anUnrelatedCharge(),
              listOf(
                CourtDate(date = LocalDate.of(2019, 7, 6), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 9, 24), type = CourtDateType.STOP),
                CourtDate(date = LocalDate.of(2021, 6, 14), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2021, 6, 15), type = CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result).isEqualTo(
        RemandResult(
          listOf(
            Remand(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              aCharge()
            ),
            Remand(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              aCharge()
            ),
            Remand(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              anUnrelatedCharge()
            ),
            Remand(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              anUnrelatedCharge()
            )
          ),
          listOf(
            Remand(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              aCharge()
            ),
            Remand(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              aCharge()
            )
          )
        )
      )
    }

    @Test
    fun `Remand with overlapping periods`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            ChargeAndEvents(
              aCharge(),
              listOf(
                CourtDate(date = LocalDate.of(2020, 1, 1), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 3, 1), type = CourtDateType.STOP, final = true)
              )
            ),
            ChargeAndEvents(
              anUnrelatedCharge(),
              listOf(
                CourtDate(date = LocalDate.of(2020, 2, 1), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 4, 1), type = CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result).isEqualTo(
        RemandResult(
          listOf(
            Remand(
              LocalDate.of(2020, 1, 1),
              LocalDate.of(2020, 2, 29),
              aCharge()
            ),
            Remand(
              LocalDate.of(2020, 2, 1),
              LocalDate.of(2020, 3, 31),
              anUnrelatedCharge()
            ),
          ),
          listOf(
            Remand(
              LocalDate.of(2020, 1, 1),
              LocalDate.of(2020, 1, 31),
              aCharge()
            ),
            Remand(
              LocalDate.of(2020, 2, 1),
              LocalDate.of(2020, 3, 31),
              anUnrelatedCharge()
            )
          )
        )
      )

      assertThat(result.sentenceRemand.sumOf { it.days }).isEqualTo(91)
    }

    @Test
    fun `Remand with overlapping periods where some have no sentence sequence`() {
      val firstCharge =
        Charge(
          chargeId = 2,
          offence = offence,
          offenceDate = LocalDate.of(2022, 11, 9),
          offenceEndDate = null,
          sentenceSequence = SENTENCE_SEQUENCE,
          bookingId = 2,
          courtCaseRef = "ABC"
        )
      val secondCharge =
        Charge(
          chargeId = 4,
          offence = offence,
          offenceDate = LocalDate.of(2022, 11, 2),
          offenceEndDate = null,
          sentenceSequence = SECOND_SENTENCE_SEQUENCE,
          bookingId = 2,
          courtCaseRef = "ABC"
        )
      val result = remandCalculationService.calculate(
        RemandCalculation(
          charges = listOf(
            ChargeAndEvents(
              Charge(
                chargeId = 1,
                offence = offence,
                offenceDate = LocalDate.of(2022, 11, 2),
                offenceEndDate = null,
                sentenceSequence = null,
                bookingId = 1,
                courtCaseRef = "ABC"
              ),
              listOf(
                CourtDate(date = LocalDate.of(2022, 11, 4), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2022, 11, 7), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2022, 11, 9), type = CourtDateType.STOP)
              )
            ),
            ChargeAndEvents(
              firstCharge,
              listOf(
                CourtDate(date = LocalDate.of(2022, 11, 11), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2022, 11, 18), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2022, 12, 16), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2023, 1, 4), type = CourtDateType.STOP, final = true)
              ),
            ),
            ChargeAndEvents(
              Charge(
                chargeId = 3,
                offence = offence,
                offenceDate = LocalDate.of(2022, 10, 26),
                offenceEndDate = null,
                sentenceSequence = null,
                bookingId = 2,
                courtCaseRef = "ABC"
              ),
              listOf(
                CourtDate(date = LocalDate.of(2022, 12, 16), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2023, 1, 4), type = CourtDateType.STOP, final = true)
              ),
            ),
            ChargeAndEvents(
              secondCharge,
              listOf(CourtDate(date = LocalDate.of(2023, 1, 4), type = CourtDateType.STOP, final = true))
            )
          )
        )
      )
      assertThat(result.sentenceRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 11, 4),
            LocalDate.of(2022, 11, 9),
            secondCharge
          ),
          Remand(
            LocalDate.of(2022, 11, 11),
            LocalDate.of(2023, 1, 3),
            firstCharge
          )
        )
      )

      assertThat(result.sentenceRemand.sumOf { it.days }).isEqualTo(60)
    }
  }

  fun chargeAndEvents(courtDates: List<CourtDate>): List<ChargeAndEvents> {
    return listOf(ChargeAndEvents(aCharge(), courtDates))
  }

  fun aCharge(): Charge {
    return Charge(1, offence, offenceDate, offenceEndDate, SENTENCE_SEQUENCE, BOOKING_ID, null)
  }

  fun anUnrelatedCharge(): Charge {
    return Charge(
      2,
      unrelatedOffence,
      unrelatedOffenceDate,
      unrelatedOffenceEndDate,
      SECOND_SENTENCE_SEQUENCE,
      BOOKING_ID,
      null
    )
  }

  companion object {
    val offence = Offence("1", "1", "An offence")
    val unrelatedOffence = Offence("2", "2", "An unrelated offence")
    val offenceDate = LocalDate.of(2000, 1, 1)
    val offenceEndDate = LocalDate.of(2000, 1, 1)
    val unrelatedOffenceDate = LocalDate.of(2001, 1, 1)
    val unrelatedOffenceEndDate = LocalDate.of(2001, 1, 1)
    const val SENTENCE_SEQUENCE = 1
    const val SECOND_SENTENCE_SEQUENCE = 2
    const val BOOKING_ID = 1L
  }
}
