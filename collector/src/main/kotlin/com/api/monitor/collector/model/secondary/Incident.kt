package com.api.monitor.collector.model.secondary

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "incidents")
data class Incident(
    @Id
    val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val type: String, // "SLOW" or "ERROR"
    var status: String = "OPEN", // OPEN, RESOLVED
    val detectedAt: LocalDateTime = LocalDateTime.now(),
    
    @Version
    val version: Long? = null
)