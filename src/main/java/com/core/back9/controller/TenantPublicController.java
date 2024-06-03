package com.core.back9.controller;

import com.core.back9.dto.TenantDTO;
import com.core.back9.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "모든 입주사 정보 조회", description = "모든 입주사 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<TenantDTO.InfoList> getAllTenant(Pageable pageable) {

        TenantDTO.InfoList infoList = tenantService.getAllTenant(pageable);

        return ResponseEntity.ok(infoList);

    }

    @GetMapping("/{tenantId}")
    @Operation(
            summary = "특정 입주사 정보 조회", description = "선택한 입주사 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "유효한 입주사를 찾을 수 없습니다."))})
    })
    public ResponseEntity<TenantDTO.Info> getOneTenant(@PathVariable(name = "tenantId") Long tenantId) {

        TenantDTO.Info info = tenantService.getOneTenant(tenantId);

        return ResponseEntity.ok(info);

    }
}
