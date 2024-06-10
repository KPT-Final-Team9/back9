package com.core.back9.dto;

import com.core.back9.entity.constant.AlarmType;
import com.core.back9.entity.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AlarmDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {
		private Long receivedId;
		private AlarmType alarmType;
		private boolean readStatus;
		private Status status;
		private String alarmTitle;
		private String alarmMessage;

		public static Request createComplaint(Long receivedId, String alarmMessage) {
			return Request.builder()
			  .receivedId(receivedId)
			  .alarmType(AlarmType.COMPLAINT_PENDING)
			  .readStatus(false)
			  .status(Status.REGISTER)
			  .alarmTitle(AlarmType.COMPLAINT_PENDING.getLabel())
			  .alarmMessage(alarmMessage)
			  .build();
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private AlarmType alarmType;
		private boolean readStatus;
		private String alarmTitle;
		private String alarmMessage;
		private LocalDateTime createdAt;
	}

}
