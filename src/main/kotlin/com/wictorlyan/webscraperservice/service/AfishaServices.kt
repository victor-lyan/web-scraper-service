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
        val today = LocalDate.now()
        val todayString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val todayMovies = afishaScraper.scrapeMoviesForDate(todayString)
        if (todayMovies.isEmpty()) {
            logger.info("No movies found from Afisha")
            return
        }
        
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
        
        logger.info("Daily movies schedule scraping finished")
    }
    
    @Transactional
    fun doTest() {
        val cinema1 = AfishaCinema("Premier Hall", "http://link1.org", "http://link1.com")
        val cinema2 = AfishaCinema("Compass Cinema", "http://link2.org", "http://link33.com")
        cinemaRepository.save(cinema1)
        cinemaRepository.save(cinema2)

        cinemaRepository.findAll().forEach {println(it)}

        /*val movie1 = AfishaMovie("South Park", "horror", "http://example.com")
        val movie2 = AfishaMovie("Witcher", "adventure", "http://witcher-sucks.com")
        movie1.addCinema(cinema1, LocalDate.parse("2020-01-01"), LocalTime.parse("15:00"), "3D")
        movie1.addCinema(cinema2, LocalDate.parse("2020-01-01"), LocalTime.parse("10:00"), "3D")
        movie2.addCinema(cinema2, LocalDate.parse("2020-01-03"), LocalTime.parse("20:00"))
        movieRepository.saveAll(listOf(movie1, movie2))*/
    }

    fun getMoviesForToday(): List<AfishaMovie> {
        val today = LocalDate.now()
        return movieRepository.findByDate(today)
    }
}