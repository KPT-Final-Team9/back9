package com.core.back9.controller;

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
    @Operation(
            summary = "입주사 정보 등록", description = "입주사 정보를 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
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
    @Operation(
            summary = "입주사 정보 수정", description = "선택한 입주사의 정보를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "유효한 입주사를 찾을 수 없습니다."))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
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
    @Operation(
            summary = "입주사 정보 삭제", description = "선택한 입주사의 정보를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "삭제가 완료되지 않았습니다."))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "관리자만 접근할 수 있습니다."))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(example = "권한이 없습니다."))})
    })
    public ResponseEntity<Integer> deleteTenant(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "tenantId") Long tenantId
    ) {

        Integer result = tenantService.deleteTenant(member, tenantId);

        return ResponseEntity.ok(result);

    }

}
