package com.cms.backend.service;

import com.cms.backend.dto.MasterDataDto;
import com.cms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterDataService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public MasterDataDto getMasterData() {
        return MasterDataDto.builder()
                .countries(countryRepository.findAll().stream()
                        .map(c -> new MasterDataDto.CountryDto(c.getId(), c.getName()))
                        .collect(Collectors.toList()))
                .cities(cityRepository.findAll().stream()
                        .map(c -> new MasterDataDto.CityDto(
                                c.getId(), c.getName(),
                                c.getCountry() != null ? c.getCountry().getId() : null))
                        .collect(Collectors.toList()))
                .build();
    }
}