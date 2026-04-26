package com.cms.backend.controller;

import com.cms.backend.dto.MasterDataDto;
import com.cms.backend.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-data")
@RequiredArgsConstructor
public class MasterDataController {
    private final MasterDataService masterDataService;

    @GetMapping
    public ResponseEntity<MasterDataDto> get() {
        return ResponseEntity.ok(masterDataService.getMasterData());
    }
}