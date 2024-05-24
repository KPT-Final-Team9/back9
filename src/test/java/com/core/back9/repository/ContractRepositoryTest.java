package com.core.back9.repository;

import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import com.core.back9.repository.fixture.ContractFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class ContractRepositoryTest extends ContractFixture {

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
                room1,
                tenant1
        );
        contractRepository.save(contract);

        // when
        Optional<Contract> mappedContract = contractRepository.findByContractRoomIdAndTenantId(
                room1.getId(),
                tenant1.getId(), ContractType.INITIAL);

        // then
        assertThat(mappedContract.get().getRoom().getId()).isEqualTo(1L);

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
                room1,
                tenant1
        );
        contractRepository.save(contract1);

        Contract contract2 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                110000000L,
                300000L,
                room1,
                tenant1
        );
        contractRepository.save(contract2);

        Contract contract3 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                120000000L,
                400000L,
                room1,
                tenant1
        );
        contractRepository.save(contract3);

        // when
        Page<Contract> pageContracts = contractRepository.selectAllRegisteredContract(room1.getId(), Status.REGISTER, pageRequest);

        // then
        assertThat(pageContracts)
                .extracting("id", "startDate", "endDate", "checkOut", "deposit", "rentalPrice", "contractStatus")
                .containsExactlyInAnyOrder(
                        tuple(1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                100000000L,
                                200000L,
                                ContractStatus.PENDING),
                        tuple(2L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                110000000L,
                                300000L,
                                ContractStatus.PENDING),
                        tuple(3L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                120000000L,
                                400000L,
                                ContractStatus.PENDING)

                );

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
                room1,
                tenant1
        );
        Contract contract2 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                room1,
                tenant1
        );
        Contract contract3 = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                room1,
                tenant1
        );

        List<Contract> contracts = contractRepository.saveAll(List.of(contract1, contract2, contract3));

        // when
        Contract contract = contractRepository.getValidOneContractOrThrow(room1.getId(), contracts.get(1).getId());

        // then
        assertThat(contract)
                .extracting("id", "status", "contractStatus")
                .contains(2L, Status.REGISTER, ContractStatus.PENDING);
    }

    @Test
    @DisplayName("Contract의 매핑 관계를 확인할 수 있다.")
    void test() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L,
                room1,
                tenant1
        );

        Contract savedContract = contractRepository.save(contract);

        // when
        Contract selectedContract = contractRepository.getValidOneContractOrThrow(room1.getId(), savedContract.getId());

        // then
        assertThat(contract)
                .extracting("id", "status", "contractStatus")
                .contains(1L, Status.REGISTER, ContractStatus.PENDING);

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

    private Contract assumeContract(
            LocalDate startDate,
            LocalDate endDate,
            Long deposit,
            Long rentalPrice,
            Room room,
            Tenant tenant) {

        return Contract.builder()
                .startDate(startDate)
                .endDate(endDate)
                .deposit(deposit)
                .rentalPrice(rentalPrice)
                .room(room)
                .tenant(tenant)
                .build();

    }
}