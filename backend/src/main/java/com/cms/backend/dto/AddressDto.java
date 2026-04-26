package com.cms.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddressDto {
    private String addressLine1;
    private String addressLine2;
    private Long cityId;
    private Long countryId;
}