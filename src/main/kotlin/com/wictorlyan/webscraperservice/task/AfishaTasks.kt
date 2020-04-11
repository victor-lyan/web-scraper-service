package com.wictorlyan.webscraperservice.task

import com.wictorlyan.webscraperservice.service.AfishaMovieService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AfishaMoviesTask(
    val movieService: AfishaMovieService
) {
    /*@Scheduled(
        initialDelayString = "\${afisha.scheduledTaskInitialDelay}", 
        fixedDelayString = "\${afisha.scheduledTaskFixedDelay}"
    )
    fun dailyScrapingTask() {
        val today = LocalDate.now()
        movieService.doDailyScraping(today)
        movieService.doDailyScraping(today.plusDays(1))
        movieService.doDailyScraping(today.plusDays(2))
        movieService.doDailyScraping(today.plusDays(3))
        movieService.doDailyScraping(today.plusDays(4))
        movieService.doDailyScraping(today.plusDays(5))
        movieService.doDailyScraping(today.plusDays(6))
        movieService.updateMoviesLinksAndImages()
    }*/
}