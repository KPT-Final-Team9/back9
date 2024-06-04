package com.core.back9.controller;

import com.core.back9.common.config.annotation.SwaggerDocs;
import com.core.back9.dto.ContractDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.mapper.ContractMapper;
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
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    @PostMapping("/{tenantId}")
    @SwaggerDocs.RegisterContract
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

    @GetMapping("/statistic")
    @SwaggerDocs.GetContractStatisticInfo
    public ResponseEntity<ContractDTO.StatisticInfo> getContractStatisticInfo(
            @AuthMember MemberDTO.Info member,
            @PathVariable(name = "buildingId") Long buildingId,
            @PathVariable(name = "roomId") Long roomId
    ) {

        LocalDate startDate = LocalDate.now().minusYears(1); // 검색 범위를 1년으로 설정하기 위한 변수

        ContractDTO.CostInfo costInfo = contractService.getContractCostInfo(member, buildingId, roomId); // 내 호실 임대료 & 공실이 아닌 호실의 임대 평균값 반환
        ContractDTO.RenewalContractRateInfo renewalContractRateInfo = contractService.getRenewalContractRateInfo(member, buildingId, roomId);
        ContractDTO.VacancyRateInfo vacancyRateInfo = contractService.getContractVacancyRateInfo(member, buildingId, roomId, startDate);

        ContractDTO.StatisticInfo statisticInfo = contractMapper.toStatisticInfo(costInfo, renewalContractRateInfo, vacancyRateInfo);

        return ResponseEntity.ok(statisticInfo);

    }

    @PostMapping("/{contractId}/tenants/{tenantId}")
    @SwaggerDocs.RenewContract
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
    @SwaggerDocs.GetAllContract
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
    @SwaggerDocs.GetOneContract
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
    @SwaggerDocs.ModifyContract
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
    @SwaggerDocs.CompleteContract
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
    @SwaggerDocs.CancelContract
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
    @SwaggerDocs.ProgressContract
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
    @SwaggerDocs.ExpireContract
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
    @SwaggerDocs.TerminateContract
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
    @SwaggerDocs.DeleteContract
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
