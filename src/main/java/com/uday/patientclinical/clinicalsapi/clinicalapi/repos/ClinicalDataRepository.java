package com.uday.patientclinical.clinicalsapi.clinicalapi.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uday.patientclinical.clinicalsapi.clinicalapi.models.ClinicalData;

@Repository
public interface ClinicalDataRepository extends JpaRepository<ClinicalData, Long> {
    // Custom query methods can be defined here if needed

    
}
