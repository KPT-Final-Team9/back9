package com.core.back9.repository;

import com.core.back9.entity.Score;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

	Optional<Score> findFirstByIdAndStatus(Long scoreId, Status status);

	default Score getValidScoreWithIdAndStatus(Long scoreId, Status status) {
		return findFirstByIdAndStatus(scoreId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_EVALUATION));
	}

	Optional<Score> findFirstByIdAndMemberIdAndStatus(Long scoreId, Long memberId, Status status);

	default Score getValidScoreWithIdAndMemberIdAndStatus(Long scoreId, Long memberId, Status status) {
		return findFirstByIdAndMemberIdAndStatus(scoreId, memberId, status)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_EVALUATION));
	}

	@Query("""
	  SELECT COUNT(s) = 0 FROM Score s WHERE
	  YEAR(s.createdAt) = :year AND
	  MONTH(s.createdAt) BETWEEN :startMonth AND :endMonth AND
	  s.member.id = :memberId AND
	  s.room.id = :roomId AND
	  s.ratingType = 'FACILITY'
	  """)
	boolean existsByYearAndQuarterAndMemberIdAndRoomId(
	  @Param("year") int year,
	  @Param("startMonth") int startMonth,
	  @Param("endMonth") int endMonth,
	  @Param("memberId") Long memberId,
	  @Param("roomId") Long roomId
	);

	@Query("""
	  SELECT COUNT(s) = 0 FROM Score s WHERE
	  YEAR(s.createdAt) = :year AND
	  MONTH(s.createdAt) = :currentMonth AND
	  s.member.id = :memberId AND
	  s.room.id = :roomId AND
	  s.ratingType = 'MANAGEMENT'
	  """)
	boolean existsByYearAndMonthAndMemberIdAndRoomId(
	  @Param("year") int year,
	  @Param("currentMonth") int currentMonth,
	  @Param("memberId") Long memberId,
	  @Param("roomId") Long roomId
	);

}
