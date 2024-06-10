package com.core.back9.service;

import com.core.back9.dto.AlarmDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Alarm;
import com.core.back9.mapper.AlarmMapper;
import com.core.back9.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final AlarmMapper alarmMapper;

	public void create(AlarmDTO.Request request) {
		Alarm newAlarm = alarmMapper.toEntity(request);
		alarmRepository.save(newAlarm);
	}

	@Transactional(readOnly = true)
	public List<AlarmDTO.Info> selectAllById(MemberDTO.Info member) {
		return alarmRepository.findAllByReceivedId(member.getId())
		  .stream().map(alarmMapper::toInfo).toList();
	}

	public void updateRead(MemberDTO.Info member, Long alarmId) {
		alarmRepository.getValidAlarmByIdAndMemberId(alarmId, member.getId()).read();
	}

	public void delete(MemberDTO.Info member, Long alarmId) {
		alarmRepository.getValidAlarmByIdAndMemberId(alarmId, member.getId()).delete();
	}

	@Transactional(readOnly = true)
	public boolean hasUnreadAlarms(MemberDTO.Info member) {
		return alarmRepository.existsByReceivedIdAndReadStatusIsFalse(member.getId());
	}

}
