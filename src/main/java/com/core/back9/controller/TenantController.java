package com.core.back9.controller;

import com.core.back9.dto.TenantDTO;
import com.core.back9.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/tenants")
@RestController
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("")
    public ResponseEntity<TenantDTO.RegisterResponse> register(
            @Valid
            @RequestBody TenantDTO.RegisterRequest request
    ) {

        TenantDTO.RegisterResponse response = tenantService.register(request);

        return ResponseEntity.created(URI.create("/api/tenants/" + response.getId())).body(response);

    }

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

    @PatchMapping("/{tenantId}")
    public ResponseEntity<TenantDTO.Info> modifyTenant(
            @PathVariable(name = "tenantId") Long tenantId,
            @Valid
            @RequestBody TenantDTO.RegisterRequest request
    ) {

        TenantDTO.Info info = tenantService.modifyTenant(tenantId, request);

        return ResponseEntity.ok(info);

    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Integer> deleteTenant(@PathVariable(name = "tenantId") Long tenantId) {

        Integer result = tenantService.deleteTenant(tenantId);

        return ResponseEntity.ok(result);
    }

}
