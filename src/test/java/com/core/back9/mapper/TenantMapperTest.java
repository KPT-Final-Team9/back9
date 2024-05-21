package com.core.back9.mapper;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;

public class TenantMapperTest {

    TenantMapper tenantMapper = Mappers.getMapper(TenantMapper.class);

    @Test
    @DisplayName("tenant의 request를 entity로 변환할 수 있다.")
    void tenantRequestToEntity() {
        // given
        TenantDTO.RegisterRequest registerRequest = TenantDTO.RegisterRequest
                .builder()
                .name("입주사1")
                .companyNumber("02-000-0000")
                .build();

        // when
        Tenant tenant = tenantMapper.toEntity(registerRequest);

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
        TenantDTO.RegisterResponse response = tenantMapper.toRegisterResponse(tenant);

        // then
        assertThat(response)
                .extracting("name", "companyNumber")
                .containsExactly("입주사1", "02-000-0000");

    }

    @Test
    @DisplayName("tenantInfo의 List와 Count를 TenantInfoList에 매핑할 수 있다.")
    void tenantInfoToInfoList() {
        // given
        Tenant tenant1 = assumeTenant("입주사1", "02-000-0000");
        Tenant tenant2 = assumeTenant("입주사2", "02-000-0001");
        Tenant tenant3 = assumeTenant("입주사3", "02-000-0002");

        List<Tenant> tenantList = List.of(tenant1, tenant2, tenant3);

        List<TenantDTO.Info> tenantInfoList = tenantList.stream()
                .map(tenantMapper::toInfo)
                .collect(Collectors.toList());

        // when
        TenantDTO.InfoList infoList = tenantMapper.toInfoList(3L, tenantInfoList);

        // then
        assertThat(infoList.getInfoList()).extracting("name", "companyNumber")
                .containsExactlyInAnyOrder(
                        tuple("입주사1", "02-000-0000"),
                        tuple("입주사2", "02-000-0001"),
                        tuple("입주사3", "02-000-0002")
                );

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
        TenantDTO.RegisterResponse response = tenantMapper.toRegisterResponse(tenant);

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

    private Tenant assumeTenant(String name, String companyNumber) {
        return Tenant.builder()
                .name(name)
                .companyNumber(companyNumber)
                .build();
    }

}
