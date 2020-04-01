package com.wictorlyan.webscraperservice.controller

import com.wictorlyan.webscraperservice.entity.CoronavirusCountryStats
import com.wictorlyan.webscraperservice.service.CoronavirusService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coronavirus")
class CoronavirusController(
    val coronavirusService: CoronavirusService
) {
    @GetMapping("/country-data/{country}")
    fun getCountryData(@PathVariable country: String): ResponseEntity<CoronavirusCountryStats> {
        val data = coronavirusService.getDataForCountry(country)
        return if (data == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity.ok(data)
        }
    }
}