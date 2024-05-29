package com.core.back9.service;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
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
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ScoreService {

	private final MemberRepository memberRepository;
	//	private final TenantRepository tenantRepository;
	private final RoomRepository roomRepository;
	private final ScoreRepository scoreRepository;
	private final ScoreMapper scoreMapper;

	public void create(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  RatingType ratingType
	) {
		/* TODO 평가 레코드 생성 조건 붙이기,
		    batch 생성이라면 멤버(OWNER) 권한은 필요없음
		    예외적으로 로그인 멤버가 OWNER 상태일 때 수동으로 발생
		*/

		/*
		첫번째 작업 - 코딩 중 예외상황이 많이 확인되어 철회
		해당 호실과 인증된 유저로 평가 레코드가 생성될 수 있는지 확인 후 생성
		1. 해당 호실의 계약목록이 있는지
		2. 계약목록이 있다면 마지막 계약이 진행중인 계약인지
		3. 진행중인 계약의 입주사에 포함된 유저인지

		생각해볼 문제: 1차적인 생각으로 위의 조건으로 생성을 했는데
		 1. 계약목록이 4건이 있는데 4번째 계약은 대기 상태이고 유저는 3번째 계약 진행중인 입주사에 포함되어 있을 때
		 -> 무조건 마지막 계약이 진행중인지 확인하는건 오류 가능성이 있음
		 TODO 2. 평가를 진행 할 수 있는 유효기간이 필요함
		  -> 4월 1일에 발생한 평가를 진행하지 않고 7월 1일이 되었을 때
		 3. 평가 레코드 생성은 시설/관리는 입주사 기준으로 생성이 되고, 민원은 유저 기준으로 생성되어야 함
		 4. 다 필요없고 빌딩-호실에서 수동으로 평가를 발생시키려면 평가타입만 선택하면 계약 이행중인 입주사에 바로 실행
		 TODO 5. 평가 레코드 타입에 대해 생성일 체크하여 중복생성 방지해야함 (시설-분기별, 관리-월별, 민원-건별)
		 */

		if (member.getRole() == Role.OWNER) {
			/* 유효한 호실 */
			Room validRoom = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);

			/* 유효한 입주사 -> 필요가 없넹 */
//			Tenant validTenant = tenantRepository.getValidOneTenantOrThrow(createRequest.getTenantId());

			/* 해당 호실의 계약 목록 */
			List<Contract> contracts = validRoom.getContracts();

			/* 계약 목록 중 계약 이행중인 입주사
			 * 이행중인 계약은 단 한건이라고 판단 -> findFirst -> 없다면 계약 이행중인 상태가 아니다! */
			Contract progressContract = contracts.stream().filter(contract -> contract.getContractStatus() == ContractStatus.IN_PROGRESS)
			  .findFirst().orElseThrow(() -> new ApiException(ApiErrorCode.CONTRACT_NOT_IN_PROGRESS));

			/* 계약 이행 중인 입주사에 포함된 모든 사용자에게 평가 레코드 생성 (평가타입은 리퀘스트로 받음) */
			progressContract.getTenant().getMembers().forEach(user -> {
				try {
					memberRepository.findFirstByIdAndRoleAndStatus(user.getId(), Role.USER, Status.REGISTER)
					  .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER, "평가 레코드를 생성할 수 없는 사용자입니다"));
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
				} catch (ApiException apiException) {
					System.out.printf("평가 레코드 생성 실패 사용자 id: %s, role: %s, status: %s%n", user.getId(), user.getRole(), user.getStatus());
				}
			});
			return;


		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "평가 수동 발생은 소유자의 권한입니다");
	}

	public ScoreDTO.UpdateResponse update(
	  MemberDTO.Info member,    // USER
	  Long scoreId,
	  Long roomId,
	  ScoreDTO.UpdateRequest updateRequest
	) {
		Score validScore = scoreRepository.getValidScoreWithIdAndMemberIdAndRoomId(scoreId, member.getId(), roomId);
		validScore.updateScore(updateRequest);
		return scoreMapper.toUpdateResponse(validScore);
	}

	/* TODO 마지막 계약을 가져오는 건 검증의 조건이 될 수 없음, 내일 아침에 당장 바까라
	    차라리 계약 이행중인 입주사 하나만 빼오는게 좋음 (유일한 상태 값) -> 계약 도메인이 완전한 상태라면 */
//	private boolean isPossible(List<Contract> contracts, Tenant validTenant) {
//		return !contracts.isEmpty()
//		  && contracts.get(contracts.size() - 1).getContractStatus() != ContractStatus.IN_PROGRESS
//		  && Objects.equals(contracts.get(contracts.size() - 1).getTenant().getId(), validTenant.getId());
//	}

}
