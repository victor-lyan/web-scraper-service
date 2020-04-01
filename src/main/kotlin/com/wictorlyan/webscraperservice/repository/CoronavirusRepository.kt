package com.wictorlyan.webscraperservice.repository

import com.wictorlyan.webscraperservice.entity.CoronavirusCountryStats
import com.wictorlyan.webscraperservice.mapper.CoronavirusCountrySetter
import com.wictorlyan.webscraperservice.mapper.CoronavirusCountryStatsRowMapper
import com.wictorlyan.webscraperservice.property.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CoronavirusRepository(
    val jdbcTemplate: JdbcTemplate
) {
    fun updateCountryStats(countryStats: List<CoronavirusCountryStats>) {
        jdbcTemplate.batchUpdate(
            """
            INSERT INTO $TABLE_CORONA_COUNTRY_STATS ($COLUMN_COUNTRY,$COLUMN_TOTAL_CASES,
            $COLUMN_NEW_CASES,$COLUMN_TOTAL_DEATHS,$COLUMN_NEW_DEATHS,$COLUMN_TOTAL_RECOVERED,
            $COLUMN_ACTIVE_CASES,$COLUMN_SERIOUS_CASES,$COLUMN_TOTAL_CASES_BY_MILLION,
            $COLUMN_DEATHS_BY_MILLION,$COLUMN_FIRST_CASE_DATE) VALUES (?,?,?,?,?,?,?,?,?,?,?) 
            ON CONFLICT ($COLUMN_COUNTRY) DO UPDATE SET
            ($COLUMN_TOTAL_CASES,$COLUMN_NEW_CASES,$COLUMN_TOTAL_DEATHS,$COLUMN_NEW_DEATHS,
            $COLUMN_TOTAL_RECOVERED,$COLUMN_ACTIVE_CASES,$COLUMN_SERIOUS_CASES,
            $COLUMN_TOTAL_CASES_BY_MILLION,$COLUMN_DEATHS_BY_MILLION,$COLUMN_FIRST_CASE_DATE,
            $COLUMN_MODIFIED_DATE) = (EXCLUDED.$COLUMN_TOTAL_CASES,EXCLUDED.$COLUMN_NEW_CASES,
            EXCLUDED.$COLUMN_TOTAL_DEATHS,EXCLUDED.$COLUMN_NEW_DEATHS,EXCLUDED.$COLUMN_TOTAL_RECOVERED,
            EXCLUDED.$COLUMN_ACTIVE_CASES,EXCLUDED.$COLUMN_SERIOUS_CASES,
            EXCLUDED.$COLUMN_TOTAL_CASES_BY_MILLION,EXCLUDED.$COLUMN_DEATHS_BY_MILLION,
            EXCLUDED.$COLUMN_FIRST_CASE_DATE,NOW())
            """.trimIndent(),
            CoronavirusCountrySetter(countryStats)
        )
    }

    fun getDataForCountry(country: String): CoronavirusCountryStats? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM $TABLE_CORONA_COUNTRY_STATS WHERE LOWER($COLUMN_COUNTRY) = ?",
                arrayOf(country.toLowerCase()),
                CoronavirusCountryStatsRowMapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}