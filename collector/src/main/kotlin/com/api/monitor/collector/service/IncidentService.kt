package com.api.monitor.collector.service

import com.api.monitor.collector.model.secondary.Incident
import com.api.monitor.collector.repository.secondary.IncidentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IncidentService(private val incidentRepository: IncidentRepository) {

    fun getOpenIncidents(): List<Incident> {
        return incidentRepository.findAll().filter { it.status == "OPEN" }
    }

    @Transactional
    fun resolveIncident(id: String) {
        val incident = incidentRepository.findById(id).orElseThrow { 
            RuntimeException("Incident not found") 
        }
        
        incident.status = "RESOLVED"
        
        incidentRepository.save(incident)
    }
}