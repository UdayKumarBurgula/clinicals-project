// Java
package com.uday.patientclinical.clinicalsapi.clinicalapi;

import com.uday.patientclinical.clinicalsapi.clinicalapi.controllers.PatientController;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.uday.patientclinical.clinicalsapi.clinicalapi.models.Patient;
import com.uday.patientclinical.clinicalsapi.clinicalapi.repos.PatientRepository;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient createPatient(Long id, String firstName, String lastName, int age) {
        Patient p = new Patient();
        p.setId(id);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAge(age);
        return p;
    }

    @Test
    @DisplayName("GET /api/patients - returns list of patients")
    void getAllPatients_returnsList() throws Exception {
        Patient p1 = createPatient(1L, "Alice", "Smith", 30);
        Patient p2 = createPatient(2L, "Bob", "Jones", 45);
        List<Patient> patients = Arrays.asList(p1, p2);

        when(patientRepository.findAll()).thenReturn(patients);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Alice")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Bob")));

        verify(patientRepository, times(1)).findAll();
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("GET /api/patients/{id} - found")
    void getPatientById_found_returnsPatient() throws Exception {
        Patient p = createPatient(1L, "Alice", "Smith", 30);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/patients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.age", is(30)));

        verify(patientRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("GET /api/patients/{id} - not found")
    void getPatientById_notFound_returns404() throws Exception {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(99L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("POST /api/patients - creates patient")
    void createPatient_savesAndReturns() throws Exception {
        Patient input = createPatient(null, "Charlie", "Brown", 28);
        Patient saved = createPatient(5L, "Charlie", "Brown", 28);

        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.firstName", is("Charlie")))
                .andExpect(jsonPath("$.lastName", is("Brown")))
                .andExpect(jsonPath("$.age", is(28)));

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository, times(1)).save(captor.capture());
        Patient savedArg = captor.getValue();
        // verify fields passed to save
        assert savedArg.getFirstName().equals("Charlie");
        assert savedArg.getLastName().equals("Brown");
        assert savedArg.getAge() == 28;
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - updates when found")
    void updatePatient_found_updatesAndReturns() throws Exception {
        Patient existing = createPatient(10L, "Dana", "White", 50);
        when(patientRepository.findById(10L)).thenReturn(Optional.of(existing));

        Patient updateDetails = createPatient(null, "DanaUpdated", "WhiteUpdated", 51);
        Patient updated = createPatient(10L, "DanaUpdated", "WhiteUpdated", 51);
        when(patientRepository.save(any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("DanaUpdated")))
                .andExpect(jsonPath("$.lastName", is("WhiteUpdated")))
                .andExpect(jsonPath("$.age", is(51)));

        verify(patientRepository, times(1)).findById(10L);
        verify(patientRepository, times(1)).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("PUT /api/patients/{id} - not found")
    void updatePatient_notFound_returns404() throws Exception {
        when(patientRepository.findById(20L)).thenReturn(Optional.empty());

        Patient details = createPatient(null, "X", "Y", 40);

        mockMvc.perform(put("/api/patients/{id}", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(20L);
        verify(patientRepository, times(0)).save(any());
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - deletes when exists")
    void deletePatient_found_deletesAndReturnsNoContent() throws Exception {
        when(patientRepository.existsById(30L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(30L);

        mockMvc.perform(delete("/api/patients/{id}", 30L))
                .andExpect(status().isNoContent());

        verify(patientRepository, times(1)).existsById(30L);
        verify(patientRepository, times(1)).deleteById(30L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} - not found")
    void deletePatient_notFound_returns404() throws Exception {
        when(patientRepository.existsById(40L)).thenReturn(false);

        mockMvc.perform(delete("/api/patients/{id}", 40L))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).existsById(40L);
        verify(patientRepository, times(0)).deleteById(any());
        verifyNoMoreInteractions(patientRepository);
    }
}