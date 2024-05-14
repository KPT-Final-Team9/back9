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
public class Contracts extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "rental_price", nullable = false)
    private Long rentalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false)
    private ContractStatus contractStatus;

    @Builder
    private Contracts(LocalDate startDate, LocalDate endDate, LocalDate checkOut, Long rentalPrice, ContractStatus contractStatus) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkOut = checkOut;
        this.rentalPrice = rentalPrice;
        this.contractStatus = contractStatus;
    }

}
