package com.core.back9.mapper;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.constant.ContractStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class ContractMapperTest {

    private final ContractMapper contractMapper = Mappers.getMapper(ContractMapper.class);

    @Test
    @DisplayName("contract의 registerRequest를 entity로 변환할 수 있다.")
    void contractRegisterRequestToEntity() {
        // given
        ContractDTO.RegisterRequest request = ContractDTO.RegisterRequest.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .deposit(100000000L)
                .rentalPrice(200000L)
                .build();

        // when
        Contract contract = contractMapper.toEntity(request);

        // then
        assertThat(contract)
                .extracting("startDate", "endDate", "deposit", "rentalPrice")
                .contains(LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1),
                        100000000L,
                        200000L);
    }

    @Test
    @DisplayName("contract의 UpdateRequest를 entity로 변환할 수 있다.")
    void contractUpdateRequestToEntity() {
        // given
        ContractDTO.UpdateRequest request = ContractDTO.UpdateRequest.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(1))
                .deposit(100000000L)
                .rentalPrice(200000L)
                .build();

        // when
        Contract contract = contractMapper.toEntity(request);

        // then
        assertThat(contract)
                .extracting("startDate", "endDate", "deposit", "rentalPrice")
                .contains(LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1),
                        100000000L,
                        200000L);
    }



    @Test
    @DisplayName("contract의 entity를 response로 변환할 수 있다.")
    void contractEntityToResponse() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L
        );

        // when
        ContractDTO.RegisterResponse response = contractMapper.toRegisterResponse(contract);

        // then
        assertThat(response)
                .extracting("startDate", "endDate", "checkOut", "deposit", "rentalPrice", "contractStatus")
                .contains(
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1),
                        100000000L,
                        200000L,
                        ContractStatus.PENDING
                );

    }

    @Test
    @DisplayName("contract의 entity를 Info로 변환할 수 있다.")
    void contractEntityToInfo() {
        // given
        Contract contract = assumeContract(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L
        );

        // when
        ContractDTO.Info info = contractMapper.toInfo(contract);

        // then
        assertThat(info)
                .extracting("startDate", "endDate", "checkOut", "deposit", "rentalPrice", "contractStatus")
                .contains(
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1),
                        100000000L,
                        200000L,
                        ContractStatus.PENDING
                );

    }

    @Test
    @DisplayName("contract의 Info를 InfoList로 변환할 수 있다.")
    void contractInfoToInfoList() {
        // given
        ContractDTO.Info info1 = assumeContractInfo(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1),
                100000000L,
                200000L
        );

        ContractDTO.Info info2 = assumeContractInfo(
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1),
                110000000L,
                300000L
        );

        ContractDTO.Info info3 = assumeContractInfo(
                3L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1),
                120000000L,
                400000L
        );

        List<ContractDTO.Info> infoList = List.of(info1, info2, info3);

        // when
        ContractDTO.InfoList mapperInfoList = contractMapper.toInfoList(
                infoList.stream().count(),
                infoList
        );

        // then
        assertThat(mapperInfoList.getCount()).isEqualTo(3L);

        assertThat(mapperInfoList.getInfoList())
                .extracting("id", "startDate", "endDate", "checkOut", "deposit", "rentalPrice", "contractStatus")
                .containsExactlyInAnyOrder(
                        tuple(
                                1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                100000000L,
                                200000L,
                                ContractStatus.PENDING
                        ),tuple(
                                2L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                110000000L,
                                300000L,
                                ContractStatus.PENDING
                        ),tuple(
                                3L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(1),
                                120000000L,
                                400000L,
                                ContractStatus.PENDING
                        )

                );

    }

    private Contract assumeContract(
            LocalDate startDate,
            LocalDate endDate,
            Long deposit,
            Long rentalPrice) {

        return Contract.builder()
                .startDate(startDate)
                .endDate(endDate)
                .deposit(deposit)
                .rentalPrice(rentalPrice)
                .build();

    }

    private ContractDTO.Info assumeContractInfo(
            Long id,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate checkOut,
            Long deposit,
            Long rentalPrice
    ) {

        return ContractDTO.Info.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .checkOut(checkOut)
                .deposit(deposit)
                .rentalPrice(rentalPrice)
                .contractStatus(ContractStatus.PENDING)
                .build();

    }


}