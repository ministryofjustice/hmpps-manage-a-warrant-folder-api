package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.UnsupportedCalculationException
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Charge
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDate
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.CourtDateType
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Offence
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandCalculation
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.RemandPeriod
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
          aCharge(
            listOf(
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.STOP),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.CONTINUE),
            )
          )
        )
      )
      assertThat(result.finalRemand).isEmpty()
    }

    @Test
    fun `Remand is with start and final stop event`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          aCharge(
            listOf(
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.START),
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP, true)
            )
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(31)
    }

    @Test
    fun `Remand is with start and final stop event with court dates in the wrong order`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          aCharge(
            listOf(
              CourtDate(LocalDate.of(2022, 2, 1), CourtDateType.STOP, true),
              CourtDate(LocalDate.of(2022, 1, 1), CourtDateType.START)
            )
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(31)
    }
    @Test
    fun `Remand preprod example `() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          aCharge(
            listOf(
              CourtDate(LocalDate.of(2021, 5, 13), CourtDateType.START),
              CourtDate(LocalDate.of(2021, 5, 14), CourtDateType.STOP, true)
            )
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2021, 5, 13),
            LocalDate.of(2021, 5, 13),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(1)
    }
    @Test
    fun `Remand with bail (not final stop)`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          aCharge(
            listOf(
              CourtDate(LocalDate.of(2015, 3, 25), CourtDateType.START),
              CourtDate(LocalDate.of(2015, 4, 8), CourtDateType.STOP),
              CourtDate(LocalDate.of(2015, 9, 18), CourtDateType.STOP, true)

            )
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2015, 3, 25),
            LocalDate.of(2015, 4, 8),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(15)
    }
    @Test
    fun `Remand with related offences`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            Charge(
              1, offence, offenceDate, offenceEndDate, SENTENCE_SEQUENCE, BOOKING_ID, null,
              listOf(
                CourtDate(LocalDate.of(2020, 1, 5), CourtDateType.START)
              )
            ),
            Charge(
              2, offence, offenceDate, offenceEndDate, null, BOOKING_ID,null,
              listOf(
                CourtDate(LocalDate.of(2020, 1, 20), CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 19),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(15)
    }
    @Test
    fun `Remand with related offences no end date`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            Charge(
              1, offence, offenceDate, null, 1, BOOKING_ID, null,
              listOf(
                CourtDate(LocalDate.of(2020, 1, 5), CourtDateType.START)
              )
            ),
            Charge(
              2, offence, offenceDate, null, null, BOOKING_ID,null,
              listOf(
                CourtDate(LocalDate.of(2020, 1, 20), CourtDateType.STOP, final = true)
              )
            ),
          )
        )
      )
      assertThat(result.finalRemand).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 19),
            BOOKING_ID,
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result.finalRemand[0].days).isEqualTo(15)
    }

    @Test
    fun `Remand with multiple offences`() {
      val result = remandCalculationService.calculate(
        RemandCalculation(
          listOf(
            Charge(
              1, offence, offenceDate, offenceEndDate, SENTENCE_SEQUENCE, BOOKING_ID, null,
              listOf(
                CourtDate(date = LocalDate.of(2019, 7, 6), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 9, 24), type = CourtDateType.STOP),
                CourtDate(date = LocalDate.of(2021, 6, 14), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2021, 6, 15), type = CourtDateType.STOP, final = true)
              )
            ),
            Charge(
              2, unrelatedOffence, unrelatedOffenceDate, unrelatedOffenceEndDate, SECOND_SENTENCE_SEQUENCE, BOOKING_ID, null,
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
            RemandPeriod(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              offenceDate,
              offenceEndDate,
              offence.code,
              offence.description, null,
              1
            ),
            RemandPeriod(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              offenceDate,
              offenceEndDate,
              offence.code,
              offence.description, null,
              1
            ),
            RemandPeriod(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              unrelatedOffenceDate,
              unrelatedOffenceEndDate,
              unrelatedOffence.code,
              unrelatedOffence.description, null,
              2
            ),
            RemandPeriod(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              unrelatedOffenceDate,
              unrelatedOffenceEndDate,
              unrelatedOffence.code,
              unrelatedOffence.description, null,
              2
            )
          ),
          listOf(
            Remand(
              LocalDate.of(2019, 7, 6),
              LocalDate.of(2020, 9, 24),
              BOOKING_ID,
              SENTENCE_SEQUENCE
            ),
            Remand(
              LocalDate.of(2021, 6, 14),
              LocalDate.of(2021, 6, 14),
              BOOKING_ID,
              SENTENCE_SEQUENCE
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
            Charge(
              1, offence, offenceDate, offenceEndDate, SENTENCE_SEQUENCE, BOOKING_ID,null,
              listOf(
                CourtDate(date = LocalDate.of(2020, 1, 1), type = CourtDateType.START),
                CourtDate(date = LocalDate.of(2020, 3, 1), type = CourtDateType.STOP, final = true)
              )
            ),
            Charge(
              2, unrelatedOffence, unrelatedOffenceDate, unrelatedOffenceEndDate, SECOND_SENTENCE_SEQUENCE, BOOKING_ID,null,
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
            RemandPeriod(
              LocalDate.of(2020, 1, 1),
              LocalDate.of(2020, 2, 29),
              offenceDate,
              offenceEndDate,
              offence.code,
              offence.description, null,
              1
            ),
            RemandPeriod(
              LocalDate.of(2020, 2, 1),
              LocalDate.of(2020, 3, 31),
              unrelatedOffenceDate,
              unrelatedOffenceEndDate,
              unrelatedOffence.code,
              unrelatedOffence.description, null,
              2
            ),
          ),
          listOf(
            Remand(
              LocalDate.of(2020, 1, 1),
              LocalDate.of(2020, 1, 31),
              BOOKING_ID,
              SENTENCE_SEQUENCE
            ),
            Remand(
              LocalDate.of(2020, 2, 1),
              LocalDate.of(2020, 3, 31),
              BOOKING_ID,
              SECOND_SENTENCE_SEQUENCE
            )
          )
        )
      )

      assertThat(result.finalRemand.sumOf { it.days }).isEqualTo(91)
    }
  }

  fun aCharge(courtDates: List<CourtDate>): List<Charge> {
    return listOf(Charge(1, offence, offenceDate, offenceEndDate, SENTENCE_SEQUENCE, BOOKING_ID,null, courtDates))
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
