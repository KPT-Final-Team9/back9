package com.core.back9.repository;

import com.core.back9.entity.Room;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	Optional<Room> findFirstByBuildingIdAndIdAndStatus(Long buildingId, Long roomId, Status status);

	default Room getValidRoomWithIdOrThrow(Long buildingId, Long roomId, Status status) {
		return findFirstByBuildingIdAndIdAndStatus(buildingId, roomId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
	}

	Page<Room> findAllByBuildingIdAndStatus(Long buildingId, Status status, Pageable pageable);

}
