package com.cms.backend.controller;

import com.cms.backend.dto.*;
import com.cms.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody CustomerRequestDto dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerListDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(customerService.getAll(search, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}