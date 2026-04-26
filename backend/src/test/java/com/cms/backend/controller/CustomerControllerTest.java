package com.cms.backend.controller;

import com.cms.backend.dto.*;
import com.cms.backend.exception.*;
import com.cms.backend.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  CustomerService customerService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private CustomerRequestDto buildRequest() {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setName("John Silva");
        dto.setNicNumber("199005150123");
        dto.setDateOfBirth(LocalDate.of(1990, 5, 15));
        dto.setPhoneNumbers(List.of("0771234567"));
        dto.setAddresses(new ArrayList<>());
        dto.setFamilyMemberIds(new ArrayList<>());
        return dto;
    }

    private CustomerResponseDto buildResponse(Long id) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(id);
        dto.setName("John Silva");
        dto.setNicNumber("199005150123");
        dto.setDateOfBirth(LocalDate.of(1990, 5, 15));
        dto.setPhoneNumbers(List.of("0771234567"));
        dto.setAddresses(new ArrayList<>());
        dto.setFamilyMembers(new ArrayList<>());
        return dto;
    }

    // ── POST /api/customers ───────────────────────────────────

    @Test
    @DisplayName("POST /api/customers - creates customer and returns 201")
    void createCustomer_returns201() throws Exception {
        when(customerService.create(any())).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Silva"))
                .andExpect(jsonPath("$.nicNumber").value("199005150123"));
    }

    @Test
    @DisplayName("POST /api/customers - missing name returns 400")
    void createCustomer_missingName_returns400() throws Exception {
        CustomerRequestDto dto = buildRequest();
        dto.setName("");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("POST /api/customers - missing NIC returns 400")
    void createCustomer_missingNic_returns400() throws Exception {
        CustomerRequestDto dto = buildRequest();
        dto.setNicNumber("");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nicNumber").exists());
    }

    @Test
    @DisplayName("POST /api/customers - duplicate NIC returns 409")
    void createCustomer_duplicateNic_returns409() throws Exception {
        when(customerService.create(any())).thenThrow(new DuplicateNicException("199005150123"));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── GET /api/customers ────────────────────────────────────

    @Test
    @DisplayName("GET /api/customers - returns 200 with paged result")
    void getAllCustomers_returns200() throws Exception {
        CustomerListDto listDto = new CustomerListDto();
        listDto.setId(1L);
        listDto.setName("John Silva");
        listDto.setNicNumber("199005150123");
        listDto.setDateOfBirth(LocalDate.of(1990, 5, 15));

        Page<CustomerListDto> page = new PageImpl<>(List.of(listDto));
        when(customerService.getAll(any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Silva"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/customers - returns empty page when no customers")
    void getAllCustomers_empty_returns200() throws Exception {
        when(customerService.getAll(any(), anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ── GET /api/customers/{id} ───────────────────────────────

    @Test
    @DisplayName("GET /api/customers/{id} - returns 200 with customer")
    void getCustomerById_returns200() throws Exception {
        when(customerService.getById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Silva"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} - not found returns 404")
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Customer not found: 99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── PUT /api/customers/{id} ───────────────────────────────

    @Test
    @DisplayName("PUT /api/customers/{id} - updates and returns 200")
    void updateCustomer_returns200() throws Exception {
        CustomerResponseDto updated = buildResponse(1L);
        updated.setName("Updated Name");
        when(customerService.update(eq(1L), any())).thenReturn(updated);

        CustomerRequestDto req = buildRequest();
        req.setName("Updated Name");

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - not found returns 404")
    void updateCustomer_notFound_returns404() throws Exception {
        when(customerService.update(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Customer not found: 99"));

        mockMvc.perform(put("/api/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/customers/{id} ────────────────────────────

    @Test
    @DisplayName("DELETE /api/customers/{id} - deletes and returns 204")
    void deleteCustomer_returns204() throws Exception {
        doNothing().when(customerService).delete(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - not found returns 404")
    void deleteCustomer_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Customer not found: 99"))
                .when(customerService).delete(99L);

        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound());
    }
}