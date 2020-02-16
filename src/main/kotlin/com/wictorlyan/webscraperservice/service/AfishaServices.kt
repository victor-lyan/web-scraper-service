package com.wictorlyan.webscraperservice.service

import com.wictorlyan.webscraperservice.entity.AfishaCinema
import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.repository.AfishaCinemaMovieRepository
import com.wictorlyan.webscraperservice.repository.AfishaCinemaRepository
import com.wictorlyan.webscraperservice.repository.AfishaMovieRepository
import com.wictorlyan.webscraperservice.scraper.AfishaScraper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AfishaMovieService(
    val cinemaRepository: AfishaCinemaRepository,
    val movieRepository: AfishaMovieRepository,
    val cinemaMovieRepository: AfishaCinemaMovieRepository,
    val afishaScraper: AfishaScraper
) {
    val logger: Logger = LoggerFactory.getLogger(AfishaMovieService::class.java)
    
    @Transactional
    fun doDailyScraping() {
        logger.info("Daily movies schedule scraping started")
        try {
            val today = LocalDate.now()
            val todayString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val todayMovies = afishaScraper.scrapeMoviesForDate(todayString)
            if (todayMovies.isEmpty()) {
                logger.info("No movies found from Afisha")
                return
            }
            logger.info("${todayMovies.size} movies found today")

            // here we store all cinemas that we updated in order not to do unnecessary updates next times
            val updatedCinemas = mutableMapOf<String, AfishaCinema>()
            for ((_, groupedByNameList) in todayMovies) {
                // all movies in the groupedByNameList are the same, that's why we update the first one and
                // use it's ID next
                val currentMovieId = movieRepository.save(groupedByNameList[0].movie)
                groupedByNameList.forEach {
                    val currentCinemaId: Int
                    if (!updatedCinemas.containsKey(it.cinema.name)) {
                        currentCinemaId = cinemaRepository.save(it.cinema)
                        updatedCinemas[it.cinema.name] = it.cinema
                    } else {
                        currentCinemaId = updatedCinemas[it.cinema.name]!!.id
                    }
                    
                    it.cinema.id = currentCinemaId
                    it.movie.id = currentMovieId
                }
                
                // here we remove records for movie and date and do batch insert
                cinemaMovieRepository.removeAllForMovieAndDate(currentMovieId, today)
                cinemaMovieRepository.saveAll(groupedByNameList)
            }
        } catch (e: Exception) {
            logger.error("Some error occurred in doDailyScraping method: ${e.message}")
        }

        logger.info("Daily movies schedule scraping finished")
    }

    fun getMoviesForToday(): List<AfishaMovie> {
        val today = LocalDate.now()
        return movieRepository.findByDate(today)
    }

    fun getMovieWithCinemas(id: Int, date: String?): AfishaMovie? {
        return movieRepository.findById(id, true, date)
    }
}