package com.core.back9.repository;

import com.core.back9.entity.Score;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

	Optional<Score> findFirstByIdAndMemberIdAndRoomId(Long scoreId, Long memberId, Long roomId);

	default Score getValidScoreWithIdAndMemberIdAndRoomId(Long scoreId, Long memberId, Long roomId) {
		return findFirstByIdAndMemberIdAndRoomId(scoreId, memberId, roomId)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_EVALUATION));
	}

}
