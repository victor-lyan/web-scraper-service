package com.wictorlyan.webscraperservice.scraper

import com.wictorlyan.webscraperservice.entity.AfishaCinema
import com.wictorlyan.webscraperservice.entity.AfishaCinemaMovie
import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.exception.AfishaMoviesNotFoundException
import com.wictorlyan.webscraperservice.property.AfishaProperties
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime

@Component
class AfishaScraper(
    val afishaProperties: AfishaProperties
) {
    fun scrapeMoviesForDate(date: String): Map<String, List<AfishaCinemaMovie>> {
        val result: MutableMap<String, MutableList<AfishaCinemaMovie>> = mutableMapOf()
        val url = "${afishaProperties.movies.baseUrl}?date=$date"
        val document = Jsoup.connect(url).get()
        val table = document.selectFirst(".schedule table")
            ?: throw AfishaMoviesNotFoundException("No movies found for date: $date")
        val currentMovies = mutableListOf<AfishaMovie>()

        table.select("tbody tr:not(.day)").forEach { row ->
            val timeCell = row.selectFirst("td.time")
            if (timeCell.selectFirst("small.lg")?.html() != afishaProperties.movies.skipMoviesCriteria) {
                val titleCell = row.selectFirst("td.title")
                if (titleCell != null) {
                    // this row contains cell with title links
                    // first thing we do - reset currentMovies list
                    currentMovies.clear()
                    val titles = titleCell.select("a")
                    val genres = titleCell.select("span.genre")
                    
                    titles.forEachIndexed {index, title -> 
                        val movieTitle = title.html()
                        val movieLink = afishaProperties.baseUrl + title.attr("href")
                        val movieGenre = genres[index].html()
                        val afishaMovie = AfishaMovie(movieTitle, movieGenre, movieLink)
                        currentMovies.add(afishaMovie)
                    }
                }

                val format = row.selectFirst("td.format")?.html()
                val placeCell = row.selectFirst("td.place")
                val cinemaLink = afishaProperties.baseUrl + placeCell.selectFirst("a").attr("href")
                val cinemaName = placeCell.selectFirst("a").html()
                val afishaCinema = AfishaCinema(cinemaName, cinemaLink)
                
                currentMovies.forEach {currentMovie ->
                    // iterate over time sections
                    timeCell.select("span:not(.i-double)").forEach {
                        val afishaCinemaMovie = AfishaCinemaMovie(
                            afishaCinema, 
                            currentMovie, 
                            LocalDate.parse(date), 
                            LocalTime.parse(it?.text()),
                            if (format != "") "3D" else ""
                        )

                        val resultItem = result[currentMovie.name]
                        if (resultItem == null) {
                            result[currentMovie.name] = mutableListOf(afishaCinemaMovie)
                        } else {
                            resultItem.add(afishaCinemaMovie)
                        }
                    }
                }                
            }
        }

        return result
    }
    
    fun scrapeMovieImageAndLink(movie: AfishaMovie): Pair<String, String> {
        val document = Jsoup.connect(movie.link).get()
        val image = document.selectFirst(".image img")
        val imageUrl = image.attr("src")
        val linksBlock = document.selectFirst(".block_grey .links")
        val descriptionLink = linksBlock.selectFirst("li:last-child a").attr("href")
        
        return Pair(imageUrl, descriptionLink)
    }
}