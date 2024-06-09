package com.core.back9.controller;

import com.core.back9.dto.ContractDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/app/contracts")
@RestController
public class AppUserContractController {

	private final ContractService contractService;

	@GetMapping
	public ResponseEntity<ContractDTO.InfoWithRoomList> getContract(
	  @AuthMember MemberDTO.Info member
	  ) {
		return ResponseEntity.ok(contractService.selectContractsByTenantId(member));
	}

}
