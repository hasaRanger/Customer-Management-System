package com.cms.backend.controller;

import com.cms.backend.dto.MasterDataDto;
import com.cms.backend.service.MasterDataService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MasterDataController.class)
class MasterDataControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  MasterDataService masterDataService;

    @Test
    @DisplayName("GET /api/master-data - returns countries and cities")
    void getMasterData_returns200() throws Exception {
        MasterDataDto dto = new MasterDataDto();
        dto.setCountries(List.of(
                new MasterDataDto.CountryDto(1L, "Sri Lanka"),
                new MasterDataDto.CountryDto(2L, "India")
        ));
        dto.setCities(List.of(
                new MasterDataDto.CityDto(1L, "Colombo", 1L),
                new MasterDataDto.CityDto(2L, "Kandy", 1L)
        ));

        when(masterDataService.getMasterData()).thenReturn(dto);

        mockMvc.perform(get("/api/master-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countries").isArray())
                .andExpect(jsonPath("$.countries.length()").value(2))
                .andExpect(jsonPath("$.countries[0].name").value("Sri Lanka"))
                .andExpect(jsonPath("$.cities.length()").value(2))
                .andExpect(jsonPath("$.cities[0].name").value("Colombo"));
    }
}