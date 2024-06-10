package com.core.back9.repository;

import com.core.back9.entity.Room;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	Optional<Room> findFirstByBuildingIdAndIdAndStatus(Long buildingId, Long roomId, Status status);

	default Room getValidRoomWithIdOrThrow(Long buildingId, Long roomId, Status status) {
		return findFirstByBuildingIdAndIdAndStatus(buildingId, roomId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
	}

	@Query("""
	  select r
	  from Room r
	  where r.building.id=?1
	  and r.id=?2
	  and r.member.id=?3
	  and r.status=?4
	  """)
	Optional<Room> getRoomBySpecificIds(Long buildingId, Long roomId, Long memberId, Status status);

	default Room getValidSpecificRoom(Long buildingId, Long roomId, Long memberId, Status status) {
		return getRoomBySpecificIds(buildingId, roomId, memberId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
	}

	Page<Room> findAllByBuildingIdAndStatus(Long buildingId, Status status, Pageable pageable);

	Page<Room> findAllByBuildingIdAndMemberIdAndStatus(Long buildingId, Long memberId, Status status, Pageable pageable);

	@Query(
	  """
		SELECT COUNT(r) = 0
		FROM Room r
		WHERE r.building.id = :buildingId
		AND r.member.id = :memberId
		AND r.represent = true
		"""
	)
	boolean notExistsRepresentRoom(@Param("buildingId") Long buildingId, @Param("memberId") Long memberId);

	@Query(
	  """
		SELECT r
		FROM Room r 
		WHERE r.building.id = :buildingId 
		AND r.member.id = :memberId 
		AND r.represent = true
		"""
	)
	Optional<Room> findFirstRepresentRoom(@Param("buildingId") Long buildingId, @Param("memberId") Long memberId);

	default Room getValidRepresentRoom(Long buildingId, Long memberId) {
		return findFirstRepresentRoom(buildingId, memberId)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
	}

	List<Room> findAllByBuildingIdAndMemberIdAndStatus(Long buildingId, Long MemberId, Status status);

	Optional<Room> findFirstByIdAndStatus(Long roomId, Status status);

	default Room getValidRoomByIdAndStatus(Long roomId, Status status) {
		return findFirstByIdAndStatus(roomId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
	}

}
