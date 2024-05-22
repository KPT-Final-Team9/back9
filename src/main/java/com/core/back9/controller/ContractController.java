package com.core.back9.controller;

import com.core.back9.dto.ContractDTO;
import com.core.back9.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/contracts")
@RestController
public class ContractController { // TODO: Tenant, Member 구현 정도에 따라 리팩토링 우선

    private final ContractService contractService;

    @PostMapping("")
    public ResponseEntity<ContractDTO.RegisterResponse> registerContract(
            @Valid
            @RequestBody ContractDTO.RegisterRequest request
    ) {

        ContractDTO.RegisterResponse response = contractService.registerContract(request);

        return ResponseEntity
                .created(URI.create("/api/contracts" + response.getId()))
                .body(response);

    }

    @GetMapping("")
    public ResponseEntity<ContractDTO.InfoList> getAllContract(Pageable pageable) {

       ContractDTO.InfoList infoList = contractService.getAllContract(pageable);

        return ResponseEntity.ok(infoList);

    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> getOneContract(@PathVariable(name = "contractId") Long contractId) {

        ContractDTO.Info info = contractService.getOneContract(contractId);

        return null;

    }

    @PatchMapping("/{contractId}")
    public ResponseEntity<ContractDTO.Info> modifyContract(
            @PathVariable(name = "contractId") Long contractId,
            @Valid
            @RequestBody ContractDTO.UpdateRequest request
    ) {

        ContractDTO.Info info = contractService.modifyContract(contractId, request);

        return ResponseEntity.ok(info);

    }

    @DeleteMapping("/{contractId}")
    public ResponseEntity<Integer> deleteContract(@PathVariable(name = "contractId") Long contractId) {

        Integer result = contractService.deleteContract(contractId);

        return ResponseEntity.ok(result);
    }

    // TODO : checkOutUpdate, ContractStatusModify 미구현

}
