package com.cms.backend.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_upload_jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BulkUploadJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','PROCESSING','DONE','FAILED')")
    private JobStatus status;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "processed_rows")
    private Integer processedRows;

    @Column(name = "failed_rows")
    private Integer failedRows;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum JobStatus { PENDING, PROCESSING, DONE, FAILED }
}