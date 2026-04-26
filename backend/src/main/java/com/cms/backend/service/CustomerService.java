package com.cms.backend.service;

import com.cms.backend.dto.*;
import com.cms.backend.exception.*;
import com.cms.backend.model.*;
import com.cms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public CustomerResponseDto create(CustomerRequestDto dto) {
        if (customerRepository.existsByNicNumber(dto.getNicNumber())) {
            throw new DuplicateNicException(dto.getNicNumber());
        }
        Customer customer = buildCustomer(new Customer(), dto);
        return toResponseDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        if (customerRepository.existsByNicNumberAndIdNot(dto.getNicNumber(), id)) {
            throw new DuplicateNicException(dto.getNicNumber());
        }
        customer.getPhones().clear();
        customer.getAddresses().clear();
        customer.getFamilyMembers().clear();
        buildCustomer(customer, dto);
        return toResponseDto(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return toResponseDto(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerListDto> getAll(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return customerRepository.findAllWithSearch(search, pageable)
                .map(this::toListDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }

    private Customer buildCustomer(Customer customer, CustomerRequestDto dto) {
        customer.setName(dto.getName());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setNicNumber(dto.getNicNumber());

        if (dto.getPhoneNumbers() != null) {
            dto.getPhoneNumbers().stream()
                    .filter(p -> p != null && !p.isBlank())
                    .map(p -> CustomerPhone.builder().phoneNumber(p).build())
                    .forEach(customer::addPhone);
        }

        if (dto.getAddresses() != null) {
            for (AddressDto a : dto.getAddresses()) {
                CustomerAddress addr = new CustomerAddress();
                addr.setAddressLine1(a.getAddressLine1());
                addr.setAddressLine2(a.getAddressLine2());
                if (a.getCityId() != null)
                    addr.setCity(cityRepository.findById(a.getCityId()).orElse(null));
                if (a.getCountryId() != null)
                    addr.setCountry(countryRepository.findById(a.getCountryId()).orElse(null));
                customer.addAddress(addr);
            }
        }

        if (dto.getFamilyMemberIds() != null) {
            dto.getFamilyMemberIds().stream()
                    .filter(fid -> !fid.equals(customer.getId()))
                    .map(fid -> customerRepository.findById(fid)
                            .orElseThrow(() -> new ResourceNotFoundException("Family member not found: " + fid)))
                    .forEach(m -> customer.getFamilyMembers().add(m));
        }
        return customer;
    }

    public CustomerResponseDto toResponseDto(Customer c) {
        return CustomerResponseDto.builder()
                .id(c.getId())
                .name(c.getName())
                .dateOfBirth(c.getDateOfBirth())
                .nicNumber(c.getNicNumber())
                .phoneNumbers(c.getPhones().stream()
                        .map(CustomerPhone::getPhoneNumber).collect(Collectors.toList()))
                .addresses(c.getAddresses().stream().map(a ->
                        CustomerResponseDto.AddressResponseDto.builder()
                                .id(a.getId())
                                .addressLine1(a.getAddressLine1())
                                .addressLine2(a.getAddressLine2())
                                .cityName(a.getCity() != null ? a.getCity().getName() : null)
                                .countryName(a.getCountry() != null ? a.getCountry().getName() : null)
                                .build()).collect(Collectors.toList()))
                .familyMembers(c.getFamilyMembers().stream().map(m ->
                        CustomerResponseDto.FamilyMemberDto.builder()
                                .id(m.getId()).name(m.getName()).nicNumber(m.getNicNumber())
                                .build()).collect(Collectors.toList()))
                .build();
    }

    private CustomerListDto toListDto(Customer c) {
        return CustomerListDto.builder()
                .id(c.getId()).name(c.getName())
                .dateOfBirth(c.getDateOfBirth()).nicNumber(c.getNicNumber())
                .phoneCount(c.getPhones().size())
                .addressCount(c.getAddresses().size())
                .build();
    }
}