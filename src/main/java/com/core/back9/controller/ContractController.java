package com.core.back9.controller;

import com.core.back9.dto.ContractDTO;
import com.core.back9.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/rooms/{roomId}/contracts")
@RestController
public class ContractController { // TODO: Tenant, Member 구현 정도에 따라 리팩토링 우선

    private final ContractService contractService;

    @PostMapping("/{tenantId}") // TODO : Member 붙이면 tenantId 캐치 방법 재고려해야함
    public ResponseEntity<ContractDTO.RegisterResponse> registerContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "tenantId") Long tenantId,
            @Valid
            @RequestBody ContractDTO.RegisterRequest request
    ) {

        ContractDTO.RegisterResponse response = contractService.registerContract(buildingId, roomId, tenantId, request);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + buildingId + "/rooms/" + roomId + "/contracts/" + response.getId()))
                .body(response);

    }

    @PostMapping("/{contractId}/tenant/{tenantId}")
    public ResponseEntity<ContractDTO.Info> renewContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @PathVariable(name = "tenantId") Long tenantId,
            @RequestBody ContractDTO.RegisterRequest request
    ) {

        ContractDTO.Info info = contractService.renewContract(buildingId, roomId, contractId, tenantId, request);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + buildingId + "/rooms/" + roomId + "/contracts/" + info.getId()))
                .body(info);

    }

    @GetMapping("")
    public ResponseEntity<ContractDTO.InfoList> getAllContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            Pageable pageable
    ) {

        ContractDTO.InfoList infoList = contractService.getAllContract(buildingId, roomId, pageable);

        return ResponseEntity.ok(infoList);

    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> getOneContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        ContractDTO.Info info = contractService.getOneContract(buildingId, roomId, contractId);

        return ResponseEntity.ok(info);

    }

    @PatchMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> modifyContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @Valid
            @RequestBody ContractDTO.UpdateRequest request
    ) {

        ContractDTO.Info info = contractService.modifyContract(buildingId, roomId, contractId, request);

        return ResponseEntity.ok(info);

    }

    @PatchMapping("/{contractId}/complete")
    public ResponseEntity<ContractDTO.statusInfo> completeContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.statusInfo statusInfo = contractService.completeContract(buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/cancel")
    public ResponseEntity<ContractDTO.statusInfo> cancelContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.statusInfo statusInfo = contractService.cancelContract(buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/progress")
    public ResponseEntity<ContractDTO.statusInfo> progressContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.statusInfo statusInfo = contractService.progressContract(buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/expire")
    public ResponseEntity<ContractDTO.statusInfo> expireContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate endDate = LocalDate.now();

        ContractDTO.statusInfo statusInfo = contractService.expireContract(buildingId, roomId, contractId, endDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/terminate")
    public ResponseEntity<ContractDTO.statusInfo> terminateContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @RequestParam(name = "checkOut", required = true) LocalDate checkOut // 더 적합한 방법으로 일자를 받을 방법 고민 중
    ) {

        ContractDTO.statusInfo statusInfo = contractService.terminateContract(buildingId, roomId, contractId, checkOut);

        return ResponseEntity.ok(statusInfo);

    }

    @DeleteMapping("/{contractId}")
    public ResponseEntity<Integer> deleteContract(
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        Integer result = contractService.deleteContract(buildingId, roomId, contractId);

        return ResponseEntity.ok(result);

    }

}
