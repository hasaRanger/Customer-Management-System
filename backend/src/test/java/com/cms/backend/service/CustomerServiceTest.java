package com.cms.backend.service;

import com.cms.backend.dto.*;
import com.cms.backend.exception.*;
import com.cms.backend.model.*;
import com.cms.backend.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock CustomerRepository customerRepository;
    @Mock CityRepository cityRepository;
    @Mock CountryRepository countryRepository;
    @InjectMocks CustomerService customerService;

    private CustomerRequestDto buildRequest(String name, String nic) {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setName(name);
        dto.setNicNumber(nic);
        dto.setDateOfBirth(LocalDate.of(1990, 5, 15));
        dto.setPhoneNumbers(List.of("0771234567"));
        dto.setAddresses(new ArrayList<>());
        dto.setFamilyMemberIds(new ArrayList<>());
        return dto;
    }

    private Customer buildCustomer(Long id, String name, String nic) {
        Customer c = new Customer();
        c.setId(id);
        c.setName(name);
        c.setNicNumber(nic);
        c.setDateOfBirth(LocalDate.of(1990, 5, 15));
        c.setPhones(new ArrayList<>());
        c.setAddresses(new ArrayList<>());
        c.setFamilyMembers(new HashSet<>());
        return c;
    }

    // ── CREATE ────────────────────────────────────────────────

    @Test
    @DisplayName("Create customer successfully")
    void create_success() {
        CustomerRequestDto dto = buildRequest("John Silva", "199005150123");
        when(customerRepository.existsByNicNumber("199005150123")).thenReturn(false);
        when(customerRepository.save(any())).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerResponseDto result = customerService.create(dto);

        assertThat(result.getName()).isEqualTo("John Silva");
        assertThat(result.getNicNumber()).isEqualTo("199005150123");
        assertThat(result.getId()).isEqualTo(1L);
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Create customer with duplicate NIC throws DuplicateNicException")
    void create_duplicateNic_throwsException() {
        CustomerRequestDto dto = buildRequest("John Silva", "199005150123");
        when(customerRepository.existsByNicNumber("199005150123")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(dto))
                .isInstanceOf(DuplicateNicException.class)
                .hasMessageContaining("199005150123");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create customer with phone numbers saves phones")
    void create_withPhones_savesPhones() {
        CustomerRequestDto dto = buildRequest("Jane Perera", "198508221234");
        dto.setPhoneNumbers(List.of("0771111111", "0772222222"));

        when(customerRepository.existsByNicNumber(any())).thenReturn(false);
        when(customerRepository.save(any())).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        CustomerResponseDto result = customerService.create(dto);

        assertThat(result.getPhoneNumbers()).hasSize(2);
        assertThat(result.getPhoneNumbers()).containsExactly("0771111111", "0772222222");
    }

    // ── GET BY ID ─────────────────────────────────────────────

    @Test
    @DisplayName("Get customer by ID returns correct customer")
    void getById_success() {
        Customer customer = buildCustomer(1L, "John Silva", "199005150123");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDto result = customerService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Silva");
    }

    @Test
    @DisplayName("Get customer by non-existent ID throws ResourceNotFoundException")
    void getById_notFound_throwsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── UPDATE ────────────────────────────────────────────────

    @Test
    @DisplayName("Update customer successfully")
    void update_success() {
        Customer existing = buildCustomer(1L, "Old Name", "199005150123");
        CustomerRequestDto dto = buildRequest("New Name", "199005150123");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByNicNumberAndIdNot("199005150123", 1L)).thenReturn(false);
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto result = customerService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Update customer with duplicate NIC throws DuplicateNicException")
    void update_duplicateNic_throwsException() {
        Customer existing = buildCustomer(1L, "John Silva", "199005150123");
        CustomerRequestDto dto = buildRequest("John Silva", "DUPLICATE_NIC");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByNicNumberAndIdNot("DUPLICATE_NIC", 1L)).thenReturn(true);

        assertThatThrownBy(() -> customerService.update(1L, dto))
                .isInstanceOf(DuplicateNicException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update non-existent customer throws ResourceNotFoundException")
    void update_notFound_throwsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(99L, buildRequest("Name", "NIC")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── DELETE ────────────────────────────────────────────────

    @Test
    @DisplayName("Delete customer successfully")
    void delete_success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        assertThatCode(() -> customerService.delete(1L)).doesNotThrowAnyException();
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete non-existent customer throws ResourceNotFoundException")
    void delete_notFound_throwsException() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(customerRepository, never()).deleteById(any());
    }

    // ── GET ALL ───────────────────────────────────────────────

    @Test
    @DisplayName("Get all customers returns paginated result")
    void getAll_returnsPaginatedResult() {
        Customer c = buildCustomer(1L, "Alice", "NIC001");
        Page<Customer> page = new PageImpl<>(List.of(c),
                PageRequest.of(0, 20), 1);

        when(customerRepository.findAllWithSearch(isNull(), any())).thenReturn(page);

        Page<CustomerListDto> result = customerService.getAll(null, 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Get all with search term returns filtered result")
    void getAll_withSearch_returnsFiltered() {
        Customer c = buildCustomer(2L, "Bob", "NIC002");
        Page<Customer> page = new PageImpl<>(List.of(c));

        when(customerRepository.findAllWithSearch(eq("Bob"), any())).thenReturn(page);

        Page<CustomerListDto> result = customerService.getAll("Bob", 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Get all customers returns empty page when no customers exist")
    void getAll_empty_returnsEmptyPage() {
        when(customerRepository.findAllWithSearch(isNull(), any()))
                .thenReturn(Page.empty());

        Page<CustomerListDto> result = customerService.getAll(null, 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }
}