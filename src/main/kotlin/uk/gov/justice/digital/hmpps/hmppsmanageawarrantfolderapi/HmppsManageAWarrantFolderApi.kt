package uk.gov.justice.digital.hmpps.hmppsmanageawarrantfolderapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
class HmppsManageAWarrantFolderApi

fun main(args: Array<String>) {
  runApplication<HmppsManageAWarrantFolderApi>(*args)
}
