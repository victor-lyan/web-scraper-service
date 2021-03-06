package com.wictorlyan.webscraperservice.dto

import com.wictorlyan.webscraperservice.entity.AfishaMovie
import com.wictorlyan.webscraperservice.entity.AfishaNewsArticle
import java.time.LocalDate
import java.time.LocalTime

data class AfishaCinemaMovieDTO(
    var movie: AfishaMovieDTO,
    var cinema: AfishaCinemaDTO,
    var date: LocalDate,
    var time: LocalTime,
    var format: String?
)

data class AfishaCinemaDTO(
    var id: Int,
    var name: String,
    var linkAfisha: String,
    var linkAbout: String
)

abstract class BaseAfishaMovieDTO

data class AfishaMovieDTO(
    var id: Int,
    var name: String,
    var genre: String,
    var link: String,
    var image: String?
): BaseAfishaMovieDTO() {
    var cinemas: MutableList<AfishaCinemaMovieDTO> = mutableListOf()

    constructor(movie: AfishaMovie) : this(movie.id, movie.name, movie.genre, movie.link, movie.image) {
        movie.cinemas.forEach {
            cinemas.add(AfishaCinemaMovieDTO(
                AfishaMovieDTO(it.movie.id, it.movie.name, it.movie.genre, it.movie.link, it.movie.image),
                AfishaCinemaDTO(it.cinema.id, it.cinema.name, it.cinema.linkAfisha, it.cinema.linkAbout),
                it.movieDate,
                it.movieTime,
                it.format
            ))
        }
    }
}

data class AfishaMovieSmallDTO(val id: Int, val name: String, val genre: String) : BaseAfishaMovieDTO()

data class AfishaMovieScheduleDTO(val result: List<BaseAfishaMovieDTO>)

data class AfishaNewsListDTO(val result: List<AfishaNewsArticle>)