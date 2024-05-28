package com.core.back9.repository;

import com.core.back9.entity.Contract;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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

    // TODO : 기간 검증 로직에 대해 추가적인 고민 해보기
    @Query("""
            select c
            from Contract c
            where c.room.id=?1
            and c.contractStatus IN (?2)
            and((c.endDate>?3 and c.startDate<?3) or (c.startDate <?4 and c.endDate>?4))
            """)
    Optional<List<Contract>> findByContractDuplicate(Long roomId, List<ContractStatus> statusList, LocalDate startDate, LocalDate endDate);

    @Query("""
            select c
            from Contract c
            where c.id=?1
            """)
    Optional<Contract> findByContractId(Long contractId);

}