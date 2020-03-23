package com.wictorlyan.webscraperservice.service

import com.wictorlyan.webscraperservice.entity.AfishaCinema
import com.wictorlyan.webscraperservice.entity.AfishaCinemaMovie
import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.property.AfishaProperties
import com.wictorlyan.webscraperservice.repository.AfishaCinemaMovieRepository
import com.wictorlyan.webscraperservice.repository.AfishaCinemaRepository
import com.wictorlyan.webscraperservice.repository.AfishaMovieRepository
import com.wictorlyan.webscraperservice.scraper.AfishaScraper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AfishaMovieService(
    val cinemaRepository: AfishaCinemaRepository,
    val movieRepository: AfishaMovieRepository,
    val cinemaMovieRepository: AfishaCinemaMovieRepository,
    val afishaScraper: AfishaScraper,
    val afishaProperties: AfishaProperties
) {
    val logger: Logger = LoggerFactory.getLogger(AfishaMovieService::class.java)
    
    @Transactional
    fun doDailyScraping(date: LocalDate) {
        logger.info("Daily movies schedule scraping started for date: $date")
        try {
            val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val moviesForDate = afishaScraper.scrapeMoviesForDate(dateString)
            if (moviesForDate.isEmpty()) {
                logger.info("No movies found from Afisha")
                return
            }
            logger.info("${moviesForDate.size} movies found for date: $dateString")

            processMovies(moviesForDate, date)
        } catch (e: Exception) {
            logger.error("Some error occurred in doDailyScraping method: ${e.message}")
        }

        logger.info("Daily movies schedule scraping finished for date: $date")
    }

    private fun processMovies(moviesForDate: Map<String, List<AfishaCinemaMovie>>, date: LocalDate) {
        // here we store all cinemas that we updated in order not to do unnecessary updates next times
        val updatedCinemas = mutableMapOf<String, AfishaCinema>()
        for ((_, groupedByNameList) in moviesForDate) {
            // all movies in the groupedByNameList are the same, that's why we update the first one and
            // use it's ID next
            val currentMovieId = movieRepository.save(groupedByNameList[0].movie)
            groupedByNameList.forEach {
                val currentCinemaId: Int
                if (!updatedCinemas.containsKey(it.cinema.name)) {
                    it.cinema.modifiedDate = Instant.now()
                    currentCinemaId = cinemaRepository.save(it.cinema)
                    updatedCinemas[it.cinema.name] = it.cinema
                } else {
                    currentCinemaId = updatedCinemas[it.cinema.name]!!.id
                }

                it.cinema.id = currentCinemaId
                it.movie.id = currentMovieId
            }

            // here we remove records for movie and date and do batch insert
            cinemaMovieRepository.removeAllForMovieAndDate(currentMovieId, date)
            cinemaMovieRepository.saveAll(groupedByNameList)
        }
    }

    fun getMoviesForDate(date: String?): List<AfishaMovie> {
        val dateObject = try {
            LocalDate.parse(date)
        } catch (e: Exception) {
            // by default use today
            LocalDate.now()
        }
        return movieRepository.findByDate(dateObject)
    }

    fun getMovieWithCinemas(id: Int, date: String?): AfishaMovie? {
        return movieRepository.findByIdOrName(id, withCinemas = true, date = date)
    }

    fun getMovieWithCinemas(name: String, date: String?): AfishaMovie? {
        return movieRepository.findByIdOrName(name = name, withCinemas = true, date = date)
    }
    
    @Transactional
    fun updateMoviesLinksAndImages() {
        logger.info("Update movies and links started")
        val moviesForUpdate = movieRepository.getMoviesForUpdate()
        logger.info("Found ${moviesForUpdate.size} movies for update")
        
        moviesForUpdate.forEach {
            val imageAndLink = afishaScraper.scrapeMovieImageAndLink(it)

            // we update movie link only if it contains wrong part
            if (it.link.contains(afishaProperties.movies.wrongLinkPart)) {
                it.link = "${afishaProperties.baseUrl}${imageAndLink.second}"
            }
            it.image = imageAndLink.first
            movieRepository.save(it)
        }
        logger.info("Update movies and links finished")
    }
}