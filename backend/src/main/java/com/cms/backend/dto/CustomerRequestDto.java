package com.cms.backend.dto;

import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;

    @NotBlank(message = "NIC number is mandatory")
    private String nicNumber;

    private List<String> phoneNumbers = new ArrayList<>();
    private List<AddressDto> addresses = new ArrayList<>();
    private List<Long> familyMemberIds = new ArrayList<>();
}