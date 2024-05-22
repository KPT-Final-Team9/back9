package com.core.back9.repository;

import com.core.back9.entity.Contract;
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
            where c.status=?1
            """,
    countQuery = "select count(c) from Contract c where c.status=?1")
    Page<Contract> selectAllRegisteredContract(Status status, Pageable pageable);

    @Query("""
            select c
            from Contract c
            where c.id=?2 and c.status=?1
            """)
    Optional<Contract> getOneTenant(Status status, Long contractId);

    default Contract getValidOneContractOrThrow(Status status, Long contractId) {
        return getOneTenant(status, contractId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_CONTRACT));
    }

    @Modifying(clearAutomatically = true) // 영속성 컨텍스트를 비워 db로부터 정상조회 가능
    @Query("""
            update Contract c
            set c.status=?1
            where c.id=?2 and c.status='REGISTER'
            """)
    Optional<Integer> deleteRegisteredContract(Status status, Long contractId);
}