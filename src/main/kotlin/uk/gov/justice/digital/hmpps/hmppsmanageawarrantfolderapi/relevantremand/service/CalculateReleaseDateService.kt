package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi.relevantremand.model.Remand
import java.time.LocalDate

@Service
class CalculateReleaseDateService {

// TODO
  fun calculateReleaseDate(remand: List<Remand>, date: LocalDate): LocalDate {
    return date
  }
}
