package com.wictorlyan.webscraperservice.entity

data class CoronavirusCountryStats(
    var country: String = "",
    var totalCases: Int = 0,
    var newCases: Int = 0,
    var totalDeaths: Int = 0,
    var newDeaths: Int = 0,
    var totalRecovered: Int = 0,
    var activeCases: Int = 0,
    var seriousCases: Int = 0,
    var totalCasesByMillion: Double = 0.0,
    var deathsByMillion: Double = 0.0,
    var firstCaseDate: String = ""
) : BaseEntity()