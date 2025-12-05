package com.api.monitor.collector.service

import com.api.monitor.collector.dto.LogRequest
import com.api.monitor.collector.model.primary.ApiLog
import com.api.monitor.collector.model.secondary.Incident
import com.api.monitor.collector.repository.primary.ApiLogRepository
import com.api.monitor.collector.repository.secondary.IncidentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LogService(
    private val apiLogRepository: ApiLogRepository,
    private val incidentRepository: IncidentRepository
) {

    fun processLog(request: LogRequest) {
        
        val log = ApiLog(
            serviceName = request.serviceName,
            endpoint = request.endpoint,
            method = request.method,
            status = request.status,
            durationMs = request.durationMs,
            timestamp = request.timestamp ?: LocalDateTime.now(),
            errorMessage = request.errorMessage,
            isRateLimitHit = request.rateLimitHit
        )
        apiLogRepository.save(log)

        if (request.durationMs > 500) {
            createOrUpdateIncident(request, "SLOW")
        }

        if (request.status >= 500) {
            createOrUpdateIncident(request, "ERROR")
        }
        
        if (request.rateLimitHit) {
             createOrUpdateIncident(request, "RATE_LIMIT")
        }
    }
    fun getAllLogs(): List<ApiLog> {
        return apiLogRepository.findAll()
    }

    private fun createOrUpdateIncident(request: LogRequest, type: String) {
        val existingIncident = incidentRepository.findByServiceNameAndEndpointAndStatus(
            request.serviceName, 
            request.endpoint, 
            "OPEN"
        )

        if (existingIncident == null) {
            val newIncident = Incident(
                serviceName = request.serviceName,
                endpoint = request.endpoint,
                type = type,
                status = "OPEN",
                detectedAt = LocalDateTime.now()
            )
            incidentRepository.save(newIncident)
        } else {
            // For now, we just ensure it's recorded.
        }
    }
}