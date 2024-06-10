package com.core.back9.service;

import com.core.back9.dto.AlarmDTO;
import com.core.back9.dto.ComplaintDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Complaint;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.AlarmType;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.ComplaintMapper;
import com.core.back9.repository.ComplaintRepository;
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.ScoreRepository;
import com.core.back9.sse.connection.SseConnectionPoolImpl;
import com.core.back9.sse.connection.model.SseMemberConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ComplaintService {

	private final MemberRepository memberRepository;
	private final RoomRepository roomRepository;
	private final ComplaintRepository complaintRepository;
	private final ComplaintMapper complaintMapper;
	private final AlarmService alarmService;
	private final ScoreRepository scoreRepository;
	private final SseConnectionPoolImpl sseConnectionPool;

	public void create(MemberDTO.Info member, ComplaintDTO.RegisterRequest registerRequest) {
		Member validMember =
		  memberRepository.getValidMemberWithIdAndRole(member.getId(), member.getRole(), Status.REGISTER);
		Room validRoom =
		  roomRepository.getValidRoomByIdAndStatus(registerRequest.getRoomId(), Status.REGISTER);

		Complaint newComplaint = Complaint.createOf(validRoom, validMember, registerRequest.getComplaintMessage());
		complaintRepository.save(newComplaint);

		AlarmDTO.Request userAlarm = AlarmDTO.Request
		  .createComplaint(validMember.getId(), registerRequest.getComplaintMessage());
		AlarmDTO.Request ownerAlarm = AlarmDTO.Request
		  .createComplaint(validRoom.getMember().getId(), registerRequest.getComplaintMessage());
		alarmService.create(userAlarm);
		alarmService.create(ownerAlarm);

		SseMemberConnection userConnection = sseConnectionPool.getSession(validMember.getId().toString());
		SseMemberConnection ownerConnection = sseConnectionPool.getSession(validRoom.getMember().getId().toString());

		if (userConnection != null) {
			userConnection.sendMessage(AlarmType.COMPLAINT_PENDING.getDescription());
		}
		if (ownerConnection != null) {
			ownerConnection.sendMessage(AlarmType.COMPLAINT_PENDING.getDescription());
		}
	}

	@Transactional(readOnly = true)
	public List<ComplaintDTO.Info> selectAllByMemberId(MemberDTO.Info member) {
		if (member.isUser()) {
			return complaintRepository.findAllByMemberId(member.getId())
			  .stream().map(complaintMapper::toInfo).toList();
		}
		return complaintRepository.findAllComplaintsByOwnerId(member.getId())
		  .stream().map(complaintMapper::toInfo).toList();
	}

	public void updateCompleted(MemberDTO.Info member, Long complaintId, String completeMessage) {
		Complaint validComplaint = complaintRepository.getValidComplaint(complaintId);
		validComplaint.completeComplaint(member, completeMessage);

		Score complaintScore = Score.createComplaint(validComplaint.getRoom(), validComplaint.getMember());
		scoreRepository.save(complaintScore);
	}

	public void updateRejected(MemberDTO.Info member, Long complaintId, String rejectMessage) {
		Complaint validComplaint = complaintRepository.getValidComplaint(complaintId);
		validComplaint.rejectComplaint(member, rejectMessage);
	}

	public void delete(MemberDTO.Info user, Long complaintId) {
		Complaint validComplaint = complaintRepository.getValidComplaint(complaintId);
		if (validComplaint.isPossibleToDelete(user)) {
			validComplaint.delete();
		}
	}

}
