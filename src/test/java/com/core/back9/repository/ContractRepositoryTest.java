package com.core.back9.repository;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import com.core.back9.repository.fixture.ContractRepositoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ContractRepositoryTest extends ContractRepositoryFixture {

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @DisplayName("room과 tenant와 매핑 관계가 형성된 contract를 생성할 수 있다.")
    void findByRoomIdAndTenantId() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        // when
        Contract savedContract = contractRepository.save(contract);


        // then
        assertThat(savedContract)
                .extracting("id", "room.id", "tenant.id")
                .contains(1L, 1L, 1L);

    }

    @Test
    @DisplayName("원하는 계약 리스트를 정해진 페이징 단위로 조회할 수 있다.")
    void selectAllRegisteredContract() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 4);

        Contract contract1 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract contract2 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                110000000L,
                300000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract contract3 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                120000000L,
                400000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        contractRepository.saveAll(List.of(contract1, contract2, contract3));

        // when
        Page<Contract> pageContracts = contractRepository.selectAllRegisteredContract(room1.getId(), Status.REGISTER, pageRequest);

        // then
        assertThat(pageContracts)
                .extracting("id", "startDate", "endDate", "checkOut", "deposit", "rentalPrice", "contractStatus")
                .containsExactlyInAnyOrder(
                        tuple(
                                1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                100000000L,
                                200000L,
                                ContractStatus.PENDING),
                        tuple(
                                2L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                110000000L,
                                300000L,
                                ContractStatus.PENDING),
                        tuple(
                                3L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                120000000L,
                                400000L,
                                ContractStatus.PENDING)

                );

    }

    @Test
    @DisplayName("계약 리스트가 존재하지 않는 경우 empty 객체를 반환한다.")
    void selectAllRegisteredContractIsEmpty() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 4);

        // when
        Page<Contract> pageContracts = contractRepository.selectAllRegisteredContract(room1.getId(), Status.REGISTER, pageRequest);

        // then
        assertThat(pageContracts).isEmpty();

    }

    @Test
    @DisplayName("원하는 하나의 Contract만 조회할 수 있다.")
    void getOneContract() {
        // given
        Contract contract1 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract contract2 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.RENEWAL,
                room1,
                tenant1
        );
        Contract contract3 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.RENEWAL,
                room1,
                tenant1
        );

        List<Contract> contracts = contractRepository.saveAll(List.of(contract1, contract2, contract3));

        // when
        Contract contract = contractRepository.getValidOneContractOrThrow(room1.getId(), contracts.get(1).getId());

        // then
        assertThat(contract)
                .extracting("id", "status", "contractStatus", "contractType")
                .contains(2L, Status.REGISTER, ContractStatus.PENDING, ContractType.RENEWAL);
    }

    @Test
    @DisplayName("Contract의 매핑 관계를 확인할 수 있다.")
    void contractMappedRoomAndTenant() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        // when
        Contract selectedContract = contractRepository.getValidOneContractOrThrow(room1.getId(), savedContract.getId());

        // then
        assertThat(contract)
                .extracting("id", "status", "contractStatus", "contractType")
                .contains(1L, Status.REGISTER, ContractStatus.PENDING, ContractType.INITIAL);

        assertThat(selectedContract)
                .extracting("room.id", "room.name", "room.floor", "room.area", "room.usage", "room.status")
                .contains(
                        1L,
                        "호실1",
                        "1층",
                        0.0f,
                        Usage.OFFICES,
                        Status.REGISTER);

        assertThat(selectedContract)
                .extracting("tenant.id", "tenant.name", "tenant.companyNumber", "tenant.status")
                .contains(1L,
                        "입주사1",
                        "02-000-0000",
                        Status.REGISTER);

    }

    @Test
    @DisplayName("원하는 contract의 내용을 수정할 수 있다.")
    void UpdateContractInfo() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        ContractDTO.UpdateRequest request = ContractDTO.UpdateRequest.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .deposit(200000000L)
                .rentalPrice(400000L)
                .build();

        // when
        Contract updatedContract = savedContract.infoUpdate(request);

        // then
        assertThat(updatedContract)
                .extracting("id", "startDate", "checkOut", "deposit", "rentalPrice", "status", "contractStatus", "contractType")
                .contains(1L,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2),
                        200000000L,
                        400000L,
                        Status.REGISTER,
                        ContractStatus.PENDING,
                        ContractType.INITIAL
                );

    }

    @Test
    @DisplayName("선택한 호실에 계약 시작을 원하는 날짜와 겹치는 일자의 계약이 존재하는지 조회할 수 있다.")
    void findByContractDuplicateCase1() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );


        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();

        LocalDate contractStartDate = LocalDate.now().plusDays(19);
        LocalDate contractEndDate = LocalDate.now().plusDays(39);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).hasSize(1);
        assertThat(contractList.get())
                .extracting("startDate", "endDate", "contractStatus")
                .contains(
                        tuple(
                                LocalDate.now().plusDays(10),
                                LocalDate.now().plusDays(20),
                                ContractStatus.COMPLETED
                        ));

    }

    @Test
    @DisplayName("선택한 호실에 계약 종료를 원하는 날짜와 겹치는 일자의 계약이 존재하는지 조회할 수 있다.")
    void findByContractDuplicateCase2() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );


        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();

        LocalDate contractStartDate = LocalDate.now().plusDays(19);
        LocalDate contractEndDate = LocalDate.now().plusDays(39);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).hasSize(1);
        assertThat(contractList.get())
                .extracting("startDate", "endDate", "contractStatus")
                .contains(
                        tuple(
                                LocalDate.now().plusDays(10),
                                LocalDate.now().plusDays(20),
                                ContractStatus.COMPLETED
                        ));

    }

    @Test
    @DisplayName("선택한 호실에 계약 시작을 원하는 날짜와 겹치는 일자의 계약이 2개 이상 존재하는지 조회할 수 있다.(필터링 조건 동시충족)")
    void findByContractDuplicateCase3() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract1 = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract savedContract1 = contractRepository.save(contract1);
