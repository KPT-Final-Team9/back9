package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    private ContractType contractType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    @Builder
    private Contract(LocalDate startDate, LocalDate endDate, Long deposit, Long rentalPrice, Room room, Tenant tenant, ContractType contractType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkOut = endDate; // 초기 생성시 checkOut == endDate
        this.deposit = deposit;
        this.rentalPrice = rentalPrice;
        this.contractStatus = ContractStatus.PENDING; // 초기 생성시 ContractStatus == PENDING
        this.contractType = contractType;
        this.room = room;
        this.tenant = tenant;
        this.status = Status.REGISTER;

    }

    public Contract infoUpdate(ContractDTO.UpdateRequest request) {
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.checkOut = request.getEndDate(); // 계약 내용의 변경을 가정해 checkOut는 endDate를 그대로 따라감
        this.deposit = request.getDeposit();
        this.rentalPrice = request.getRentalPrice();
        this.status = Status.REGISTER; // status = reqister 일 때만 변경 가능

        return this;

    }

    // 계약 완료 상태 지정
    public Contract contractComplete() { // 계약 대기 -> 완료 상태로 변경

        if (this.contractStatus != ContractStatus.PENDING) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약 대기 상태가 아닙니다.");
        }

        this.contractStatus = ContractStatus.COMPLETED;

        return this;

    }

    // 계약 취소 상태 지정
    public Contract contractCancelMissedStartDate() {

        if (!(this.contractStatus == ContractStatus.PENDING ||
              this.contractStatus == ContractStatus.COMPLETED)) { // 대기 & 완료 상태가 아닌 경우
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약을 취소할 수 있는 상태가 아닙니다.");
        }

        this.contractStatus = ContractStatus.CANCELED;

        return this;

    }

    // 계약 이행 상태 지정
    public Contract contractInProgress() {

        if (this.contractStatus != ContractStatus.COMPLETED) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약 완료 상태가 아닙니다.");
        }

        this.contractStatus = ContractStatus.IN_PROGRESS;

        return this;

    }

    // 계약 만료 상태 지정
    public Contract contractExpire() {

        if(this.contractStatus != ContractStatus.IN_PROGRESS) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약 이행 상태가 아닙니다.");
        }

        this.contractStatus = ContractStatus.EXPIRED;

        return this;

    }

    // 계약 파기 상태 지정 (실제 퇴실 일자 변경과 함께 계약 파기 상태로 변경)
    public Contract contractTerminate(LocalDate checkOut) {

        if (this.contractStatus != ContractStatus.IN_PROGRESS) {
            throw new ApiException(ApiErrorCode.CONTRACT_NOT_IN_PROGRESS);
        }

        this.checkOut = checkOut;
        this.contractStatus = ContractStatus.TERMINATED;

        return this;

    }
}
