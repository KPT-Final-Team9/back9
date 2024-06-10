package com.core.back9.repository;

import com.core.back9.entity.Alarm;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	List<Alarm> findAllByReceivedId(Long memberId);

	Optional<Alarm> findFirstByIdAndReceivedId(Long alarmId, Long receivedId);

	default Alarm getValidAlarmByIdAndMemberId(Long alarmId, Long receivedId) {
		return findFirstByIdAndReceivedId(alarmId, receivedId)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ALARM));
	}

	boolean existsByReceivedIdAndReadStatusIsFalse(Long receivedId);

}
