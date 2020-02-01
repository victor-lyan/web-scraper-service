package com.wictorlyan.webscraperservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class WebScraperServiceApplication

fun main(args: Array<String>) {
	runApplication<WebScraperServiceApplication>(*args)
}
