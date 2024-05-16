package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.ContractStatus;
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
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "check_out", nullable = false)
	private LocalDate checkOut;

	@Column(name = "deposit", nullable = false)
	private Long deposit;

	@Column(name = "rental_price", nullable = false)
	private Long rentalPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "contract_status", nullable = false)
	private ContractStatus contractStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@Builder
	private Contract(LocalDate startDate, LocalDate endDate, LocalDate checkOut, Long deposit, Long rentalPrice, ContractStatus contractStatus, Room room, Tenant tenant) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.checkOut = checkOut;
		this.deposit = deposit;
		this.rentalPrice = rentalPrice;
		this.contractStatus = contractStatus;
		this.room = room;
		this.tenant = tenant;
	}
}
