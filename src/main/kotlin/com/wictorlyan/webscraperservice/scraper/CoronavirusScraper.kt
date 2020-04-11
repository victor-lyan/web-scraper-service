package com.wictorlyan.webscraperservice.scraper

import com.wictorlyan.webscraperservice.entity.CoronavirusCountryStats
import com.wictorlyan.webscraperservice.property.CoronavirusProperties
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class CoronavirusScraper(val properties: CoronavirusProperties) {
    fun scrapeCountryStats(): List<CoronavirusCountryStats> {
        val result: MutableList<CoronavirusCountryStats> = mutableListOf()

        val url = properties.globalStats.baseUrl
        val document = Jsoup.connect(url).get()
        val tableBody = document.selectFirst("#main_table_countries_today tbody")
        /*val totalRow = document.selectFirst("#main_table_countries_today .total_row")
        val totalRowCells = totalRow.select("td")*/
        
        tableBody.select("tr").forEach {row ->
            val cells = row.select("td")
            result.add(CoronavirusCountryStats(
                country = cells[0].text(),
                totalCases = parseIntFromString(cells[1].text()),
                newCases = parseIntFromString(cells[2].text()),
                totalDeaths = parseIntFromString(cells[3].text()),
                newDeaths = parseIntFromString(cells[4].text()),
                totalRecovered = parseIntFromString(cells[5].text()),
                activeCases = parseIntFromString(cells[6].text()),
                seriousCases = parseIntFromString(cells[7].text()),
                totalCasesByMillion = parseDoubleFromString(cells[8].text()),
                deathsByMillion = parseDoubleFromString(cells[9].text())
            ))
        }
        
        /*result.add(CoronavirusCountryStats(
            country = "World",
            totalCases = parseIntFromString(totalRowCells[1].text()),
            newCases = parseIntFromString(totalRowCells[2].text()),
            totalDeaths = parseIntFromString(totalRowCells[3].text()),
            newDeaths = parseIntFromString(totalRowCells[4].text()),
            totalRecovered = parseIntFromString(totalRowCells[5].text()),
            activeCases = parseIntFromString(totalRowCells[6].text()),
            seriousCases = parseIntFromString(totalRowCells[7].text()),
            totalCasesByMillion = parseDoubleFromString(totalRowCells[8].text()),
            deathsByMillion = parseDoubleFromString(totalRowCells[9].text())
        ))*/
        
        return result
    }
    
    private fun parseIntFromString(input: String): Int {
        return input.replace(",", "").replace("+", "").toIntOrNull() ?: 0
    }
    
    private fun parseDoubleFromString(input: String): Double {
        return input.replace(",", "").toDoubleOrNull() ?: 0.0
    }
}