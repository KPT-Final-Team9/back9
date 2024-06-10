package com.core.back9.repository;

import com.core.back9.entity.Complaint;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

	@Query("SELECT c FROM Complaint c WHERE c.id = :complaintId AND c.status = 'REGISTER'")
	Optional<Complaint> findFirstById(Long complaintId);

	default Complaint getValidComplaint(Long complaintId) {
		return findFirstById(complaintId)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_COMPLAINT));
	}

	/* 소유자가 자신의 특정 호실에 등록된 민원 조회 */
	List<Complaint> findAllByRoomIdAndStatus(Long roomId, Status status);

	/* 소유자가 모든 호실에 등록된 민원 조회 */
	@Query(
	  """
		SELECT DISTINCT c FROM Complaint c\s
				JOIN FETCH c.room r
				JOIN FETCH r.building b
				WHERE r.member.id = :ownerId
		"""
	)
	List<Complaint> findAllComplaintsByOwnerId(@Param("ownerId") Long ownerId);

	/* 입주자가 자신이 등록한 모든 민원 목록 조회 */
	List<Complaint> findAllByMemberId(Long memberId);

}
