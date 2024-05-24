package com.core.back9.repository;

import com.core.back9.entity.Contract;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query(value = """
            select c
            from Contract c
            where c.status=?2 and c.room.id=?1
            """,
    countQuery = "select count(c) from Contract c where c.room.id=?1 and c.status=?2")
    Page<Contract> selectAllRegisteredContract(Long roomId, Status status, Pageable pageable);

    @Query("""
            select c
            from Contract c
            where c.room.id=?1
            and c.id=?2
            and c.status='REGISTER'
            """)
    Optional<Contract> getOneTenant(Long roomId, Long contractId);

    default Contract getValidOneContractOrThrow(Long roomId, Long contractId) {
        return getOneTenant(roomId, contractId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_CONTRACT));
    }

    @Modifying(clearAutomatically = true) // 영속성 컨텍스트를 비워 db로부터 정상조회 가능
    @Query("""
            update Contract c
            set c.status=?1
            where c.id=?3
            and c.room.id=?2
            and c.status='REGISTER'
            """)
    Optional<Integer> deleteRegisteredContract(Status status, Long roomId, Long contractId);

    @Query("""
            select c
            from Contract c
            where c.room.id=?1
            and c.tenant.id=?2
            and c.status='REGISTER'
            and c.contractType=?3
            """)
    Optional<Contract> findByContractRoomIdAndTenantId(Long roomId, Long tenantId, ContractType initial);

    @Query("""
            select c
            from Contract c
            where c.id=?1
            and c.contractType=?2
            """)
    Optional<Contract> findByContractInitial(Long contractId, ContractType contractType);
}