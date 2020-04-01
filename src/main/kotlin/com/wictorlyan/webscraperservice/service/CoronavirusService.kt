package com.wictorlyan.webscraperservice.service

import com.wictorlyan.webscraperservice.entity.CoronavirusCountryStats
import com.wictorlyan.webscraperservice.repository.CoronavirusRepository
import com.wictorlyan.webscraperservice.scraper.CoronavirusScraper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CoronavirusService(
    val scraper: CoronavirusScraper,
    val repository: CoronavirusRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AfishaMovieService::class.java)
    
    fun getDataForCountry(country: String): CoronavirusCountryStats? {
        return repository.getDataForCountry(country)
    }
    
    fun doCountryStatsScraping() {
        logger.info("Country stats scraping started")

        val worldStats = scraper.scrapeCountryStats()
        repository.updateCountryStats(worldStats)

        logger.info("Country stats scraping finished")
    }
}