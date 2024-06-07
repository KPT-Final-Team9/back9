package com.core.back9.service;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.ScoreMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.ScoreRepository;
import com.core.back9.util.DateUtils;
import com.core.back9.util.EvaluationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ScoreService {

	private final MemberRepository memberRepository;
	private final BuildingRepository buildingRepository;
	private final RoomRepository roomRepository;
	private final ScoreRepository scoreRepository;
	private final ScoreMapper scoreMapper;

	public void create(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  RatingType ratingType
	) {
		/* TODO batch 생성이라면 멤버(OWNER) 권한은 필요없음
		    예외적으로 로그인 멤버가 OWNER 상태일 때 수동으로 발생
		*/

		/*
		 TODO 2. 평가를 진행 할 수 있는 유효기간이 필요함
		  -> 4월 1일에 발생한 평가를 진행하지 않고 7월 1일이 되었을 때
		 */

		if (member.getRole() == Role.OWNER) {
			/* 유효한 호실 */
			Room validRoom = roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
			  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));

			/* 해당 호실의 계약 목록 */
			List<Contract> contracts = validRoom.getContracts();

			/* 계약 목록 중 계약 이행중인 입주사
			 * 이행중인 계약은 단 한건이라고 판단 -> findFirst -> 없다면 계약 이행중인 상태가 아니다! */
			Contract progressContract = contracts.stream().filter(contract -> contract.getContractStatus() == ContractStatus.IN_PROGRESS)
			  .findFirst().orElseThrow(() -> new ApiException(ApiErrorCode.CONTRACT_NOT_IN_PROGRESS));

			/* 계약 이행 중인 입주사에 포함된 모든 사용자에게 평가 레코드 생성 (평가타입은 리퀘스트로 받음) */
			progressContract.getTenant().getMembers().forEach(user -> {
				try {
					if (isPossible(user.getId(), validRoom.getId(), ratingType)) {
						Score newScore = Score.builder()
						  .score(0)
						  .comment("")
						  .bookmark(false)
						  .ratingType(ratingType)
						  .room(validRoom)
						  .member(user)
						  .status(Status.REGISTER)
						  .build();
						scoreRepository.save(newScore);
					}
				} catch (ApiException apiException) {
					System.out.printf("평가 레코드 생성 실패 사용자 id: %s, role: %s, status: %s%n", user.getId(), user.getRole(), user.getStatus());
				}
			});
			return;

		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "평가 수동 발생은 소유자의 권한입니다");
	}

	public ScoreDTO.UpdateResponse updateScore(
	  MemberDTO.Info member,    // USER
	  Long scoreId,
	  ScoreDTO.UpdateRequest updateRequest
	) {
		if (member.getRole() == Role.USER) {

			Score validScore = scoreRepository.getValidScoreWithIdAndMemberIdAndStatus(scoreId, member.getId(), Status.REGISTER);

			if (validScore.getScore() > 0 || !validScore.getCreatedAt().isEqual(validScore.getUpdatedAt())) {
				throw new ApiException(ApiErrorCode.ALREADY_COMPLETED_EVALUATION);
			}

			validScore.updateScore(updateRequest);
			return scoreMapper.toUpdateResponse(validScore);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public ScoreDTO.Info updateBookmark(MemberDTO.Info member, Long scoreId, boolean bookmark) {
		if (member.getRole() == Role.OWNER) {
			Score validScore = scoreRepository.getValidScoreWithIdAndStatus(scoreId, Status.REGISTER);
			validScore.updateBookmark(bookmark);
			return scoreMapper.toInfo(validScore);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	@Transactional(readOnly = true)
	public Page<ScoreDTO.Info> selectScores(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  LocalDateTime startDate,
	  LocalDateTime endDate,
	  RatingType ratingType,
	  Boolean bookmark,
	  String keyword,
	  Pageable pageable
	) {
		Building building = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		Specification<Score> specification = Specification.where(null);
		specification = specification.and(EvaluationSpecifications.isCompleted());
		specification = specification.and(EvaluationSpecifications.hasRatingType(ratingType));

		if (roomId != null) {
			Room room = roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
			  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));
			specification = specification.and(EvaluationSpecifications.hasRoomId(room));
		} else {
			// 선택된 빌딩의 모든 호실의 데이터
			specification = specification.and(EvaluationSpecifications.hasBuilding(building, true));
		}
		if (startDate != null && endDate != null) {
			specification = specification.and(EvaluationSpecifications.isUpdatedBetween(startDate, endDate));
		}
		if (bookmark) {
			specification = specification.and(EvaluationSpecifications.isBookmarked());
		}
		if (keyword != null) {
			specification = specification.and(EvaluationSpecifications.containsKeyword(keyword));
		}
		return scoreRepository.findAll(specification, pageable).map(scoreMapper::toInfo);
	}

	@Transactional(readOnly = true)
	public ScoreDTO.ListOfYearAvgWithMeAndOthers selectYearScoresIntervalMonth(MemberDTO.Info member, Long buildingId, Long roomId, YearMonth yearMonth) {

		Room validRoom = roomRepository.getValidSpecificRoom(buildingId, roomId, member.getId(), Status.REGISTER);
		List<Room> roomList = roomRepository.findAllByBuildingIdAndMemberIdAndStatus(
		  buildingId, member.getId(), Status.REGISTER, null
		).stream().toList();

		Specification<Score> specification = Specification.where(null);
		specification = specification.and(EvaluationSpecifications.hasRoomId(validRoom));
		specification = specification.and(EvaluationSpecifications.isOneYearAgo(yearMonth.atEndOfMonth().atTime(LocalTime.MAX)));
		List<Score> myYearScores = scoreRepository.findAll(specification);    // 내 단일 호실 점수 목록

		specification = Specification.where(null);
		specification = specification.and(EvaluationSpecifications.hasRoomList(roomList, false));
		specification = specification.and(EvaluationSpecifications.isOneYearAgo(yearMonth.atEndOfMonth().atTime(LocalTime.MAX)));
		List<Score> othersYearScores = scoreRepository.findAll(specification);    // 타 호실 점수 목록

		List<ScoreDTO.AllAvgByMonth> yearlyScoreMy = getYearlyScoreListOfMyOrOthers(yearMonth, myYearScores);
		List<ScoreDTO.AllAvgByMonth> yearlyScoreOthers = getYearlyScoreListOfMyOrOthers(yearMonth, othersYearScores);

		return scoreMapper.toListOfYearAvgWithMeAndOthers(yearlyScoreMy, yearlyScoreOthers);
	}

	public ScoreDTO.AvgByQuarter selectScoresByQuarter(MemberDTO.Info member, Long buildingId, int year, int quarter) {
		List<Room> roomList = roomRepository.findAllByBuildingIdAndMemberIdAndStatus(
		  buildingId, member.getId(), Status.REGISTER, null
		).stream().toList();

		List<Score> currentQuarterScoreList = getQuarterlyScoreListOfMyOrOthers(roomList, year, quarter, true);

		return scoreMapper.toQuarterlyTotalAvg(year, quarter, currentQuarterScoreList);
	}

	public ScoreDTO.CurrentAndBeforeQuarterlyTotalAvg selectQuarterlyScoreOfMyRooms(MemberDTO.Info member, Long buildingId, int year, int quarter) {
		List<Room> roomList = roomRepository.findAllByBuildingIdAndMemberIdAndStatus(
		  buildingId, member.getId(), Status.REGISTER, null
		).stream().toList();

		List<ScoreDTO.TotalAvgByRoom> currentQuarterByRoomList = getQuarterlyScoreListOfMyRooms(roomList, year, quarter);

		int beforeYear = year, beforeQuarter;
		if (quarter == 1) {
			beforeQuarter = 4;
			beforeYear = year - 1;
		} else {
			beforeQuarter = quarter - 1;
		}

		List<ScoreDTO.TotalAvgByRoom> beforeQuarterByRoomList = getQuarterlyScoreListOfMyRooms(roomList, beforeYear, beforeQuarter);

		return scoreMapper.toQuarterlyTotalAvgWithCurrentAndBefore(currentQuarterByRoomList, beforeQuarterByRoomList);
	}

	public List<ScoreDTO.AllAvgByRoom> selectYearScoreOfMyRooms(MemberDTO.Info member, Long buildingId) {
		List<Room> roomList = roomRepository.findAllByBuildingIdAndMemberIdAndStatus(
		  buildingId, member.getId(), Status.REGISTER, null
		).stream().toList();

		YearMonth now = YearMonth.now();

		return roomList.stream().map(room -> {

			Specification<Score> specification = Specification.where(null);
			specification = specification.and(EvaluationSpecifications.hasRoomId(room));
			specification = specification.and(EvaluationSpecifications.isOneYearAgo(now.atEndOfMonth().atTime(LocalTime.MAX)));
			List<Score> myYearScores = scoreRepository.findAll(specification);    // 내 단일 호실 점수 목록

			return scoreMapper.toAllAvgWithMonthByRoom(room, getYearlyScoreListOfMyOrOthers(now, myYearScores));
		}).toList();
	}

	private boolean isPossible(Long memberId, Long roomId, RatingType ratingType) {
		DateUtils dateUtils = new DateUtils();
		int quarter = dateUtils.getQuarter();

		memberRepository.findFirstByIdAndRoleAndStatus(memberId, Role.USER, Status.REGISTER)
		  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER, "평가 레코드를 생성할 수 없는 사용자입니다"));

		if (ratingType == RatingType.FACILITY) {
			// 시설 - 분기별
			return scoreRepository.existsByYearAndQuarterAndMemberIdAndRoomId(
			  dateUtils.getYear(),
			  dateUtils.getStartMonth(quarter),
			  dateUtils.getEndMonth(quarter),
			  memberId, roomId);
		} else {
			// 관리 - 월별
			return scoreRepository.existsByYearAndMonthAndMemberIdAndRoomId(
			  dateUtils.getYear(), dateUtils.getMonthValue(),
			  memberId, roomId);
		}
	}

	public List<ScoreDTO.TotalAvgByRoom> getQuarterlyScoreListOfMyRooms(List<Room> roomList, int year, int quarter) {
		return roomList.stream().map(room -> {
			DateUtils dateUtils = new DateUtils();
			LocalDateTime[] startDayAndEndDay = dateUtils.getStartDayAndEndDayByYearAndQuarter(year, quarter);

			Specification<Score> specification = Specification.where(null);
			specification = specification.and(EvaluationSpecifications.isCompleted());
			specification = specification.and(EvaluationSpecifications.isUpdatedBetween(
			  startDayAndEndDay[0], startDayAndEndDay[1]
			));
			specification = specification.and(EvaluationSpecifications.hasRoomId(room));
			List<Score> scoreByRoom = scoreRepository.findAll(specification);

			return scoreMapper.toTotalAvgByRoom(room, scoreByRoom);
		}).toList();
	}

	public List<Score> getQuarterlyScoreListOfMyOrOthers(List<Room> roomList, int year, int quarter, boolean isMine) {
		DateUtils dateUtils = new DateUtils();
		LocalDateTime[] startDayAndEndDay = dateUtils.getStartDayAndEndDayByYearAndQuarter(year, quarter);

		Specification<Score> specification = Specification.where(null);
		specification = specification.and(EvaluationSpecifications.isCompleted());
		specification = specification.and(EvaluationSpecifications.isUpdatedBetween(
		  startDayAndEndDay[0], startDayAndEndDay[1]
		));
		specification = specification.and(EvaluationSpecifications.hasRoomList(roomList, isMine));
		return scoreRepository.findAll(specification);
	}

	public List<ScoreDTO.AllAvgByMonth> getYearlyScoreListOfMyOrOthers(YearMonth yearMonth, List<Score> scoreList) {
		List<ScoreDTO.AllAvgByMonth> allAvgByMonthList = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			YearMonth current = yearMonth.minusMonths(i);
			LocalDateTime startDayOfMonth = current.atDay(1).atStartOfDay();
			LocalDateTime endDayOfMonth = current.atEndOfMonth().atTime(LocalTime.MAX);

			List<Score> scoresOfMonth = scoreList.stream()
			  .filter(score -> !score.getCreatedAt().isBefore(startDayOfMonth) && !score.getCreatedAt().isAfter(endDayOfMonth))
			  .toList();

			allAvgByMonthList.add(scoreMapper.toAllAvgWithMonth(current, scoresOfMonth));
		}
		return allAvgByMonthList;
	}

	public boolean hasValidScore(MemberDTO.Info member, Long buildingId) {

		List<Long> roomIds = roomRepository.findAllByBuildingIdAndMemberIdAndStatus(buildingId, member.getId(), Status.REGISTER).stream()
				.map(BaseEntity::getId).toList();

        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);

		for (Long roomId : roomIds) {
            if (!scoreRepository.findFirstByRoomIdAndStatus(roomId, Status.REGISTER, twoYearsAgo).isEmpty()) {
                return true;
            }
		}

		return false;
	}

}
