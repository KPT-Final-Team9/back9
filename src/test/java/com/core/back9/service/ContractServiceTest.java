package com.core.back9.service;

import com.core.back9.dto.ContractDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Role;
import com.core.back9.exception.ApiException;
import com.core.back9.repository.ContractRepository;
import com.core.back9.service.fixture.ContractServiceFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        MemberDTO.Info memberInfo = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when
        ContractDTO.StatusInfo updatedContract = contractService.completeContract(memberInfo, 1L, 1L, savedContract.getId(), today);

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(member, 1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("계약 완료처리가 가능한 일자가 이미 경과했습니다.");

    }

    @Test
    @DisplayName("어드민 권한을 가지지 않은 사용자라면 완료 요청이 불가능하다.")
    void completeContractRoleInvalid() {
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.ADMIN)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(member, 1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("소유자만 접근할 수 있습니다.");

    }

    @Test
    @DisplayName("해당 호실의 소유자가 아니라면 호실과 관련된 계약의 완료 처리를 수행할 수 없다.")
    void notCompleteContractNotOwnerThisRoom() {
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(1L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(member, 1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("유효한 호실을 찾을 수 없습니다");

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when
        ContractDTO.StatusInfo updatedContract = contractService.cancelContract(member, 1L, 1L, savedContract.getId(), today);

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.completeContract(member, 1L, 1L, savedContract.getId(), today))
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when
        ContractDTO.StatusInfo updatedContract = contractService.progressContract(member, 1L, 1L, savedContract.getId(), today);

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.progressContract(member, 1L, 1L, savedContract.getId(), afterDay))
                .isInstanceOf(ApiException.class) // 이행 요청 일자가 계약 시작 일자 이후인 경우
                .extracting("errorMessage")
                .isEqualTo("계약 이행 가능 일자가 아닙니다.");

        assertThatThrownBy(() -> contractService.progressContract(member, 1L, 1L, savedContract.getId(), beforeDay))
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when
        ContractDTO.StatusInfo updatedContract = contractService.expireContract(member, 1L, 1L, savedContract.getId(), today);

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.expireContract(member, 1L, 1L, savedContract.getId(), today))
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when
        ContractDTO.StatusInfo updatedContract = contractService.terminateContract(member, 1L, 1L, savedContract.getId(), today);

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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.terminateContract(member, 1L, 1L, savedContract.getId(), today))
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

        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> contractService.terminateContract(member, 1L, 1L, savedContract.getId(), today))
                .isInstanceOf(ApiException.class)
                .extracting("errorMessage")
                .isEqualTo("""
                        원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                        계약 종료 일자 보다 이후의 일자인 경우
                        계약 파기 상태로 변경할 수 없습니다.
                        """);

    }

    @Test
    @DisplayName("내 호실과 비교 대상 호실의 보증금/임대료를 비교할 수 있는 데이터를 조회할 수 있다.")
    void getContractCostInfoTest() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                200000000L,
                500000L,
                ContractType.INITIAL,
                room2,
                tenant2
        );
        Contract contract3 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                400000000L,
                500000L,
                ContractType.INITIAL,
                room3,
                tenant3
        );
        contractRepository.saveAll(List.of(contract1, contract2, contract3));

        contract1.contractComplete();
        contract1.contractInProgress();

        contract2.contractComplete();
        contract2.contractInProgress();

        contract3.contractComplete();
        contract3.contractInProgress();

        // when
        ContractDTO.CostInfo contractCostInfo = contractService.getContractCostInfo(member, 1L, room1.getId());

        // then
        assertThat(contractCostInfo)
                .extracting("deposit", "rentalPrice", "averageDeposit", "averageRentalPrice")
                .contains(
                        100000000L,
                        200000L,
                        300000000.0,
                        500000.0
                );

    }

    @Test
    @DisplayName("내 호실과 비교 대상 호실의 보증금/임대료를 비교할 수 있는 데이터를 조회시 비교 대상이 없다면 0.0을 반환한다.")
    void getContractCostInfoTestAverageDefaultValue() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                200000000L,
                500000L,
                ContractType.INITIAL,
                room2,
                tenant2
        );
        Contract contract3 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                400000000L,
                500000L,
                ContractType.INITIAL,
                room3,
                tenant3
        );
        contractRepository.saveAll(List.of(contract1, contract2, contract3));

        contract1.contractComplete();
        contract1.contractInProgress();

        contract2.contractComplete();

        contract3.contractComplete();

        // when
        ContractDTO.CostInfo contractCostInfo = contractService.getContractCostInfo(member, 1L, room1.getId());

        // then
        assertThat(contractCostInfo)
                .extracting("deposit", "rentalPrice", "averageDeposit", "averageRentalPrice")
                .contains(
                        100000000L,
                        200000L,
                        0.0,
                        0.0
                );

    }

    @Test
    @DisplayName("내 호실과 비교 대상 호실의 보증금/임대료를 비교할 수 있는 데이터를 조회시 계약 상태인 내 호실이 없는 경우 0을 반환한다.")
    void getContractCostInfoTestCostIsZero() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract contract2 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                200000000L,
                500000L,
                ContractType.INITIAL,
                room2,
                tenant2
        );
        Contract contract3 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                400000000L,
                500000L,
                ContractType.INITIAL,
                room3,
                tenant3
        );
        contractRepository.saveAll(List.of(contract1, contract2, contract3));

        contract1.contractComplete();

        contract2.contractComplete();
        contract2.contractInProgress();

        contract3.contractComplete();
        contract3.contractInProgress();

        // when
        ContractDTO.CostInfo contractCostInfo = contractService.getContractCostInfo(member, 1L, room1.getId());

        // then
        assertThat(contractCostInfo)
                .extracting("deposit", "rentalPrice", "averageDeposit", "averageRentalPrice")
                .contains(
                        0L,
                        0L,
                        300000000.0,
                        500000.0
                );

    }

    @Test
    @DisplayName("내 호실 및 비교 호실의 재계약률을 조회할 수 있다.")
    void getRenewalContractRateInfo() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        List<Contract> contracts1 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 3 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts1_1 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        /* ========== 아래 부터 비교 호실 데이터 구성 ========== */
        List<Contract> contracts2 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room2,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 3 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts3 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room3,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 3 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        // when
        ContractDTO.RenewalContractRateInfo renewalContractRateInfo = contractService.getRenewalContractRateInfo(member, 1L, room1.getId());

        // then
        assertThat(renewalContractRateInfo.getRenewalContractRate()).isEqualTo(80.0);
        assertThat(renewalContractRateInfo.getAverageRenewalContractRate()).isEqualTo(100.0);

    }

    @Test
    @DisplayName("내 호실의 재계약이 없을 경우 재계약률은 0.0이다.")
    void getRenewalContractRateInfoZeroPercent() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        List<Contract> contracts1 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts1_1 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts1_2 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        /* ========== 아래 부터 비교 호실 데이터 구성 ========== */
        List<Contract> contracts2 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room2,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 3 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts3 = IntStream.range(0, 3)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room3,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 3 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        // when
        ContractDTO.RenewalContractRateInfo renewalContractRateInfo = contractService.getRenewalContractRateInfo(member, 1L, room1.getId());

        // then
        assertThat(renewalContractRateInfo.getRenewalContractRate()).isEqualTo(0.0);
        assertThat(renewalContractRateInfo.getAverageRenewalContractRate()).isEqualTo(100.0);

    }

    @Test
    @DisplayName("비교 호실 추정 재계약률 평균값 일치 테스트.")
    void getRenewalContractRateInfo50Percent() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        List<Contract> contracts1 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts1_1 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts1_2 = IntStream.range(0, 1)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            ContractType.INITIAL,
                            room1,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        /* ========== 아래 부터 비교 호실 데이터 구성 ========== */
        List<Contract> contracts2 = IntStream.range(0, 2)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room2,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 2 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts2_1 = IntStream.range(0, 2)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room2,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 2 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts2_2 = IntStream.range(0, 2)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room2,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 2 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts3 = IntStream.range(0, 2)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room3,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 2 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        List<Contract> contracts3_1 = IntStream.range(0, 2)
                .mapToObj(i -> {
                    long startDate = 10L * i + 1;
                    long endDate = 10L * (i + 1);
                    ContractType contractType = (i == 0) ? ContractType.INITIAL : ContractType.RENEWAL;

                    Contract contract = assumeContract(
                            LocalDate.now().plusDays(startDate),
                            LocalDate.now().plusDays(endDate),
                            100000000L,
                            200000L,
                            contractType,
                            room3,
                            tenant1
                    );
                    Contract savedContract = contractRepository.save(contract);

                    if (i < 2 - 1) {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                        savedContract.contractExpire();
                    } else {
                        savedContract.contractComplete();
                        savedContract.contractInProgress();
                    }

                    return contract;
                })
                .collect(Collectors.toList());

        // when
        ContractDTO.RenewalContractRateInfo renewalContractRateInfo = contractService.getRenewalContractRateInfo(member, 1L, room1.getId());

        // then
        assertThat(renewalContractRateInfo.getRenewalContractRate()).isEqualTo(0.0);
        assertThat(renewalContractRateInfo.getAverageRenewalContractRate()).isEqualTo(63.4);

    }

    @Test
    @DisplayName("내 호실의 공실률을 조회할 수 있다.")
    void getContractVacancyRateInfo() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        LocalDate startDate = LocalDate.of(2024, 10, 30).minusYears(1).plusDays(1);

        Contract contract1_1 = assumeContract(
                LocalDate.of(2023, 10, 31),
                LocalDate.of(2024, 10, 30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract contract1_2 = assumeContract(
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract contract1_3 = assumeContract(
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        contractRepository.saveAll(List.of(contract1_1, contract1_2, contract1_3));

        contract1_1.contractComplete();
        contract1_1.contractInProgress();
        contract1_1.contractExpire();

        contract1_2.contractComplete();
        contract1_2.contractInProgress();
        contract1_2.contractExpire();

        contract1_3.contractComplete();
        contract1_3.contractInProgress();

        // when
        ContractDTO.VacancyRateInfo contractVacancyRateInfo = contractService.getContractVacancyRateInfo(member, 1L, room1.getId(), startDate);

        // then
        assertThat(contractVacancyRateInfo.getVacancyRate()).isEqualTo(0.0);

    }

    @Test
    @DisplayName("진행 중인 계약 기간이 1년이 넘는 기간이어서 조회 범위를 넘길 시 공실률은 0으로 산출된다.")
    void getContractVacancyRateInfoExceptionCheck() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        LocalDate startDate = LocalDate.of(2024, 6, 9).minusYears(1);

        Contract contract1_3 = assumeContract(
                LocalDate.of(2023, 5, 29),
                LocalDate.of(2024, 9, 11),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room2,
                tenant1
        );
        contractRepository.saveAll(List.of(contract1_3));

        contract1_3.contractComplete();
        contract1_3.contractInProgress();

        // when
        ContractDTO.VacancyRateInfo contractVacancyRateInfo = contractService.getContractVacancyRateInfo(member, 1L, room2.getId(), startDate);

        // then
        assertThat(contractVacancyRateInfo.getVacancyRate()).isEqualTo(0.0);

    }

    @Test
    @DisplayName("종료된 계약의 기간이 1년이 넘는 기간이어도 검색 범위의 시작부터 종료일까지를 기반으로 결과가 산출된다.")
    void getContractVacancyRateInfoExceptionCheck2() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        LocalDate startDate = LocalDate.of(2024, 6, 9).minusYears(1).plusDays(1);

        Contract contract1_3 = assumeContract(
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2024, 6, 1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room2,
                tenant1
        );
        contractRepository.saveAll(List.of(contract1_3));

        contract1_3.contractComplete();
        contract1_3.contractInProgress();
        contract1_3.contractExpire();

        // when
        ContractDTO.VacancyRateInfo contractVacancyRateInfo = contractService.getContractVacancyRateInfo(member, 1L, room2.getId(), startDate);

        // then
        assertThat(contractVacancyRateInfo.getVacancyRate()).isEqualTo(2.2);

    }

    @Test
    @DisplayName("비교 호실의 공실률 평균을 산출할 수 있다.")
    void getContractVacancyRateInfoRelative() {
        // given
        MemberDTO.Info member = MemberDTO.Info.builder()
                .id(2L)
                .role(Role.OWNER)
                .build();

        LocalDate startDate = LocalDate.of(2024, 10, 30).minusYears(1);

        Contract contract1_1 = assumeContract(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 9, 30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room2,
                tenant1
        );

        Contract contract1_2 = assumeContract(
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room2,
                tenant1
        );

        Contract contract1_3 = assumeContract(
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room2,
                tenant1
        );
        contractRepository.saveAll(List.of(contract1_1, contract1_2, contract1_3));

        contract1_1.contractComplete();
        contract1_1.contractInProgress();
        contract1_1.contractExpire();

        contract1_2.contractComplete();
        contract1_2.contractInProgress();
        contract1_2.contractExpire();

        contract1_3.contractComplete();
        contract1_3.contractInProgress();

        Contract contract2_1 = assumeContract(
                LocalDate.of(2023, 10, 30),
                LocalDate.of(2024, 10, 29),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room3,
                tenant1
        );

        Contract contract2_2 = assumeContract(
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room3,
                tenant1
        );

        Contract contract2_3 = assumeContract(
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room3,
                tenant1
        );
        contractRepository.saveAll(List.of(contract2_1, contract2_2, contract2_3));

        contract2_1.contractComplete();
        contract2_1.contractInProgress();
        contract2_1.contractExpire();

        contract2_2.contractComplete();
        contract2_2.contractInProgress();
        contract2_2.contractExpire();

        contract2_3.contractComplete();
        contract2_3.contractInProgress();

        // when
        ContractDTO.VacancyRateInfo contractVacancyRateInfo = contractService.getContractVacancyRateInfo(member, 1L, room1.getId(), startDate);

        // then
        assertThat(contractVacancyRateInfo.getAverageVacancyRate()).isEqualTo(12.6);

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
