package com.core.back9.repository;

import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

	Optional<Building> findFirstByIdAndStatus(Long buildingId, Status status);

	default Building getValidBuildingWithIdOrThrow(Long buildingId, Status status) {
		return findFirstByIdAndStatus(buildingId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_BUILDING));
	}

	Page<Building> findAllByStatus(Status status, Pageable pageable);

	@Query(
	  """
		SELECT b FROM Building b 
		JOIN FETCH b.roomList r 
		WHERE b.id = :buildingId 
		AND r.member.id = :memberId
		"""
	)
	Optional<Building> findFirstBuildingWithRoomsByBuildingIdAndMemberId(
	  @Param("buildingId") Long buildingId, @Param("memberId") Long memberId
	);

	default Building findValidBuildingWithRoomsByBuildingIdAndMemberId(Long buildingId, Long memberId) {
		return findFirstBuildingWithRoomsByBuildingIdAndMemberId(buildingId, memberId)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_BUILDING));
	}

	@Query(
	  """
		SELECT b FROM Building b
		JOIN FETCH b.roomList r
		WHERE r.member.id = :memberId
		"""
	)
	Page<Building> findAllBuildingWithRoomsByBuildingIdAndMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
