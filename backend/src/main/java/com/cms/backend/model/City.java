package com.cms.backend.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "cities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class City {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
}