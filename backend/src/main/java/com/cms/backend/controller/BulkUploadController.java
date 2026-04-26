package com.cms.backend.controller;

import com.cms.backend.dto.BulkUploadStatusDto;
import com.cms.backend.service.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/bulk-upload")
@RequiredArgsConstructor
public class BulkUploadController {
    private final BulkUploadService bulkUploadService;

    @PostMapping
    public ResponseEntity<BulkUploadStatusDto> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.accepted().body(bulkUploadService.initiateUpload(file));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<BulkUploadStatusDto> status(@PathVariable Long jobId) {
        return ResponseEntity.ok(bulkUploadService.getStatus(jobId));
    }
}