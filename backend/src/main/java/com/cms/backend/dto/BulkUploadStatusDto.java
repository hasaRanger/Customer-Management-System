package com.cms.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BulkUploadStatusDto {
    private Long jobId;
    private String status;
    private Integer totalRows;
    private Integer processedRows;
    private Integer failedRows;
    private String errorMessage;
}