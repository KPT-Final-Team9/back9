package com.core.back9.repository;

import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {


    @Query("""
            select t
            from Tenant t
            where t.status=?1
            """)
    Page<Tenant> selectAllByStatus(Status status, Pageable pageable);

    @Query("""
            select t
            from Tenant t
            where t.id=?2 and t.status=?1
            """)
    Optional<Tenant> getOneTenant(Status status, Long tenantId);

    default Tenant getValidOneTenantOrThrow(Status status, Long tenantId) {
        return getOneTenant(status, tenantId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_TENANT));
    }

    @Modifying(clearAutomatically = true) // 영속성 컨텍스트를 비워 db로부터 정상조회 가능
    @Query("""
            update Tenant t
            set t.status=?1
            where t.id=?2 and t.status='REGISTER'
            """)
    Optional<Integer> deleteRegisteredTenant(Status status, Long tenantId);
}
