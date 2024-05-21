package com.core.back9.repository;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    @DisplayName("원하는 하나의 Tenant만 조회할 수 있다.")
    void getOneTenant() {
        // given
        Tenant tenant1 = assumeTenant("입주사1", "02-000-0000");
        Tenant tenant2 = assumeTenant("입주사2", "02-000-0001");
        Tenant tenant3 = assumeTenant("입주사3", "02-000-0002");

        List<Tenant> tenants = tenantRepository.saveAll(List.of(tenant1, tenant2, tenant3));

        // when
        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(Status.REGISTER, tenants.get(1).getId());

        // then
        assertThat(tenant.getId()).isEqualTo(tenants.get(1).getId());

    }

    @Test
    @DisplayName("원하는 게시물 리스트를 정해진 페이징 단위로 조회할 수 있다.")
    void selectAllByStatusPaging() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10); // (pageNumber, pageSize)

        Tenant tenant1 = assumeTenant("입주사1", "02-000-0000");
        Tenant tenant2 = assumeTenant("입주사2", "02-000-0001");
        Tenant tenant3 = assumeTenant("입주사3", "02-000-0002");

        tenantRepository.saveAll(List.of(tenant1, tenant2, tenant3));

        // when
        Page<Tenant> tenants = tenantRepository.selectAllByStatus(Status.REGISTER, pageRequest);

        // then
        assertThat(tenants)
                .extracting("name", "companyNumber")
                .containsExactly(
                        tuple("입주사1", "02-000-0000"),
                        tuple("입주사2", "02-000-0001"),
                        tuple("입주사3", "02-000-0002")
                );

    }

    @Test
    @DisplayName("데이터가 없는 리스트를 조회하게 되면 빈 값이 반환된다.")
    void selectAllByStatusPagingEmpty() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10); // (pageNumber, pageSize)

        // when
        Page<Tenant> tenants = tenantRepository.selectAllByStatus(Status.REGISTER, pageRequest);

        // then
        assertThat(tenants).isEmpty();

    }

    @Test
    @DisplayName("원하는 Tenant를 수정할 수 있다.")
    void modifyTenant() {
        // given
        Tenant tenant1 = assumeTenant("입주사1", "02-000-0000");
        Tenant tenant2 = assumeTenant("입주사2", "02-000-0001");
        Tenant tenant3 = assumeTenant("입주사3", "02-000-0002");

        TenantDTO.RegisterRequest request = TenantDTO.RegisterRequest.builder()
                .name("입주사4")
                .companyNumber("02-000-0003")
                .build();

        List<Tenant> tenants = tenantRepository.saveAll(List.of(tenant1, tenant2, tenant3));

        // when
        tenantRepository.getValidOneTenantOrThrow(Status.REGISTER, tenants.get(1).getId());
        tenant2.update(request);

        // then
        assertThat(tenant2).extracting("name", "companyNumber")
                .contains("입주사4", "02-000-0003");

    }

    @Test
    @DisplayName("원하는 Tenant를 삭제할 수 있다.")
    void deleteTenant() {
        // given
        Tenant tenant1 = assumeTenant("입주사1", "02-000-0000");
        Tenant tenant2 = assumeTenant("입주사2", "02-000-0001");
        Tenant tenant3 = assumeTenant("입주사3", "02-000-0002");

        tenantRepository.saveAll(List.of(tenant1, tenant2, tenant3));

        // when
        Integer deleteResult = tenantRepository.deleteRegisteredTenant(Status.UNREGISTER, tenant2.getId())
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

        // then
        assertThat(deleteResult).isEqualTo(1);

    }

    private Tenant assumeTenant(String name, String companyNumber) {
        return Tenant.builder()
                .name(name)
                .companyNumber(companyNumber)
                .build();
    }

}