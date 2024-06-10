package com.core.back9.service;

import com.core.back9.dto.ComplaintDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Complaint;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.constant.ComplaintStatus;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.ComplaintMapper;
import com.core.back9.repository.ComplaintRepository;
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.RoomRepository;
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

	public void create(MemberDTO.Info member, ComplaintDTO.RegisterRequest registerRequest) {
		Member validMember =
		  memberRepository.getValidMemberWithIdAndRole(member.getId(), member.getRole(), Status.REGISTER);
		Room validRoom =
		  roomRepository.getValidRoomByIdAndStatus(registerRequest.getRoomId(), Status.REGISTER);

		Complaint newComplaint = Complaint.builder()
		  .room(validRoom)
		  .member(validMember)
		  .complaintMessage(registerRequest.getComplaintMessage())
		  .status(Status.REGISTER)
		  .complaintStatus(ComplaintStatus.IN_PROGRESS)
		  .build();
		complaintRepository.save(newComplaint);
	}

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
