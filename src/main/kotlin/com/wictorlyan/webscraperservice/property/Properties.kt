package com.wictorlyan.webscraperservice.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "afisha")
@Configuration
class AfishaProperties {
    
    val movies = Movies()
    val news = News()
    lateinit var baseUrl: String
    lateinit var baseUrlGazeta: String

    class Movies {
        lateinit var baseUrl: String
        lateinit var skipMoviesCriteria: String
        lateinit var wrongLinkPart: String
    }
    
    class News {
        lateinit var baseUrl: String
    }
}

@ConfigurationProperties(prefix = "coronavirus")
@Configuration
class CoronavirusProperties {

    val globalStats = GlobalStats()

    class GlobalStats {
        lateinit var baseUrl: String
    }
}