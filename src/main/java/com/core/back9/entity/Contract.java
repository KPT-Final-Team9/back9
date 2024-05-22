package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate; // 계약 시작일자

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate; // 계약 종료일자

	@Column(name = "check_out", nullable = false)
	private LocalDate checkOut; // 실제 퇴실일자

	@Column(name = "deposit", nullable = false)
	private Long deposit; // 보증금

	@Column(name = "rental_price", nullable = false)
	private Long rentalPrice; // 월 납입금

	@Enumerated(EnumType.STRING)
	@Column(name = "contract_status", nullable = false)
	private ContractStatus contractStatus; // 계약의 상태

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Builder
	private Contract(LocalDate startDate, LocalDate endDate, Long deposit, Long rentalPrice, Room room, Tenant tenant) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.checkOut = endDate; // 초기 생성시 checkOut == endDate
		this.deposit = deposit;
		this.rentalPrice = rentalPrice;
		this.contractStatus = ContractStatus.PENDING; // 초기 생성시 ContractStatus == PENDING
		this.room = room;
		this.tenant = tenant;
		this.status = Status.REGISTER;
	}

	public Contract infoUpdate(ContractDTO.UpdateRequest request) {
		this.startDate = request.getStartDate();
		this.endDate = request.getEndDate();
		this.checkOut = request.getCheckOut();
		this.deposit = request.getDeposit();
		this.rentalPrice = request.getRentalPrice();
		this.status = Status.REGISTER; // status = reqister 일 때만 변경 가능
		return this;
	}

}
