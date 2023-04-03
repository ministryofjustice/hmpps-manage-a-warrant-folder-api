package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.SentencePeriod
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.SentenceRemandLoopTracker

@Service
class SentenceRemandService(
  private val calculateReleaseDateService: CalculateReleaseDateService
) {

  fun extractSentenceRemand(remandPeriods: List<Remand>): List<Remand> {
    val loopTracker = SentenceRemandLoopTracker(remandPeriods)
    for (entry in loopTracker.sentenceDateToPeriodMap.entries.sortedBy { it.key }) {
      loopTracker.startNewSentenceDateLoop(entry)
      var current: Remand? = null
      for (date in loopTracker.importantDates) {
        if (loopTracker.shouldCalculateAReleaseDate(date)) {
          log.info("calculating release date for sentence on date $date")
          val sentenceReleaseDate = calculateReleaseDateService.calculateReleaseDate(loopTracker.final, date)
          loopTracker.periodsServingSentence.add(SentencePeriod(date, sentenceReleaseDate))
        }
        val next = loopTracker.findNextPeriod(date)

        // Should we start a new period at this date?
        if (!loopTracker.doesDateIntersectWithEstablishedRemandOrSentence(date)) {
          if (next?.from == date) {
            // New period starting from its start date.
            if (current != null) {
              loopTracker.final.add(current.copy(to = date.minusDays(1)))
            }
            current = next
          }
          if (current == null && loopTracker.open.isNotEmpty()) {
            // New period starting from the end of another period.
            current = loopTracker.open.first().copy(from = date.plusDays(1))
          }
        }

        // Should the current period be closed?
        if (loopTracker.shouldCloseCurrentPeriod(date, current)) {
          // Period being closed by another period
          loopTracker.final.add(current!!.copy(to = date.minusDays(1)))
          current = null
        }
        if (current?.to == date) {
          // Period being closed by its end date.
          loopTracker.final.add(current)
          current = loopTracker.open.firstOrNull()
        }
      }
    }
    return loopTracker.final
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}