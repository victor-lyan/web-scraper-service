package com.wictorlyan.webscraperservice

import com.wictorlyan.webscraperservice.scraper.AfishaScraper
import com.wictorlyan.webscraperservice.service.AfishaMovieService
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class WebScrappingComponent(
    val afishaScraper: AfishaScraper,
    val movieService: AfishaMovieService
) {
    @PostConstruct
    fun test() {
        movieService.doDailyScraping()

        //movieService.doTest()
    }
}