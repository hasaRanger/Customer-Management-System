package com.cms.backend.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerListDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String nicNumber;
    private int phoneCount;
    private int addressCount;
}