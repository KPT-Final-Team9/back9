package com.core.back9.mapper;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class TenantMapperTest {

    private final TenantMapper tenantMapper = Mappers.getMapper(TenantMapper.class);

    @Test
    @DisplayName("tenant의 request를 entity로 변환할 수 있다.")
    void tenantRequestToEntity() {
        // given
        TenantDTO.Request request = TenantDTO.Request
                .builder()
                .name("입주사1")
                .companyNumber("02-000-0000")
                .build();

        // when
        Tenant tenant = tenantMapper.toEntity(request);

        // then
        assertThat(tenant)
                .extracting("name", "companyNumber")
                .containsExactly("입주사1", "02-000-0000");

    }

    @Test
    @DisplayName("tenant의 entity를 response로 변환할 수 있다.")
    void tenantEntityToResponse() {
        // given
        Tenant tenant = assumeTenant("입주사1", "02-000-0000");

        // when
        TenantDTO.Response response = tenantMapper.toRegisterResponse(tenant);

        // then
        assertThat(response)
                .extracting("name", "companyNumber")
                .containsExactly("입주사1", "02-000-0000");

    }

    @Test
    @DisplayName("tenant의 entity를 Info로 변환할 수 있다.")
    void tenantEntityToInfo() {
        // given
        Tenant tenant = assumeTenant("입주사1", "02-000-0000");

        // when
        TenantDTO.Info info = tenantMapper.toInfo(tenant);

        // then
        assertThat(info)
                .extracting("name", "companyNumber")
                .containsExactly("입주사1", "02-000-0000");

    }

    @Test
    @DisplayName("entity에서 response로 매필되는 필드 중 값이 직접적으로 매핑되지 않는 필드는 null이다.")
    void isNullForUnmappedFieldsResponse() {
        Tenant tenant = assumeTenant("입주사1", "02-000-0000");

        // when
        TenantDTO.Response response = tenantMapper.toRegisterResponse(tenant);

        // then
        assertThat(response)
                .extracting("id", "createdAt")
                .containsExactly(null, null);

    }

    @Test
    @DisplayName("entity에서 info로 매핑되는 필드 중 값이 직접적으로 매핑되지 않는 필드는 null이다.")
    void isNullForUnmappedFieldsInfo() {
        Tenant tenant = assumeTenant("입주사1", "02-000-0000");

        // when
        TenantDTO.Info info = tenantMapper.toInfo(tenant);

        // then
        assertThat(info)
                .extracting("id", "createdAt", "updatedAt")
                .containsExactly(null, null, null);

    }

    @Test
    @DisplayName("tenant의 info를 infoList로 변환할 수 있다.")
    void tenantInfoToInfoList() {
        // given
        TenantDTO.Info info1 = assumeTenantInfo(1L, "입주사1", "02-000-0000");
        TenantDTO.Info info2 = assumeTenantInfo(2L, "입주사2", "02-000-0001");
        TenantDTO.Info info3 = assumeTenantInfo(3L, "입주사3", "02-000-0002");

        List<TenantDTO.Info> infoList = List.of(info1, info2, info3);

        // when
        TenantDTO.InfoList mapperInfoList = tenantMapper.toInfoList(infoList.stream().count(), infoList);

        // then
        assertThat(mapperInfoList.getCount()).isEqualTo(3L);

        assertThat(mapperInfoList.getInfoList())
                .extracting("id", "name", "companyNumber")
                .containsExactlyInAnyOrder(
                        tuple(1L, "입주사1", "02-000-0000"),
                        tuple(2L, "입주사2", "02-000-0001"),
                        tuple(3L, "입주사3", "02-000-0002")
                );

    }

    private Tenant assumeTenant(
            String name
            , String companyNumber) {

        return Tenant.builder()
                .name(name)
                .companyNumber(companyNumber)
                .build();

    }

    private TenantDTO.Info assumeTenantInfo(
            Long id,
            String name,
            String companyNumber) {

        return TenantDTO.Info.builder()
                .id(id)
                .name(name)
                .companyNumber(companyNumber)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now().plusDays(1))
                .build();

    }




}
