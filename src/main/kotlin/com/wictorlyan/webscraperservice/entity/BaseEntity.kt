package com.wictorlyan.webscraperservice.entity

import java.time.Instant

open class BaseEntity {
    var id: Int = 0
    var createdDate: Instant = Instant.now()
    var modifiedDate: Instant = Instant.now()
}