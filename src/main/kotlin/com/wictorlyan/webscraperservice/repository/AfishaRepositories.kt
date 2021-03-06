package com.wictorlyan.webscraperservice.repository

import com.wictorlyan.webscraperservice.entity.*
import com.wictorlyan.webscraperservice.mapper.*
import com.wictorlyan.webscraperservice.property.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate

@Repository
class AfishaCinemaRepository(
    val jdbcTemplate: JdbcTemplate
) {
    fun save(cinema: AfishaCinema): Int {
        val cinemaInDb = findByName(cinema.name)
        if (cinemaInDb == null) {
            val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_AFISHA_CINEMA)
                .usingGeneratedKeyColumns(COLUMN_ID)

            val params = mutableMapOf(
                COLUMN_NAME to cinema.name,
                COLUMN_LINK_AFISHA to cinema.linkAfisha,
                COLUMN_LINK_ABOUT to cinema.linkAbout,
                COLUMN_CREATED_DATE to Timestamp.from(cinema.createdDate),
                COLUMN_MODIFIED_DATE to Timestamp.from(cinema.modifiedDate)
            )

            return simpleJdbcInsert.executeAndReturnKey(params).toInt()
        } else {
            jdbcTemplate.update("UPDATE $TABLE_AFISHA_CINEMA SET " +
                "$COLUMN_NAME = ?," +
                "$COLUMN_LINK_AFISHA = ?," +
                "$COLUMN_LINK_ABOUT = ?," +
                "$COLUMN_MODIFIED_DATE = ? " +
                "WHERE $COLUMN_ID = ?",
                cinema.name,
                cinema.linkAfisha,
                cinema.linkAbout,
                Timestamp.from(Instant.now()),
                cinemaInDb.id
            )
            
            return cinemaInDb.id
        }
    }
    
    fun findByName(name: String): AfishaCinema? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM $TABLE_AFISHA_CINEMA WHERE $COLUMN_NAME = ?",
                arrayOf(name),
                AfishaCinemaRowMapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
    
    fun findAll(): List<AfishaCinema> {
        return jdbcTemplate.query("SELECT * FROM $TABLE_AFISHA_CINEMA", AfishaCinemaRowMapper())
    }
    
    fun removeById(cinema: AfishaCinema) {
        jdbcTemplate.update("DELETE $TABLE_AFISHA_CINEMA WHERE $COLUMN_ID = ?", cinema.id)
    }
}

