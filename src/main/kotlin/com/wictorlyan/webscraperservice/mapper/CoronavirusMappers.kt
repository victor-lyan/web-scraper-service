package com.wictorlyan.webscraperservice.mapper

import com.wictorlyan.webscraperservice.entity.CoronavirusCountryStats
import com.wictorlyan.webscraperservice.property.*
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import java.sql.PreparedStatement
import java.sql.ResultSet

class CoronavirusCountrySetter(
    val countryStats: List<CoronavirusCountryStats>
) : BatchPreparedStatementSetter {
    override fun setValues(ps: PreparedStatement, i: Int) {
        ps.setString(1, countryStats[i].country)
        ps.setInt(2, countryStats[i].totalCases)
        ps.setInt(3, countryStats[i].newCases)
        ps.setInt(4, countryStats[i].totalDeaths)
        ps.setInt(5, countryStats[i].newDeaths)
        ps.setInt(6, countryStats[i].totalRecovered)
        ps.setInt(7, countryStats[i].activeCases)
        ps.setInt(8, countryStats[i].seriousCases)
        ps.setDouble(9, countryStats[i].totalCasesByMillion)
        ps.setDouble(10, countryStats[i].deathsByMillion)
        ps.setString(11, countryStats[i].firstCaseDate)
    }

    override fun getBatchSize(): Int {
        return countryStats.size
    }
}

class CoronavirusCountryStatsRowMapper : RowMapper<CoronavirusCountryStats> {
    override fun mapRow(rs: ResultSet, rowNum: Int): CoronavirusCountryStats? {
        return CoronavirusCountryStats().apply {
            country = rs.getString(COLUMN_COUNTRY)
            totalCases = rs.getInt(COLUMN_TOTAL_CASES)
            newCases = rs.getInt(COLUMN_NEW_CASES)
            totalDeaths = rs.getInt(COLUMN_TOTAL_DEATHS)
            newDeaths = rs.getInt(COLUMN_NEW_DEATHS)
            totalRecovered = rs.getInt(COLUMN_TOTAL_RECOVERED)
            activeCases = rs.getInt(COLUMN_ACTIVE_CASES)
            seriousCases = rs.getInt(COLUMN_SERIOUS_CASES)
            totalCasesByMillion = rs.getDouble(COLUMN_TOTAL_CASES_BY_MILLION)
            deathsByMillion = rs.getDouble(COLUMN_DEATHS_BY_MILLION)
            firstCaseDate = rs.getString(COLUMN_FIRST_CASE_DATE)
            createdDate = rs.getTimestamp(COLUMN_CREATED_DATE).toInstant()
            modifiedDate = rs.getTimestamp(COLUMN_MODIFIED_DATE).toInstant()
        }
    }
}