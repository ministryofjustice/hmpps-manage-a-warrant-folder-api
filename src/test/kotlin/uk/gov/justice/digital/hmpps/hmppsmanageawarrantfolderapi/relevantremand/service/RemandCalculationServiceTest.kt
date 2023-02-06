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
    fun `Error if there is more than one charge`() {
      assertThrows<UnsupportedCalculationException> {
        remandCalculationService.calculate(
          RemandCalculation(
            listOf(
              Charge(1, offence, 1, emptyList()),
              Charge(2, offence, 2, emptyList()),
            )
          )
        )
      }
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
      assertThat(result).isEmpty()
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
      assertThat(result).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result[0].days).isEqualTo(31)
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
      assertThat(result).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2022, 1, 31),
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result[0].days).isEqualTo(31)
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
      assertThat(result).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2021, 5, 13),
            LocalDate.of(2021, 5, 13),
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result[0].days).isEqualTo(1)
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
      assertThat(result).isEqualTo(
        listOf(
          Remand(
            LocalDate.of(2015, 3, 25),
            LocalDate.of(2015, 4, 8),
            SENTENCE_SEQUENCE
          )
        )
      )
      assertThat(result[0].days).isEqualTo(15)
    }
  }

  fun aCharge(courtDates: List<CourtDate>): List<Charge> {
    return listOf(Charge(1, offence, SENTENCE_SEQUENCE, courtDates))
  }
  companion object {
    val offence = Offence("1", "1")
    const val SENTENCE_SEQUENCE = 1
  }
}
