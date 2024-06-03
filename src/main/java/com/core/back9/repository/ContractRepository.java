package com.core.back9.repository;

import com.core.back9.entity.Contract;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            and c.contractStatus in (?2)
            and((c.endDate>?3 and c.startDate<?3) or (c.startDate <?4 and c.endDate>?4))
            """)
    Optional<List<Contract>> findByContractDuplicate(Long roomId, List<ContractStatus> statusList, LocalDate startDate, LocalDate endDate);

    @Query("""
            select c
            from Contract c
            where c.id=?1
            """)
    Optional<Contract> findByContractId(Long contractId);

    @Query("""
            select c
            from Contract c
            where c.contractStatus='COMPLETED'
            and c.startDate>=?1
            """)
    List<Contract> findByContractComplete(LocalDate now);

    @Query("""
            select c
            from Contract c
            where c.contractStatus='IN_PROGRESS'
            and c.endDate=?1
            """)
    List<Contract> findByContractInProgress(LocalDate now);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Contract c
            set c.contractStatus='IN_PROGRESS'
            where c.contractStatus='COMPLETED'
            and c.endDate>=?1
            """)
    int updateContractComplete(LocalDate now);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Contract c
            set c.contractStatus='EXPIRED'
            where c.contractStatus='IN_PROGRESS'
            and c.endDate=?1
            """)
    int updateContractInProgress(LocalDate now);

    /* 내 호실의 이행 상태인 계약 중 가장 최신 계약 조회(데이터 조회 오차를 고려 desc 사용-> 순서상 가장 최신 데이터 조회) */
    @Query("""
            select c
            from Contract c
            where c.room.id=?1
            and c.contractStatus='IN_PROGRESS'
            and c.status='REGISTER'
            order by c.id desc
            """)
    Contract findByLatestContract(Long roomId);

    /* 내가 선택한 계약을 제외한 선택된 빌딩의 모든 호실의 이행 중인 계약 내용 조회 */
    @Query("""
        select c
        from Contract c
        where c.room.building.id = ?1
        and c.contractStatus = 'IN_PROGRESS'
        and (?2 is null or c.id <> ?2)
        and c.status = 'REGISTER'
        """)
    List<Contract> findByContractInProgressAllRoomsPerBuilding(Long buildingId, Long contractId);

    /*계약 대기, 취소를 제외한 선택한 호실의 모든 계약 내용 조회 */
    @Query("""
            select c
            from Contract c
            where c.room.id=?1
            and c.contractStatus not in (?2)
            and c.status = 'REGISTER'
            """)
    List<Contract> findByAllContractPerRoom(Long roomId, List<ContractStatus> statusList);

    /*계약 대기, 취소를 제외, 선택된 호실을 제외한 해당 빌딩의 호실의 모든 계약 내용 조회 */
    @Query("""
            select c
            from Contract c
            where c.room.building.id = ?1
            and (?2 is null or c.room.id <> ?2)
            and c.contractStatus not in (?3)
            and c.status = 'REGISTER'
            """)
    List<Contract> findByAllContractAllRoomsPerBuilding(Long buildingId, Long roomId, List<ContractStatus> statusList);


    @Query("""
            select c
            from Contract c
            where c.id <?1
            order by c.id desc
            """)
    Contract findPreviousContract(Long contractId, PageRequest pageRequest);

    List<Contract> findAllByContractStatus(ContractStatus contractStatus);
}