package com.core.back9.service;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.exception.ApiException;
import com.core.back9.repository.ContractRepository;
import com.core.back9.service.fixture.ContractServiceFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContractServiceTest extends ContractServiceFixture {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @DisplayName("계약 대기 상태의 Contract를 완료 상태로 변경할 수 있다.")
    void completeContract() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        LocalDate today = LocalDate.now().plusDays(10);

        // when
        ContractDTO.statusInfo updatedContract = contractService.completeContract(1L, 1L, savedContract.getId(), today);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "endDate", "checkOut", "contractStatus")
                .contains(1L,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(25),
                        LocalDate.now().plusDays(25),
                        ContractStatus.COMPLETED
                );

    }

    @Test
    @DisplayName("완료 요청 일자가 계약 시작 일자를 넘긴 경우 예외가 발생한다.")
    void completeContractTimePasses() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        LocalDate today = LocalDate.now().plusDays(11);

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("계약 완료처리가 가능한 일자가 이미 경과했습니다.");

    }

    @Test
    @DisplayName("계약 대기 상태의 Contract를 취소 상태로 변경할 수 있다.")
    void cancelContract() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        LocalDate today = LocalDate.now().plusDays(10);

        // when
        ContractDTO.statusInfo updatedContract = contractService.cancelContract(1L, 1L, savedContract.getId(), today);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "endDate", "checkOut", "contractStatus")
                .contains(1L,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(25),
                        LocalDate.now().plusDays(25),
                        ContractStatus.CANCELED
                );

    }

    @Test
    @DisplayName("취소 요청 일자가 계약 시작 일자를 넘긴 경우 예외가 발생한다.")
    void cancelContractTimePasses() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        LocalDate today = LocalDate.now().plusDays(11);

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("계약 완료처리가 가능한 일자가 이미 경과했습니다.");

    }

    @Test
    @DisplayName("계약 완료 상태의 Contract를 이행 상태로 변경할 수 있다.")
    void progressContract() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete(); // 완료 상태로 변경 - 해당 메서드의 검증은 도메인 검증에서 처리됨

        LocalDate today = LocalDate.now().plusDays(10);

        // when
        ContractDTO.statusInfo updatedContract = contractService.progressContract(1L, 1L, savedContract.getId(), today);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "endDate", "checkOut", "contractStatus")
                .contains(1L,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(25),
                        LocalDate.now().plusDays(25),
                        ContractStatus.IN_PROGRESS
                );

    }

    @Test
    @DisplayName("이행 요청 일자가 계약 시작 일자가 아닌 경우 예외가 발생한다.")
    void progressContractTimeInvalid() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete(); // 완료 상태로 변경 - 해당 메서드의 검증은 도메인 검증에서 처리됨

        LocalDate afterDay = LocalDate.now().plusDays(11);
        LocalDate beforeDay = LocalDate.now().plusDays(9);

        // when & then
        assertThatThrownBy(() -> contractService.progressContract(1L, 1L, savedContract.getId(), afterDay))
                .isInstanceOf(ApiException.class) // 이행 요청 일자가 계약 시작 일자 이후인 경우
                .extracting("errorMessage")
                .isEqualTo("계약 이행 가능 일자가 아닙니다.");

        assertThatThrownBy(() -> contractService.progressContract(1L, 1L, savedContract.getId(), beforeDay))
                .isInstanceOf(ApiException.class) // 이행 요청 일자가 계약 시작 일자 이전인 경우
                .extracting("errorMessage")
                .isEqualTo("계약 이행 가능 일자가 아닙니다.");

    }

    @Test
    @DisplayName("계약 이행 상태의 Contract를 만료 상태로 변경할 수 있다.")
    void expireContract() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();

        LocalDate today = LocalDate.now().plusDays(25); // 계약 만료 요청 일자

        // when
        ContractDTO.statusInfo updatedContract = contractService.expireContract(1L, 1L, savedContract.getId(), today);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "endDate", "checkOut", "contractStatus")
                .contains(1L,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(25),
                        LocalDate.now().plusDays(25),
                        ContractStatus.EXPIRED
                );

    }

    @Test
    @DisplayName("만료 요청 일자가 계약 시작 일자 이전인 경우 예외가 발생한다.")
    void expireContractTimeEarly() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();

        LocalDate today = LocalDate.now().plusDays(24); // 계약 만료 요청 일자

        // when & then
        assertThatThrownBy(() -> contractService.expireContract(1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("""
                        만료 상태로 변경을 원하는 일자가
                        정해진 만료 일자보다 이전 일자인 경우
                        계약 만료 상태로 변경할 수 없습니다.
                        """);

    }

    @Test
    @DisplayName("계약 이행 상태의 Contract를 파기 상태로 변경할 수 있다.")
    void terminateContract() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();

        LocalDate today = LocalDate.now().plusDays(24); // 계약 파기 요청 일자

        // when
        ContractDTO.statusInfo updatedContract = contractService.terminateContract(1L, 1L, savedContract.getId(), today);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "endDate", "checkOut", "contractStatus")
                .contains(1L,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(25),
                        LocalDate.now().plusDays(24),
                        ContractStatus.TERMINATED
                );

    }

    @Test
    @DisplayName("파기 요청 일자가 계약 만료 일자 이후인 경우 예외가 발생한다.")
    void terminateContractTimePasses() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();

        LocalDate today = LocalDate.now().plusDays(26); // 계약 파기 요청 일자

        // when & then
        assertThatThrownBy(() -> contractService.terminateContract(1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("""
                        원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                        계약 종료 일자 보다 이후의 일자인 경우
                        계약 파기 상태로 변경할 수 없습니다.
                        """);

    }

    @Test
    @DisplayName("파기 요청 일자가 퇴실 일자와 같을 경우 예외가 발생한다.")
    void terminateContractTimeEarly() {
        // given
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();

        LocalDate today = LocalDate.now().plusDays(25); // 계약 퇴실 요청 일자

        // when & then
        assertThatThrownBy(() -> contractService.terminateContract(1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("""
                        원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                        계약 종료 일자 보다 이후의 일자인 경우
                        계약 파기 상태로 변경할 수 없습니다.
                        """);

    }

    private Contract assumeContract(
            LocalDate startDate,
            LocalDate endDate,
            Long deposit,
            Long rentalPrice,
            ContractType contractType,
            Room room,
            Tenant tenant) {

        return Contract.builder()
                .startDate(startDate)
                .endDate(endDate)
                .deposit(deposit)
                .rentalPrice(rentalPrice)
                .room(room)
                .contractType(contractType)
                .tenant(tenant)
                .build();

    }

}