@Repository
class AfishaMovieRepository(
    val jdbcTemplate: JdbcTemplate,
    val afishaProperties: AfishaProperties
) {
    fun save(movie: AfishaMovie): Int {
        val movieInDb = findByNameAndGenre(movie.name, movie.genre)
        if (movieInDb == null) {
            val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_AFISHA_MOVIE)
                .usingGeneratedKeyColumns(COLUMN_ID)

            val params = mutableMapOf(
                COLUMN_NAME to movie.name,
                COLUMN_GENRE to movie.genre,
                COLUMN_LINK to movie.link,
                COLUMN_IMAGE to movie.image,
                COLUMN_CREATED_DATE to Timestamp.from(movie.createdDate),
                COLUMN_MODIFIED_DATE to Timestamp.from(movie.modifiedDate)
            )

            return simpleJdbcInsert.executeAndReturnKey(params).toInt()
        } else {
            jdbcTemplate.update("UPDATE $TABLE_AFISHA_MOVIE SET " +
                "$COLUMN_NAME = ?," +
                "$COLUMN_GENRE = ?," +
                "$COLUMN_LINK = ?," +
                "$COLUMN_IMAGE = ?," +
                "$COLUMN_MODIFIED_DATE = ? " +
                "WHERE $COLUMN_ID = ?",
                movie.name,
                movie.genre,
                movie.link,
                movie.image,
                Timestamp.from(Instant.now()),
                movieInDb.id
            )

            return movieInDb.id
        }
    }

    fun findByIdOrName(
        id: Int? = null, 
        name: String? = null, 
        withCinemas: Boolean = false, 
        date: String? = null
    ): AfishaMovie? {
        return try {
            if (id == null && name == null) {
                throw EmptyResultDataAccessException(1)
            }
            
            val whereCondition: String
            val paramArray: Array<Any?>
            if (id == null) {
                whereCondition = "$COLUMN_NAME = ?"
                paramArray = arrayOf(name)
            } else {
                whereCondition = "$COLUMN_ID = ?"
                paramArray = arrayOf(id)
            }
            
            val movie = jdbcTemplate.queryForObject(
                "SELECT * FROM $TABLE_AFISHA_MOVIE WHERE $whereCondition",
                paramArray,
                AfishaMovieRowMapper()
            )
            
            if (movie != null && withCinemas) {
                val dateSql = if (date == null) LocalDate.now() else LocalDate.parse(date)
                val sql = """
                    SELECT m.$COLUMN_ID AS $COLUMN_MOVIE_ID, m.$COLUMN_NAME AS $COLUMN_MOVIE_NAME,
                    m.$COLUMN_GENRE, m.$COLUMN_LINK, c.$COLUMN_ID AS $COLUMN_CINEMA_ID, 
                    c.$COLUMN_NAME AS $COLUMN_CINEMA_NAME, c.$COLUMN_LINK_AFISHA, c.$COLUMN_LINK_ABOUT,
                    cm.$COLUMN_MOVIE_DATE, cm.$COLUMN_MOVIE_TIME, cm.$COLUMN_FORMAT 
                    FROM $TABLE_AFISHA_MOVIE AS m
                    LEFT JOIN $TABLE_AFISHA_CINEMA_MOVIE cm ON cm.$COLUMN_MOVIE_ID = m.$COLUMN_ID
                    LEFT JOIN $TABLE_AFISHA_CINEMA c ON c.$COLUMN_ID = cm.$COLUMN_CINEMA_ID
                    WHERE m.$whereCondition AND cm.$COLUMN_MOVIE_DATE = ?
                """.trimIndent()
                val movieCinemas = jdbcTemplate.query(
                    sql,
                    paramArray.plus(dateSql),
                    AfishaCinemaListExtractor()
                ) ?: emptyList()
                movie.cinemas.addAll(movieCinemas)
            }
            movie
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun findByNameAndGenre(name: String, genre: String): AfishaMovie? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM $TABLE_AFISHA_MOVIE WHERE $COLUMN_NAME = ? AND $COLUMN_GENRE = ?",
                arrayOf(name, genre),
                AfishaMovieRowMapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
    
    fun findByDate(date: LocalDate): List<AfishaMovie> {
        val sql = """
            SELECT m.$COLUMN_ID AS $COLUMN_MOVIE_ID, m.$COLUMN_NAME AS $COLUMN_MOVIE_NAME,
            m.$COLUMN_GENRE, m.$COLUMN_LINK, c.$COLUMN_ID AS $COLUMN_CINEMA_ID,
            c.$COLUMN_NAME AS $COLUMN_CINEMA_NAME, c.$COLUMN_LINK_AFISHA, c.$COLUMN_LINK_ABOUT,
            cm.$COLUMN_MOVIE_DATE, cm.$COLUMN_MOVIE_TIME, cm.$COLUMN_FORMAT 
            FROM $TABLE_AFISHA_MOVIE AS m
            LEFT JOIN $TABLE_AFISHA_CINEMA_MOVIE cm ON cm.$COLUMN_MOVIE_ID = m.$COLUMN_ID
            LEFT JOIN $TABLE_AFISHA_CINEMA c ON c.$COLUMN_ID = cm.$COLUMN_CINEMA_ID
            WHERE cm.$COLUMN_MOVIE_DATE = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, arrayOf(date), AfishaMovieListExtractor()) ?: emptyList()
    }

    fun findAll(): List<AfishaMovie> {
        return jdbcTemplate.query("SELECT * FROM $TABLE_AFISHA_MOVIE", AfishaMovieRowMapper())
    }

    fun removeById(movie: AfishaMovie) {
        jdbcTemplate.update("DELETE FROM $TABLE_AFISHA_MOVIE WHERE $COLUMN_ID = ?", movie.id)
    }

    fun getMoviesForUpdate(): List<AfishaMovie> {
        val sql = "SELECT * FROM $TABLE_AFISHA_MOVIE WHERE $COLUMN_IMAGE IS NULL " +
            "OR $COLUMN_LINK LIKE '%${afishaProperties.movies.wrongLinkPart}%'"
        return jdbcTemplate.query(sql, AfishaMovieRowMapper())
    }
}

@Repository
class AfishaCinemaMovieRepository(
    val jdbcTemplate: JdbcTemplate
) {
    fun removeAllForMovieAndDate(movieId: Int, date: LocalDate) {
        jdbcTemplate.update("DELETE FROM $TABLE_AFISHA_CINEMA_MOVIE WHERE $COLUMN_MOVIE_ID = ? " +
            "AND $COLUMN_MOVIE_DATE = ?", movieId, date)
    }
    
    fun saveAll(cinemaMovieList: List<AfishaCinemaMovie>) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO $TABLE_AFISHA_CINEMA_MOVIE VALUES (?,?,?,?,?) ON CONFLICT " +
                "($COLUMN_CINEMA_ID, $COLUMN_MOVIE_ID, $COLUMN_MOVIE_DATE, $COLUMN_MOVIE_TIME) DO NOTHING",
            AfishaCinemaMovieSetter(cinemaMovieList)
        )
    }
}