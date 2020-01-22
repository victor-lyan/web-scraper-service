package com.wictorlyan.webscraperservice.entity

import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class AfishaCinema(
    var name: String = "",
    var linkAfisha: String = "",
    var linkAbout: String = ""
) : BaseEntity() {
    var movies: MutableList<AfishaCinemaMovie> = mutableListOf()
    
    constructor(id: Int, name: String, linkAfisha: String, linkAbout: String) : this()
}

data class AfishaMovie(
    var name: String = "",
    var genre: String = "",
    var link: String = ""
) : BaseEntity() {
    var cinemas: MutableList<AfishaCinemaMovie> = mutableListOf()
    
    constructor(id: Int, name: String, genre: String, link: String) : this()

    fun addCinema(cinema: AfishaCinema, movieDate: LocalDate, movieTime: LocalTime, format: String? = null) {
        val cinemaMovie = AfishaCinemaMovie(cinema, this, movieDate, movieTime, format)
        cinemas.add(cinemaMovie)
        cinema.movies.add(cinemaMovie)
    }

    fun removeCinema(cinema: AfishaCinema, movieDate: LocalDate, movieTime: LocalTime) {
        val iterator: MutableIterator<AfishaCinemaMovie> = cinemas.iterator()
        while (iterator.hasNext()) {
            val cinemaMovie: AfishaCinemaMovie = iterator.next()
            if (
                cinemaMovie.movie == this
                && cinemaMovie.cinema == cinema
                && cinemaMovie.movieDate == movieDate
                && cinemaMovie.movieTime == movieTime
            ) {
                iterator.remove()
                cinemaMovie.movie.cinemas.remove(cinemaMovie)
            }
        }
    }
}

class AfishaCinemaMovie {
    var cinema: AfishaCinema

    var movie: AfishaMovie

    var movieDate: LocalDate

    var movieTime: LocalTime

    var format: String?

    constructor(cinema: AfishaCinema, movie: AfishaMovie, date: LocalDate, time: LocalTime, format: String? = null) {
        this.cinema = cinema
        this.movie = movie
        this.movieDate = date
        this.movieTime = time
        this.format = format
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other == null || this.javaClass != other.javaClass) return false

        val that = other as? AfishaCinemaMovie
        return (cinema == that?.cinema)
            && (movie == that.movie)
            && (movieDate == that.movieDate)
            && (movieTime == that.movieTime)
    }

    override fun hashCode(): Int {
        return Objects.hash(cinema, movie, movieDate, movieTime)
    }
}