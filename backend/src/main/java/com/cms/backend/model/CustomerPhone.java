package com.cms.backend.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "customer_phones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerPhone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "phone_number")
    private String phoneNumber;
}