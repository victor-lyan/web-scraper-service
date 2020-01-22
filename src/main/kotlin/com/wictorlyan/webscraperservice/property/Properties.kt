package com.wictorlyan.webscraperservice.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "afisha")
@Configuration
class AfishaProperties {
    
    val movies: Movies = Movies()
    lateinit var baseUrl: String

    class Movies {
        lateinit var baseUrl: String
        lateinit var skipMoviesCriteria: String
    }
}