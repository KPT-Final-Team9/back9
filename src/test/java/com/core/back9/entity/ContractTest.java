package com.core.back9.entity;

import com.core.back9.dto.ContractDTO;
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
                        계약에 명시된 시작 일자가 이미 지났다면
                        계약을 완료 상태로 변경할 수 없다.
                        """, () -> {
                    // given
                    LocalDate today = LocalDate.now().plusDays(11);

                    // when & then
                    assertThatThrownBy(() -> contract.contractCompleted(today))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약에 명시된 시작 일자가 이미 지났습니다.");
                }),

                DynamicTest.dynamicTest("""
                        대기 상태인 계약을 완료 상태로 변경할 수 있다.
                        """, () -> {
                    // given
                    LocalDate today = LocalDate.now().plusDays(10);

                    // when
                    ContractStatus contractStatus = contract.contractCompleted(today);

                    // then
                    assertThat(contractStatus).isEqualTo(ContractStatus.COMPLETED);
                }),

                DynamicTest.dynamicTest("""
                        계약이 대기 상태가 아니라면 완료 상태로 변경할 수 없다.
                        """, () -> {
                    // given
                    LocalDate today = LocalDate.now().plusDays(10);

                    // when & then
                    assertThatThrownBy(() -> contract.contractCompleted(today))
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

        LocalDate today = LocalDate.now().plusDays(10);
        contract2.contractCompleted(today);

        return List.of(
                DynamicTest.dynamicTest("""
                        대기 상태인 계약을 취소 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    ContractStatus contractStatus = contract1.contractCanceledMissedStartDate();

                    // then
                    assertThat(contractStatus).isEqualTo(ContractStatus.CANCELED);
                }),

                DynamicTest.dynamicTest("""
                        완료 상태인 계약을 취소 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    ContractStatus contractStatus = contract2.contractCanceledMissedStartDate();

                    // then
                    assertThat(contractStatus).isEqualTo(ContractStatus.CANCELED);
                }),

                DynamicTest.dynamicTest("""
                        계약 대기 혹은 완료 상태가 아니라면
                        계약을 취소 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(contract1::contractCanceledMissedStartDate)
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

        LocalDate today = LocalDate.now().plusDays(10);
        contract2.contractCompleted(today);

        return List.of(
                DynamicTest.dynamicTest("""
                        완료 상태가 아닌 계약은 이행 상태로 변경할 수 없다.
                        """, () -> {

                    // when & then
                    assertThatThrownBy(() -> contract1.contractInProgress(today))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 완료 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        계약 이행 일자가 아니라면
                        완료 상태인 계약을 이행 상태로 변경할 수 없다.
                        """, () -> {

                    // given
                    LocalDate beforeStartDate = LocalDate.now().plusDays(9);

                    // when & then
                    assertThatThrownBy(() -> contract2.contractInProgress(beforeStartDate))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 이행 일자가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        완료 상태인 계약을 이행 상태로 변경할 수 있다.
                        """, () -> {

                    // when
                    ContractStatus contractStatus = contract2.contractInProgress(today);

                    // then
                    assertThat(contractStatus).isEqualTo(ContractStatus.IN_PROGRESS);
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

        LocalDate today = LocalDate.now().plusDays(10);
        contract2.contractCompleted(today); // 계약 이행 상태로 변경하기 위해 먼저 완료 상태로 변경
        contract2.contractInProgress(today); // 계약 이행 상태로 변경

        return List.of(
                DynamicTest.dynamicTest("""
                        이행 상태가 아닌 계약은 만료 상태로 변경할 수 없다.
                        """, () -> {

                    // given
                    LocalDate endDate = LocalDate.now().plusDays(25);

                    // when & then
                    assertThatThrownBy(() -> contract1.contractExpire(endDate))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약 이행 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        만료를 원하는 일자가 정해진 만료일의 이전 일자인 경우
                        계약 만료 상태로 변경할 수 없다.
                        """, () -> {

                    // given
                    LocalDate endDate = LocalDate.now().plusDays(24);

                    // when & then
                    assertThatThrownBy(() -> contract2.contractExpire(endDate))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("""
                                    만료 상태로 변경을 원하는 일자가
                                    정해진 만료 일자보다 이전 일자인 경우
                                    계약 만료 상태로 변경할 수 없습니다.
                                    """);
                }),

                DynamicTest.dynamicTest("""
                        이행 상태인 계약을 만료 상태로 변경할 수 있다.
                        """, () -> {

                    // given
                    LocalDate endDate = LocalDate.now().plusDays(25);

                    // when
                    Contract contract = contract2.contractExpire(endDate);

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

        LocalDate today = LocalDate.now().plusDays(10);
        contract2.contractCompleted(today); // 계약 이행 상태로 변경하기 위해 먼저 완료 상태로 변경
        contract2.contractInProgress(today); // 계약 이행 상태로 변경

        return List.of(
                DynamicTest.dynamicTest("""
                        퇴실하려는 일자가 계약 종료일자보다 이후 일자라면
                        계약 파기가 성립하지 않는다.
                        """, () -> {
                    // given
                    ContractDTO.StatusChangeRequest request = ContractDTO.StatusChangeRequest.builder()
                            .checkOut(LocalDate.now().plusDays(26))
                            .build();

                    // when & then
                    assertThatThrownBy(() -> contract1.contractTerminate(request))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("""
                                    원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                                    계약 종료 일자 보다 이후의 일자라면
                                    계약 파기 상태로 변경할 수 없습니다.
                                    """);
                }),

                DynamicTest.dynamicTest("""
                        퇴실하려는 일자가 기존 퇴실 일자와 같은 일자라면
                        계약 파기가 성립하지 않는다.
                        """, () -> {
                    // given
                    ContractDTO.StatusChangeRequest request = ContractDTO.StatusChangeRequest.builder()
                            .checkOut(LocalDate.now().plusDays(25))
                            .build();

                    // when & then
                    assertThatThrownBy(() -> contract1.contractTerminate(request))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("""
                                    원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                                    계약 종료 일자 보다 이후의 일자라면
                                    계약 파기 상태로 변경할 수 없습니다.
                                            """);
                }),

                DynamicTest.dynamicTest("""
                        파기하려는 계약의 상태가 이행 중이 아닌 경우
                        계약 파기가 이루어지지 않는다.
                        """, () -> {
                    // given
                    ContractDTO.StatusChangeRequest request = ContractDTO.StatusChangeRequest.builder()
                            .checkOut(LocalDate.now().plusDays(23))
                            .build();

                    // when & then
                    assertThatThrownBy(() -> contract1.contractTerminate(request))
                            .isInstanceOf(ApiException.class)
                            .extracting("errorMessage")
                            .isEqualTo("계약이 이행 중인 상태가 아닙니다.");
                }),

                DynamicTest.dynamicTest("""
                        이행 상태인 계약을 파기 상태로 변경할 수 있다.
                        """, () -> {
                    // given
                    ContractDTO.StatusChangeRequest request = ContractDTO.StatusChangeRequest.builder()
                            .checkOut(LocalDate.now().plusDays(23))
                            .build();

                    // when
                    Contract contract = contract2.contractTerminate(request);

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
