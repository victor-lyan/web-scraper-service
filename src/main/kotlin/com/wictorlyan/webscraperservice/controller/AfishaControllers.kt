package com.wictorlyan.webscraperservice.controller

import com.wictorlyan.webscraperservice.dto.AfishaMovieDTO
import com.wictorlyan.webscraperservice.dto.AfishaMovieScheduleDTO
import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.service.AfishaMovieService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/afisha-movie")
class AfishaMovieController(
    val afishaMovieService: AfishaMovieService
) {
    @GetMapping("/today-schedule")
    fun getMoviesForToday(): AfishaMovieScheduleDTO {
        val moviesForToday = afishaMovieService.getMoviesForToday()
        val result = mutableListOf<AfishaMovieDTO>()
        moviesForToday.forEach {
            result.add(AfishaMovieDTO(it))
        }
        
        return AfishaMovieScheduleDTO(result)
    }
    
    @GetMapping("/{id}")
    fun getMovie(@PathVariable id: Int): AfishaMovieDTO {
        val movie = AfishaMovie("Test movie", "Fantasy", "https://afisha.uz/test")
        return AfishaMovieDTO(movie)
    }
}