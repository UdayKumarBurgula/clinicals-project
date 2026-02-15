package com.uday.patientclinical.clinicalsapi.clinicalapi.controllers;

import com.uday.patientclinical.clinicalsapi.clinicalapi.controllers.dto.ClinicalDataRequest;
import com.uday.patientclinical.clinicalsapi.clinicalapi.models.ClinicalData;
import com.uday.patientclinical.clinicalsapi.clinicalapi.models.Patient;
import com.uday.patientclinical.clinicalsapi.clinicalapi.repos.ClinicalDataRepository;
import com.uday.patientclinical.clinicalsapi.clinicalapi.repos.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/clinicaldata")
public class ClinicalDataController {
    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    public List<ClinicalData> getAllClinicalData() {
        return clinicalDataRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable Long id) {
        Optional<ClinicalData> clinicalData = clinicalDataRepository.findById(id);
        return clinicalData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ClinicalData createClinicalData(@RequestBody ClinicalData clinicalData) {
        return clinicalDataRepository.save(clinicalData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable Long id, @RequestBody ClinicalData clinicalDataDetails) {
        Optional<ClinicalData> optionalClinicalData = clinicalDataRepository.findById(id);
        if (!optionalClinicalData.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ClinicalData clinicalData = optionalClinicalData.get();
        clinicalData.setComponentName(clinicalDataDetails.getComponentName());
        clinicalData.setComponentValue(clinicalDataDetails.getComponentValue());
        clinicalData.setMeasuredDateTime(clinicalDataDetails.getMeasuredDateTime());
        // Set other fields as needed
        ClinicalData updatedClinicalData = clinicalDataRepository.save(clinicalData);
        return ResponseEntity.ok(updatedClinicalData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinicalData(@PathVariable Long id) {
        if (!clinicalDataRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clinicalDataRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    
   // method that recieve the patient id,  clinical data and saves it to the database, and return the saved clinical data.
    @PostMapping("/clinicals")
    public ResponseEntity<ClinicalData> createClinicalData(@RequestBody ClinicalDataRequest clinicalDataRequest) {

        ClinicalData clinicalData = new ClinicalData();
        clinicalData.setComponentName(clinicalDataRequest.getComponentName());
        clinicalData.setComponentValue(clinicalDataRequest.getComponentValue());
        Patient patient = patientRepository.findById(clinicalDataRequest.getPatientId()).get();
        clinicalData.setPatient(patient);
        ClinicalData savedClinicalData = clinicalDataRepository.save(clinicalData);
        return ResponseEntity.ok(savedClinicalData);
    }
    


}
