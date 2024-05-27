package com.core.back9.controller;

import com.core.back9.dto.TenantDTO;
import com.core.back9.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/public-api/tenants")
@RestController
public class TenantPublicController {

    private final TenantService tenantService;

    @GetMapping("")
    public ResponseEntity<TenantDTO.InfoList> getAllTenant(Pageable pageable) {

        TenantDTO.InfoList infoList = tenantService.getAllTenant(pageable);

        return ResponseEntity.ok(infoList);

    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDTO.Info> getOneTenant(@PathVariable(name = "tenantId") Long tenantId) {

        TenantDTO.Info info = tenantService.getOneTenant(tenantId);

        return ResponseEntity.ok(info);

    }
}
