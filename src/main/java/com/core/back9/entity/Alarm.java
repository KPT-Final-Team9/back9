package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.AlarmType;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "alarms")
@Where(clause = "status = 'REGISTER'")
public class Alarm extends BaseEntity {

	@Column(name = "received_id", nullable = false)
	private Long receivedId;

	@Enumerated(EnumType.STRING)
	@Column(name = "alarm_type", nullable = false)
	private AlarmType alarmType;

	@Column(name = "read_status", nullable = false)
	private boolean readStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@Column(name = "alarm_title")
	@Setter
	private String alarmTitle;

	@Column(name = "alarm_message")
	@Setter
	private String alarmMessage;

	@Builder

	public Alarm(Long receivedId, AlarmType alarmType, boolean readStatus, Status status, String alarmTitle, String alarmMessage) {
		this.receivedId = receivedId;
		this.alarmType = alarmType;
		this.readStatus = readStatus;
		this.status = status;
		this.alarmTitle = alarmTitle;
		this.alarmMessage = alarmMessage;
	}

	public void read() {
		this.readStatus = true;
	}

	public void delete() {
		this.status = Status.UNREGISTER;
	}

}
