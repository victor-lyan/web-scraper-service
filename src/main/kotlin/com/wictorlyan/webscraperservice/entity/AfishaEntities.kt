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
    
    constructor(
        id: Int, 
        name: String, 
        linkAfisha: String, 
        linkAbout: String
    ) : this(name, linkAfisha, linkAbout) {
        this.id = id
    }
}

data class AfishaMovie(
    var name: String = "",
    var genre: String = "",
    var link: String = "",
    var image: String? = null
) : BaseEntity() {
    var cinemas: MutableList<AfishaCinemaMovie> = mutableListOf()
    
    constructor(id: Int, name: String, genre: String, link: String) : this(name, genre, link) {
        this.id = id
    }

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

class AfishaCinemaMovie(
    var cinema: AfishaCinema, 
    var movie: AfishaMovie, 
    var movieDate: LocalDate, 
    var movieTime: LocalTime, 
    var format: String? = null
) {
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

data class AfishaNewsArticle(
    var title: String,
    var link: String,
    var date: String,
    var description: String
)