Contract contract2 = assumeContract(
                LocalDate.now().plusDays(30),
                LocalDate.now().plusDays(40),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );
        Contract savedContract2 = contractRepository.save(contract2);


        savedContract1.contractComplete();
        savedContract2.contractComplete();

        LocalDate contractStartDate = LocalDate.now().plusDays(19);
        LocalDate contractEndDate = LocalDate.now().plusDays(39);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).hasSize(2);
        assertThat(contractList.get())
                .extracting("startDate", "endDate", "contractStatus")
                .contains(
                        tuple(
                                LocalDate.now().plusDays(10),
                                LocalDate.now().plusDays(20),
                                ContractStatus.COMPLETED
                        ));

    }

    @Test
    @DisplayName("선택한 호실에 계약을 원하는 날짜와 겹치는 일자의 계약이 존재하는지 조회할 수 있다.")
    void findByContractDuplicateCase4() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract = assumeContract(
                LocalDate.now().plusDays(14),
                LocalDate.now().plusDays(26),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );


        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();

        LocalDate contractStartDate = LocalDate.now().plusDays(15);
        LocalDate contractEndDate = LocalDate.now().plusDays(25);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).hasSize(1);
        assertThat(contractList.get())
                .extracting("startDate", "endDate", "contractStatus")
                .contains(
                        tuple(
                                LocalDate.now().plusDays(14),
                                LocalDate.now().plusDays(26),
                                ContractStatus.COMPLETED
                        ));

    }

    @Test
    @DisplayName("선택한 호실에 계약 시작을 원하는 날짜와 겹치는 일자의 계약이 없다면 빈 리스트를 반환한다.")
    void findByContractDuplicateEmpty() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(30),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();

        LocalDate contractStartDate = LocalDate.now().plusDays(8);
        LocalDate contractEndDate = LocalDate.now().plusDays(9);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).isEmpty();

    }

    @Test
    @DisplayName("선택한 호실에 계약 시작을 원하는 날짜와 겹치는 일자의 계약이 존재해도 정상 상태가 아닐 시 조회되지 않는다.")
    void findByContractDuplicateAbnormal() {
        // given
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);
        Contract contract = assumeContract(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                100000000L,
                200000L,
                ContractType.INITIAL,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);
        savedContract.contractComplete();
        savedContract.contractInProgress();
        savedContract.contractExpire();

        LocalDate contractStartDate = LocalDate.now().plusDays(19);
        LocalDate contractEndDate = LocalDate.now().plusDays(39);

        // when
        Optional<List<Contract>> contractList = contractRepository.findByContractDuplicate(room1.getId(), statusList, contractStartDate, contractEndDate);

        // then
        assertThat(contractList.get()).isEmpty();

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