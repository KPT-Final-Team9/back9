package com.core.back9.controller;

import com.core.back9.dto.TenantDTO;
import com.core.back9.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/tenants")
@RestController
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("")
    public ResponseEntity<TenantDTO.RegisterResponse> register(
            @Valid
            @RequestBody TenantDTO.RegisterRequest request) {

        TenantDTO.RegisterResponse response = tenantService.register(request);

        return ResponseEntity.created(URI.create("/api/tenants/" + response.getId())).body(response);

    }


}
