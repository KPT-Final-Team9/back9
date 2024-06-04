package com.core.back9.controller;

import com.core.back9.common.config.annotation.SwaggerDocs;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.TenantDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/tenants")
@RestController
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("")
    @SwaggerDocs.RegisterTenant
    public ResponseEntity<TenantDTO.Response> registerTenant(
            @AuthMember MemberDTO.Info member,
            @Valid
            @RequestBody TenantDTO.Request request
    ) {

        TenantDTO.Response response = tenantService.registerTenant(member, request);

        return ResponseEntity
                .created(URI.create("/api/tenants/" + response.getId()))
                .body(response);

    }

    @PatchMapping("/{tenantId}")
    @SwaggerDocs.ModifyTenant
    public ResponseEntity<TenantDTO.Info> modifyTenant(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "tenantId") Long tenantId,
            @Valid
            @RequestBody TenantDTO.Request request
    ) {

        TenantDTO.Info info = tenantService.modifyTenant(member, tenantId, request);

        return ResponseEntity.ok(info);

    }

    @DeleteMapping("/{tenantId}")
    @SwaggerDocs.DeleteTenant
    public ResponseEntity<Integer> deleteTenant(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "tenantId") Long tenantId
    ) {

        Integer result = tenantService.deleteTenant(member, tenantId);

        return ResponseEntity.ok(result);

    }

}
