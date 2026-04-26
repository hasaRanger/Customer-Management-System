package com.cms.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerResponseDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String nicNumber;
    private List<String> phoneNumbers;
    private List<AddressResponseDto> addresses;
    private List<FamilyMemberDto> familyMembers;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressResponseDto {
        private Long id;
        private String addressLine1;
        private String addressLine2;
        private String cityName;
        private String countryName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FamilyMemberDto {
        private Long id;
        private String name;
        private String nicNumber;
    }
}