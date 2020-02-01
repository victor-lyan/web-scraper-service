package com.wictorlyan.webscraperservice.task

import com.wictorlyan.webscraperservice.service.AfishaMovieService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AfishaMoviesTask(
    val movieService: AfishaMovieService
) {
    @Scheduled(initialDelay = 300, fixedDelay = 3_600_000)
    fun dailyScrapingTask() {
        movieService.doDailyScraping()
    }
}