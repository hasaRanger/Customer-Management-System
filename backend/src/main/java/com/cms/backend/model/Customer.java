package com.cms.backend.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "nic_number", nullable = false, unique = true)
    private String nicNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CustomerPhone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CustomerAddress> addresses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "family_members",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private Set<Customer> familyMembers = new HashSet<>();

    public void addPhone(CustomerPhone phone) {
        phones.add(phone);
        phone.setCustomer(this);
    }

    public void addAddress(CustomerAddress address) {
        addresses.add(address);
        address.setCustomer(this);
    }
}