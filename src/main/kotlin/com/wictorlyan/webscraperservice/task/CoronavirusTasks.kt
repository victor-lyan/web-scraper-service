package com.wictorlyan.webscraperservice.task

import com.wictorlyan.webscraperservice.service.CoronavirusService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CoronavirusCountryStatsTask(
    val coronavirusService: CoronavirusService
) {
    @Scheduled(initialDelay = 1000, fixedDelay = 1_800_000)
    fun countryStatsTask() {
        coronavirusService.doCountryStatsScraping()
    }
}