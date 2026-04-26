package com.cms.backend.service;

import com.cms.backend.dto.BulkUploadStatusDto;
import com.cms.backend.model.*;
import com.cms.backend.model.BulkUploadJob.JobStatus;
import com.cms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkUploadService {

    private final CustomerRepository customerRepository;
    private final BulkUploadJobRepository jobRepository;
    private static final int BATCH_SIZE = 500;

    public BulkUploadStatusDto initiateUpload(MultipartFile file) throws IOException {
        BulkUploadJob job = BulkUploadJob.builder()
                .fileName(file.getOriginalFilename())
                .status(JobStatus.PENDING)
                .processedRows(0).failedRows(0)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        job = jobRepository.save(job);
        byte[] fileBytes = file.getBytes();
        processAsync(job.getId(), fileBytes);
        return toStatusDto(job);
    }

    @Async("bulkUploadExecutor")
    public void processAsync(Long jobId, byte[] fileBytes) {
        BulkUploadJob job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return;

        job.setStatus(JobStatus.PROCESSING);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Find actual last row
            int lastRow = sheet.getLastRowNum();
            log.info("Total rows in sheet (including header): {}", lastRow);

            // Detect if row 0 is a header by checking if first cell is non-numeric text
            int startRow = 1; // default: skip header
            Row firstRow = sheet.getRow(0);
            if (firstRow != null) {
                Cell firstCell = firstRow.getCell(0);
                if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                    String val = firstCell.getStringCellValue().trim().toLowerCase();
                    // If it looks like a header word, skip it
                    if (val.equals("name") || val.equals("full name") || val.equals("customer name")) {
                        startRow = 1;
                        log.info("Header row detected, starting from row 1");
                    } else {
                        // No header — start from row 0
                        startRow = 0;
                        log.info("No header detected, starting from row 0");
                    }
                }
            }

            int totalDataRows = lastRow - startRow + 1;
            job.setTotalRows(totalDataRows);
            jobRepository.save(job);

            List<Customer> batch = new ArrayList<>();
            int processed = 0, failed = 0;

            for (int i = startRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;
                try {
                    Customer c = rowToCustomer(row, i);
                    Optional<Customer> existing = customerRepository.findByNicNumber(c.getNicNumber());
                    if (existing.isPresent()) {
                        Customer e = existing.get();
                        e.setName(c.getName());
                        e.setDateOfBirth(c.getDateOfBirth());
                        batch.add(e);
                    } else {
                        batch.add(c);
                    }
                } catch (Exception e) {
                    log.warn("Row {} failed: {}", i, e.getMessage());
                    failed++;
                }

                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch);
                    processed += batch.size();
                    batch.clear();
                    job.setProcessedRows(processed);
                    job.setFailedRows(failed);
                    job.setUpdatedAt(LocalDateTime.now());
                    jobRepository.save(job);
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch);
                processed += batch.size();
            }

            job.setProcessedRows(processed);
            job.setFailedRows(failed);
            job.setStatus(JobStatus.DONE);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
            log.info("Bulk upload done: {} processed, {} failed", processed, failed);

        } catch (Exception e) {
            log.error("Bulk upload failed", e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
        }
    }

    @Transactional
    public void saveBatch(List<Customer> batch) {
        customerRepository.saveAll(batch);
    }

    public BulkUploadStatusDto getStatus(Long jobId) {
        BulkUploadJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        return toStatusDto(job);
    }

    private Customer rowToCustomer(Row row, int rowNum) {
        String name = readCellAsString(row.getCell(0));
        String dob  = readCellAsString(row.getCell(1));
        String nic  = readCellAsString(row.getCell(2));

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Row " + rowNum + ": Name is empty");
        if (nic == null || nic.isBlank())
            throw new IllegalArgumentException("Row " + rowNum + ": NIC is empty");
        if (dob == null || dob.isBlank())
            throw new IllegalArgumentException("Row " + rowNum + ": Date of birth is empty");

        LocalDate parsedDate = parseDate(dob.trim(), rowNum);

        return Customer.builder()
                .name(name.trim())
                .nicNumber(nic.trim())
                .dateOfBirth(parsedDate)
                .phones(new ArrayList<>())
                .addresses(new ArrayList<>())
                .familyMembers(new HashSet<>())
                .build();
    }

    /**
     * Reads a cell as String regardless of its actual cell type.
     * Handles STRING, NUMERIC (including dates), FORMULA, and BLANK.
     */
    private String readCellAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                // Could be a date stored as numeric
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Excel date — convert to LocalDate
                    Date d = cell.getDateCellValue();
                    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
                }
                // Plain number — format without decimal
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);

            case FORMULA:
                // Evaluate formula result
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
            case _NONE:
            default:
                return null;
        }
    }

    /**
     * Tries multiple date formats to be flexible.
     */
    private LocalDate parseDate(String raw, int rowNum) {
        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd",
        };
        for (String fmt : formats) {
            try {
                return LocalDate.parse(raw, DateTimeFormatter.ofPattern(fmt));
            } catch (DateTimeParseException ignored) {}
        }
        throw new IllegalArgumentException("Row " + rowNum + ": Cannot parse date '" + raw + "'");
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private BulkUploadStatusDto toStatusDto(BulkUploadJob job) {
        return BulkUploadStatusDto.builder()
                .jobId(job.getId()).status(job.getStatus().name())
                .totalRows(job.getTotalRows()).processedRows(job.getProcessedRows())
                .failedRows(job.getFailedRows()).errorMessage(job.getErrorMessage())
                .build();
    }
}