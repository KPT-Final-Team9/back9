package com.core.back9.controller;

import com.core.back9.dto.ContractDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
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
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "tenantId") Long tenantId,
            @Valid
            @RequestBody ContractDTO.RegisterRequest request
    ) {

        ContractDTO.RegisterResponse response = contractService.registerContract(member, buildingId, roomId, tenantId, request);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + buildingId + "/rooms/" + roomId + "/contracts/" + response.getId()))
                .body(response);

    }

    @GetMapping("/statistic") // TODO : 기간 조건 추가
    public ResponseEntity<ContractDTO.StatisticInfo> getContractStatisticInfo(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId
    ) {

        ContractDTO.CostInfo statisticInfo = contractService.getContractCostInfo(member, buildingId, roomId); // 내 호실 임대료 & 공실이 아닌 호실의 임대 평균값 반환

        return null; // TODO : 내 호실의 임대료, 공실률, 재계약률 및 타호실 동일 항목 평균값 조회 결과 반환(StatisticInfo로 한번에 반환할 예정)
    }

    @PostMapping("/{contractId}/tenants/{tenantId}")
    public ResponseEntity<ContractDTO.Info> renewContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @PathVariable(name = "tenantId") Long tenantId,
            @RequestBody ContractDTO.RenewRequest request
    ) {

        ContractDTO.Info info = contractService.renewContract(member, buildingId, roomId, contractId, tenantId, request);

        return ResponseEntity
                .created(URI.create("/api/buildings/" + buildingId + "/rooms/" + roomId + "/contracts/" + info.getId()))
                .body(info);

    }

    @GetMapping("")
    public ResponseEntity<ContractDTO.InfoList> getAllContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            Pageable pageable
    ) {

        ContractDTO.InfoList infoList = contractService.getAllContract(member, buildingId, roomId, pageable);

        return ResponseEntity.ok(infoList);

    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> getOneContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        ContractDTO.Info info = contractService.getOneContract(member, buildingId, roomId, contractId);

        return ResponseEntity.ok(info);

    }

    @PatchMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> modifyContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @Valid
            @RequestBody ContractDTO.UpdateRequest request
    ) {

        ContractDTO.Info info = contractService.modifyContract(member, buildingId, roomId, contractId, request);

        return ResponseEntity.ok(info);

    }

    @PatchMapping("/{contractId}/complete")
    public ResponseEntity<ContractDTO.StatusInfo> completeContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.StatusInfo statusInfo = contractService.completeContract(member, buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/cancel")
    public ResponseEntity<ContractDTO.StatusInfo> cancelContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.StatusInfo statusInfo = contractService.cancelContract(member, buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/progress")
    public ResponseEntity<ContractDTO.StatusInfo> progressContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate startDate = LocalDate.now();

        ContractDTO.StatusInfo statusInfo = contractService.progressContract(member, buildingId, roomId, contractId, startDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/expire")
    public ResponseEntity<ContractDTO.StatusInfo> expireContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        LocalDate endDate = LocalDate.now();

        ContractDTO.StatusInfo statusInfo = contractService.expireContract(member, buildingId, roomId, contractId, endDate);

        return ResponseEntity.ok(statusInfo);

    }

    @PatchMapping("/{contractId}/terminate")
    public ResponseEntity<ContractDTO.StatusInfo> terminateContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @RequestParam(name = "checkOut") LocalDate checkOut // 더 적합한 방법으로 일자를 받을 방법 고민 중
    ) {

        ContractDTO.StatusInfo statusInfo = contractService.terminateContract(member, buildingId, roomId, contractId, checkOut);

        return ResponseEntity.ok(statusInfo);

    }

    @DeleteMapping("/{contractId}")
    public ResponseEntity<Integer> deleteContract(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId
    ) {

        Integer result = contractService.deleteContract(member, buildingId, roomId, contractId);

        return ResponseEntity.ok(result);

    }

}
