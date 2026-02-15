package com.uday.patientclinical.clinicalsapi.clinicalapi;

import com.uday.patientclinical.clinicalsapi.clinicalapi.controllers.ClinicalDataController;
import com.uday.patientclinical.clinicalsapi.clinicalapi.controllers.dto.ClinicalDataRequest;
import com.uday.patientclinical.clinicalsapi.clinicalapi.models.ClinicalData;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.uday.patientclinical.clinicalsapi.clinicalapi.models.Patient;
import com.uday.patientclinical.clinicalsapi.clinicalapi.repos.ClinicalDataRepository;
import com.uday.patientclinical.clinicalsapi.clinicalapi.repos.PatientRepository;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ClinicalDataController.class)
class ClinicalDataControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ClinicalDataRepository clinicalDataRepository;
    @MockitoBean private PatientRepository patientRepository;

    @Test
    void getAllClinicalData_returnsList() throws Exception {
        ClinicalData cd1 = new ClinicalData();
        cd1.setId(1L);
        cd1.setComponentName("bp");
        cd1.setComponentValue("120/80");

        ClinicalData cd2 = new ClinicalData();
        cd2.setId(2L);
        cd2.setComponentName("heartrate");
        cd2.setComponentValue("72");

        when(clinicalDataRepository.findAll()).thenReturn(List.of(cd1, cd2));

        mockMvc.perform(get("/api/clinicaldata"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].componentName").value("bp"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getClinicalDataById_found_returnsOk() throws Exception {
        ClinicalData cd = new ClinicalData();
        cd.setId(10L);
        cd.setComponentName("bp");
        cd.setComponentValue("110/70");

        when(clinicalDataRepository.findById(10L)).thenReturn(Optional.of(cd));

        mockMvc.perform(get("/api/clinicaldata/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.componentName").value("bp"));
    }

    @Test
    void getClinicalDataById_notFound_returns404() throws Exception {
        when(clinicalDataRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinicaldata/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClinicalData_basePost_savesAndReturns() throws Exception {
        ClinicalData req = new ClinicalData();
        req.setComponentName("bp");
        req.setComponentValue("120/80");
        // req.setMeasuredDateTime(LocalDateTime.of(2018, 7, 9, 19, 34, 24));

        ClinicalData saved = new ClinicalData();
        saved.setId(1L);
        saved.setComponentName(req.getComponentName());
        saved.setComponentValue(req.getComponentValue());
        saved.setMeasuredDateTime(req.getMeasuredDateTime());

        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(saved);

        mockMvc.perform(post("/api/clinicaldata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.componentName").value("bp"));

        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    @Test
    void updateClinicalData_found_updatesFields() throws Exception {
        ClinicalData existing = new ClinicalData();
        existing.setId(5L);
        existing.setComponentName("bp");
        existing.setComponentValue("100/60");

        ClinicalData updateReq = new ClinicalData();
        updateReq.setComponentName("bp");
        updateReq.setComponentValue("120/80");
        // updateReq.setMeasuredDateTime(LocalDateTime.of(2019, 5, 29, 19, 34, 24));

        when(clinicalDataRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(clinicalDataRepository.save(any(ClinicalData.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/clinicaldata/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.componentValue").value("120/80"));

        ArgumentCaptor<ClinicalData> captor = ArgumentCaptor.forClass(ClinicalData.class);
        verify(clinicalDataRepository).save(captor.capture());
        assertThat(captor.getValue().getComponentValue()).isEqualTo("120/80");
    }

    @Test
    void updateClinicalData_notFound_returns404() throws Exception {
        when(clinicalDataRepository.findById(123L)).thenReturn(Optional.empty());

        ClinicalData updateReq = new ClinicalData();
        updateReq.setComponentName("bp");
        updateReq.setComponentValue("120/80");

        mockMvc.perform(put("/api/clinicaldata/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteClinicalData_found_deletesAndReturns204() throws Exception {
        when(clinicalDataRepository.existsById(7L)).thenReturn(true);

        mockMvc.perform(delete("/api/clinicaldata/7"))
                .andExpect(status().isNoContent());

        verify(clinicalDataRepository).deleteById(7L);
    }

    @Test
    void deleteClinicalData_notFound_returns404() throws Exception {
        when(clinicalDataRepository.existsById(7L)).thenReturn(false);

        mockMvc.perform(delete("/api/clinicaldata/7"))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, never()).deleteById(anyLong());
    }

    @Test
    void createClinicalData_clinicalsEndpoint_savesWithPatient() throws Exception {
        // request DTO
        ClinicalDataRequest dto = new ClinicalDataRequest();
        dto.setPatientId(1L);
        dto.setComponentName("bp");
        dto.setComponentValue("120/80");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Mccain");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        when(clinicalDataRepository.save(any(ClinicalData.class))).thenAnswer(inv -> {
            ClinicalData cd = inv.getArgument(0);
            cd.setId(100L);
            return cd;
        });

        mockMvc.perform(post("/api/clinicaldata/clinicals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.componentName").value("bp"));

        verify(patientRepository).findById(1L);
        verify(clinicalDataRepository).save(any(ClinicalData.class));
    }
}
