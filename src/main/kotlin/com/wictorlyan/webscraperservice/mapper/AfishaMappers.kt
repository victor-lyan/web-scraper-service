package com.wictorlyan.webscraperservice.mapper

import com.wictorlyan.webscraperservice.entity.*
import com.wictorlyan.webscraperservice.property.*
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalTime

class AfishaCinemaRowMapper : RowMapper<AfishaCinema> {
    override fun mapRow(rs: ResultSet, rowNum: Int): AfishaCinema? {
        return AfishaCinema().apply {
            id = rs.getInt(COLUMN_ID)
            name = rs.getString(COLUMN_NAME)
            linkAfisha = rs.getString(COLUMN_LINK_AFISHA)
            linkAbout = rs.getString(COLUMN_LINK_ABOUT)
            createdDate = rs.getTimestamp(COLUMN_CREATED_DATE).toInstant()
            modifiedDate = rs.getTimestamp(COLUMN_MODIFIED_DATE).toInstant()
        }
    }
}

class AfishaMovieRowMapper : RowMapper<AfishaMovie> {
    override fun mapRow(rs: ResultSet, rowNum: Int): AfishaMovie? {
        return AfishaMovie().apply {
            id = rs.getInt(COLUMN_ID)
            name = rs.getString(COLUMN_NAME)
            genre = rs.getString(COLUMN_GENRE)
            link = rs.getString(COLUMN_LINK)
            createdDate = rs.getTimestamp(COLUMN_CREATED_DATE).toInstant()
            modifiedDate = rs.getTimestamp(COLUMN_MODIFIED_DATE).toInstant()
        }
    }
}

class AfishaMovieListExtractor : ResultSetExtractor<List<AfishaMovie>> {
    override fun extractData(rs: ResultSet): List<AfishaMovie> {
        val result = mutableListOf<AfishaMovie>()
        val movieCinemaMap: MutableMap<Int, MutableList<AfishaCinemaMovie>> = mutableMapOf()
        while (rs.next()) {
            val movieId = rs.getInt(COLUMN_MOVIE_ID)
            val movieName = rs.getString(COLUMN_MOVIE_NAME)
            val movieGenre = rs.getString(COLUMN_GENRE)
            val movieLink = rs.getString(COLUMN_LINK)
            val movieDate = rs.getString(COLUMN_MOVIE_DATE)
            val movieTime = rs.getString(COLUMN_MOVIE_TIME)
            val movieFormat = rs.getString(COLUMN_FORMAT)
            val cinemaId = rs.getInt(COLUMN_CINEMA_ID)
            val cinemaName = rs.getString(COLUMN_CINEMA_NAME)
            val cinemaLinkAfisha = rs.getString(COLUMN_LINK_AFISHA)
            val cinemaLinkAbout = rs.getString(COLUMN_LINK_ABOUT)
            val currentRowObject = AfishaCinemaMovie(
                AfishaCinema(cinemaId, cinemaName, cinemaLinkAfisha, cinemaLinkAbout),
                AfishaMovie(movieId, movieName, movieGenre, movieLink),
                LocalDate.parse(movieDate),
                LocalTime.parse(movieTime),
                movieFormat
            )
            
            var currentList = movieCinemaMap[movieId]
            if (currentList == null) {
                currentList = mutableListOf(currentRowObject)
                movieCinemaMap[movieId] = currentList
            } else {
                currentList.add(currentRowObject)
            }
        }
        
        for ((_, list) in movieCinemaMap) {
            val movie = list[0].movie
            list.forEach {movie.addCinema(it.cinema, it.movieDate, it.movieTime, it.format)}
            result.add(movie)
        }
        
        return result
    }
}

class AfishaCinemaListExtractor : ResultSetExtractor<List<AfishaCinemaMovie>> {
    override fun extractData(rs: ResultSet): List<AfishaCinemaMovie> {
        val result = mutableListOf<AfishaCinemaMovie>()
        while (rs.next()) {
            val cinemaId = rs.getInt(COLUMN_CINEMA_ID)
            val cinemaName = rs.getString(COLUMN_CINEMA_NAME)
            val cinemaLinkAfisha = rs.getString(COLUMN_LINK_AFISHA)
            val cinemaLinkAbout = rs.getString(COLUMN_LINK_ABOUT)
            val movieDate = rs.getString(COLUMN_MOVIE_DATE)
            val movieTime = rs.getString(COLUMN_MOVIE_TIME)
            val movieFormat = rs.getString(COLUMN_FORMAT)
            val movieId = rs.getInt(COLUMN_MOVIE_ID)
            val movieName = rs.getString(COLUMN_MOVIE_NAME)
            val movieGenre = rs.getString(COLUMN_GENRE)
            val movieLink = rs.getString(COLUMN_LINK)
            
            result.add(AfishaCinemaMovie(
                AfishaCinema(cinemaId, cinemaName, cinemaLinkAfisha, cinemaLinkAbout),
                AfishaMovie(movieId, movieName, movieGenre, movieLink),
                LocalDate.parse(movieDate),
                LocalTime.parse(movieTime),
                movieFormat
            ))
        }

        return result
    }
}

class AfishaCinemaMovieSetter(val cinemaMovieList: List<AfishaCinemaMovie>) : BatchPreparedStatementSetter {
    override fun setValues(ps: PreparedStatement, i: Int) {
        ps.setInt(1, cinemaMovieList[i].cinema.id)
        ps.setInt(2, cinemaMovieList[i].movie.id)
        ps.setObject(3, cinemaMovieList[i].movieDate)
        ps.setObject(4, cinemaMovieList[i].movieTime)
        ps.setString(5, cinemaMovieList[i].format)
    }

    override fun getBatchSize(): Int {
        return cinemaMovieList.size
    }

}