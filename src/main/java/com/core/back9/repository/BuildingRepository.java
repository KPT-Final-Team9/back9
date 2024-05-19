package com.core.back9.repository;

import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
