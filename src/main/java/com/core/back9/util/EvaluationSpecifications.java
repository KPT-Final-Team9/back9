package com.core.back9.util;

import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.RatingType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class EvaluationSpecifications {

	public static Specification<Score> isCompleted() {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			Predicate scoreGreaterThanOrEqualToZero = criteriaBuilder.greaterThanOrEqualTo(score.get("score"), 0);
			Predicate createdDateAndUpdatedDateNotEqual = criteriaBuilder.notEqual(score.get("createdAt"), score.get("updatedAt"));
			return criteriaBuilder.and(scoreGreaterThanOrEqualToZero, createdDateAndUpdatedDateNotEqual);
		};
	}

	public static Specification<Score> isUpdatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
		  criteriaBuilder.between(score.get("updatedAt"), startDate, endDate);
	}

	public static Specification<Score> hasRoomId(Room room) {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
		  criteriaBuilder.equal(score.get("room"), room);
	}

	public static Specification<Score> hasRatingType(RatingType ratingType) {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			if (ratingType == null) {
				return criteriaBuilder.conjunction();
			} else {
				return criteriaBuilder.equal(score.get("ratingType"), ratingType);
			}
		};
	}

	public static Specification<Score> isBookmarked() {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
		  criteriaBuilder.equal(score.get("bookmark"), true);
	}

	public static Specification<Score> containsKeyword(String keyword) {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
		  criteriaBuilder.like(score.get("comment"), "%" + keyword + "%");
	}

	public static Specification<Score> isOneYearAgo(LocalDateTime baseDateTime) {
		return (Root<Score> score, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			LocalDateTime oneYearAgo = baseDateTime.minusYears(1);
			return criteriaBuilder.between(score.get("createdAt"), oneYearAgo, baseDateTime);
		};
	}

	public static Specification<Score> hasRoomList(List<Room> rooms, boolean has) {
		return ((root, query, criteriaBuilder) -> {
			if (rooms == null || rooms.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			Join<Score, Room> roomJoin = root.join("room", JoinType.INNER);
			return has ? roomJoin.in(rooms) : criteriaBuilder.not(roomJoin.in(rooms));
		});
	}

	public static Specification<Score> hasBuilding(Building building, boolean has) {
		return ((root, query, criteriaBuilder) -> {
			if (building == null) {
				return criteriaBuilder.conjunction();
			}
			Join<Score, Room> roomJoin = root.join("room", JoinType.INNER);
			Join<Room, Building> buildingJoin = roomJoin.join("building", JoinType.INNER);
			return has ? buildingJoin.in(building) : criteriaBuilder.not(buildingJoin.in(building));
		});
	}

}
