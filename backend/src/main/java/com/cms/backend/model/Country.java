package com.cms.backend.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "countries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Country {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}