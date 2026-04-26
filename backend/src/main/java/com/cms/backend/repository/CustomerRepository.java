package com.cms.backend.repository;

import com.cms.backend.model.Customer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByNicNumber(String nicNumber);

    boolean existsByNicNumberAndIdNot(String nicNumber, Long id);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%',:search,'%')) " +
            "OR LOWER(c.nicNumber) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<Customer> findAllWithSearch(@Param("search") String search, Pageable pageable);

    Optional<Customer> findByNicNumber(String nicNumber);
}