package com.cms.backend.repository;

import com.cms.backend.model.BulkUploadJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkUploadJobRepository extends JpaRepository<BulkUploadJob, Long> {}