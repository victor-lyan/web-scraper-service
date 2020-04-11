package com.wictorlyan.webscraperservice.task

import com.wictorlyan.webscraperservice.service.CoronavirusService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CoronavirusCountryStatsTask(
    val coronavirusService: CoronavirusService
) {
    @Scheduled(
        initialDelayString = "\${coronavirus.scheduledTaskInitialDelay}", 
        fixedDelayString = "\${coronavirus.scheduledTaskFixedDelay}"
    )
    fun countryStatsTask() {
        coronavirusService.doCountryStatsScraping()
    }
}