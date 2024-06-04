package com.core.back9.entity;

import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContractTest {

    @DisplayName("계약 완료 시나리오")
    @TestFactory
    Collection<DynamicTest> completeContractDynamicTest() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        return List.of(
                DynamicTest.dynamicTest("""
                        대기 상태인 계약을 완료 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    Contract modifiedContract = contract.contractComplete();

                    // then
                    assertThat(modifiedContract.getContractStatus()).isEqualTo(ContractStatus.COMPLETED);
                }),

                DynamicTest.dynamicTest("""
                        계약이 대기 상태가 아니라면 완료 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(() -> contract.contractComplete())
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 대기 상태가 아닙니다.");
                })
        );

    }

    @DisplayName("계약 취소 시나리오")
    @TestFactory
    Collection<DynamicTest> cancelContractDynamicTest() {
        // given
        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        contract2.contractComplete();

        return List.of(
                DynamicTest.dynamicTest("""
                        대기 상태인 계약을 취소 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    Contract contract = contract1.contractCancelMissedStartDate();

                    // then
                    assertThat(contract.getContractStatus()).isEqualTo(ContractStatus.CANCELED);
                }),

                DynamicTest.dynamicTest("""
                        완료 상태인 계약을 취소 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    Contract contract = contract2.contractCancelMissedStartDate();

                    // then
                    assertThat(contract.getContractStatus()).isEqualTo(ContractStatus.CANCELED);
                }),

                DynamicTest.dynamicTest("""
                        계약 대기 혹은 완료 상태가 아니라면
                        계약을 취소 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(contract1::contractCancelMissedStartDate)
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약을 취소할 수 있는 상태가 아닙니다.");

                })
        );

    }

    @DisplayName("계약 이행 시나리오")
    @TestFactory
    Collection<DynamicTest> inProgressContractDynamicTest() {
        // given
        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        contract2.contractComplete();

        return List.of(
                DynamicTest.dynamicTest("""
                        완료 상태가 아닌 계약은 이행 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(() -> contract1.contractInProgress())
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 완료 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        완료 상태인 계약을 이행 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    Contract contract = contract2.contractInProgress();

                    // then
                    assertThat(contract.getContractStatus()).isEqualTo(ContractStatus.IN_PROGRESS);
                })
        );

    }

    @DisplayName("계약 만료 시나리오")
    @TestFactory
    Collection<DynamicTest> expireContractDynamicTest() {
        // given
        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        contract2.contractComplete(); // 계약 이행 상태로 변경하기 위해 먼저 완료 상태로 변경
        contract2.contractInProgress(); // 계약 이행 상태로 변경

        return List.of(
                DynamicTest.dynamicTest("""
                        이행 상태가 아닌 계약은 만료 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(() -> contract1.contractExpire())
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 이행 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        이행 상태인 계약을 만료 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    Contract contract = contract2.contractExpire();

                    // then
                    assertThat(contract)
                            .extracting("endDate", "checkOut", "contractStatus")
                            .contains(LocalDate.now().plusDays(25),
                                    LocalDate.now().plusDays(25),
                                    ContractStatus.EXPIRED);
                })
        );

    }

    @DisplayName("계약 파기 시나리오")
    @TestFactory
    Collection<DynamicTest> terminateContractDynamicTest() {
        // given
        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL
        );

        contract2.contractComplete(); // 계약 이행 상태로 변경하기 위해 먼저 완료 상태로 변경
        contract2.contractInProgress(); // 계약 이행 상태로 변경

        return List.of(

                DynamicTest.dynamicTest("""
                        파기하려는 계약의 상태가 이행 중이 아닌 경우
                        계약 파기가 이루어지지 않는다.
                        """, () -> {
                    // given
                    LocalDate checkOut = LocalDate.now().plusDays(23);

                    // when & then
                    assertThatThrownBy(() -> contract1.contractTerminate(checkOut))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약이 이행 중인 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        이행 상태인 계약을 파기 상태로 변경할 수 있다.
                        """, () -> {
                    // given
                    LocalDate checkOut = LocalDate.now().plusDays(23);

                    // when
                    Contract contract = contract2.contractTerminate(checkOut);

                    // then
                    assertThat(contract)
                            .extracting("endDate", "checkOut", "contractStatus")
                            .contains(LocalDate.now().plusDays(25),
                                    LocalDate.now().plusDays(23),
                                    ContractStatus.TERMINATED);

                })
        );

    }

    private Contract assumeContract(
            LocalDate startDate,
            LocalDate endDate,
            Long deposit,
            Long rentalPrice,
            ContractType contractType) {

        return Contract.builder()
                .startDate(startDate)
                .endDate(endDate)
                .deposit(deposit)
                .rentalPrice(rentalPrice)
                .contractType(contractType)
                .build();

    }
}
