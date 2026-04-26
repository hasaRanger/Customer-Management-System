package com.cms.backend.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MasterDataDto {
    private List<CountryDto> countries;
    private List<CityDto> cities;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CountryDto {
        private Long id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CityDto {
        private Long id;
        private String name;
        private Long countryId;
    }
}