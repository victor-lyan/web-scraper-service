package com.wictorlyan.webscraperservice.controller

import com.wictorlyan.webscraperservice.dto.AfishaMovieDTO
import com.wictorlyan.webscraperservice.dto.AfishaMovieScheduleDTO
import com.wictorlyan.webscraperservice.dto.AfishaMovieSmallDTO
import com.wictorlyan.webscraperservice.dto.AfishaNewsListDTO
import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.service.AfishaMovieService
import com.wictorlyan.webscraperservice.service.AfishaNewsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/afisha-movie")
class AfishaMovieController(
    val afishaMovieService: AfishaMovieService
) {
    @GetMapping("/daily-schedule")
    fun getMoviesForDate(
        @RequestParam(required = false) short: String?,
        @RequestParam(required = false) date: String?
    ): AfishaMovieScheduleDTO {
        val moviesForDate = afishaMovieService.getMoviesForDate(date)
        
        return AfishaMovieScheduleDTO(
            if (short == null)
                getFullMoviesList(moviesForDate) 
            else 
                getSmallMoviesList(moviesForDate)
        )
    }
    
    @GetMapping("/{id}")
    fun getMovie(
        @PathVariable id: Int,
        @RequestParam(required = false) date: String?
    ): ResponseEntity<AfishaMovieDTO> {
        val movie = afishaMovieService.getMovieWithCinemas(id, date)
        return if (movie == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity.ok(AfishaMovieDTO(movie))
        }
    }
    
    @GetMapping("/get-by-name/{name}")
    fun getMovieByName(
        @PathVariable name: String, 
        @RequestParam(required = false) date: String?
    ): ResponseEntity<AfishaMovieDTO> {
        val movie = afishaMovieService.getMovieWithCinemas(name, date)
        return if (movie == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity.ok(AfishaMovieDTO(movie))
        }
    }
    
    private fun getSmallMoviesList(movies: List<AfishaMovie>): List<AfishaMovieSmallDTO> {
        val result = mutableListOf<AfishaMovieSmallDTO>()
        movies.forEach {
            result.add(AfishaMovieSmallDTO(it.id, it.name, it.genre))
        }
        return result
    }

    private fun getFullMoviesList(movies: List<AfishaMovie>): List<AfishaMovieDTO> {
        val result = mutableListOf<AfishaMovieDTO>()
        movies.forEach {
            result.add(AfishaMovieDTO(it))
        }
        return result
    }
}

@RestController
@RequestMapping("/afisha-news")
class AfishaNewsController(
    val afishaNewsService: AfishaNewsService
) {
    @GetMapping("/latest")
    fun getLatestNews(): AfishaNewsListDTO {
        return AfishaNewsListDTO(afishaNewsService.getLatestNews())
    }
}