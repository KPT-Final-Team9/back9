package com.core.back9.controller;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import com.core.back9.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RoomService roomService;

	@BeforeEach
	public void initSetting() {

	}

	@DisplayName("호실 등록 성공")
	@Test
	public void givenBuildingIdAndRequestWhenCreateRoomThenResponse() throws Exception {
		long selectedBuildingId = 1L;
		RoomDTO.Request request = RoomDTO.Request.builder()
		  .name("room name")
		  .floor("room floor")
		  .area(84F)
		  .usage(Usage.NEWSPAPERS)
		  .build();
		RoomDTO.Response response = RoomDTO.Response.builder()
		  .id(1L)
		  .name("room name")
		  .floor("room floor")
		  .area(84F)
		  .usage(Usage.NEWSPAPERS)
		  .status(Status.REGISTER)
		  .build();
		given(roomService.create(anyLong(), any(RoomDTO.Request.class))).willReturn(response);

		mockMvc.perform(
			post("/api/buildings/" + selectedBuildingId + "/rooms")
			  .contentType(MediaType.APPLICATION_JSON)
			  .content(objectMapper.writeValueAsString(request))
		  )
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.name").value(request.getName()))
		  .andExpect(jsonPath("$.floor").value(request.getFloor()))
		  .andExpect(jsonPath("$.area").value(request.getArea()))
		  .andExpect(jsonPath("$.usage").value(request.getUsage().name()))
		  .andExpect(jsonPath("$.status").value(Status.REGISTER.name()));
		verify(roomService).create(anyLong(), any(RoomDTO.Request.class));
	}

	@DisplayName("호실 정보 수정 성공")
	@Test
	public void givenBuildingIdAndRoomIdAndRequestWhenUpdateRoomDataThenRoomInfo() throws Exception {
		long selectedBuildingId = 1L;
		long updateRoomId = 1L;
		RoomDTO.Request updateRequest = RoomDTO.Request.builder()
		  .name("updated name")
		  .floor("updated floor")
		  .build();
		RoomDTO.Info updatedInfo = RoomDTO.Info.builder()
		  .name("updated name")
		  .floor("updated floor")
		  .build();
		given(roomService.update(anyLong(), any(RoomDTO.Request.class))).willReturn(updatedInfo);

		mockMvc.perform(
			patch("/api/buildings/" + selectedBuildingId + "/rooms/" + updateRoomId)
			  .contentType(MediaType.APPLICATION_JSON)
			  .content(objectMapper.writeValueAsString(updateRequest))
		  )
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.name").value(updateRequest.getName()))
		  .andExpect(jsonPath("$.floor").value(updateRequest.getFloor()));
		verify(roomService).update(anyLong(), any(RoomDTO.Request.class));
	}

	@DisplayName("호실 삭제 성공")
	@Test
	public void givenBuildingIdAndRoomIdWhenDeleteRoomThenSuccessResult() throws Exception {
		long selectedBuildingId = 1L;
		long deleteRoomId = 1L;
		given(roomService.delete(anyLong(), anyLong())).willReturn(true);

		mockMvc.perform(delete("/api/buildings/" + selectedBuildingId + "/rooms/" + deleteRoomId))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(content().string("true"));
		verify(roomService).delete(anyLong(), anyLong());
	}

	@DisplayName("호실 전체 조회 성공")
	@Test
	public void givenBuildingIdWhenSelectAllRoomsThenRoomPageList() throws Exception {
		List<RoomDTO.Info> infoList = List.of(
		  RoomDTO.Info.builder().id(1L).build(),
		  RoomDTO.Info.builder().id(2L).build(),
		  RoomDTO.Info.builder().id(3L).build()
		);
		long selectedBuildingId = 1L;
		int currentPage = 0;
		int pageSize = 10;
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<RoomDTO.Info> responsePage = new PageImpl<>(infoList, pageable, infoList.size());

		given(roomService.selectAll(anyLong(), any(Pageable.class))).willReturn(responsePage);

		mockMvc.perform(get("/api/buildings/" + selectedBuildingId + "/rooms"))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.empty").value(false))
		  .andExpect(jsonPath("$.number_of_elements").value(infoList.size()))
		  .andExpect(jsonPath("$.pageable.page_size").value(pageSize))
		  .andExpect(jsonPath("$.content").isArray())
		  .andExpect(jsonPath("$.content", hasSize(infoList.size())))
		  .andExpect(jsonPath("$.content[0].id").value(1L))
		  .andExpect(jsonPath("$.content[1].id").value(2L))
		  .andExpect(jsonPath("$.content[2].id").value(3L));
		verify(roomService).selectAll(anyLong(), any(Pageable.class));
	}

	@DisplayName("호실 아이디로 조회 성공")
	@Test
	public void givenBuildingIdAndRoomIdWhenSelectOneRoomThenRoomInfo() throws Exception {
		long selectedBuildingId = 1L;
		long selectRoomId = 1L;
		RoomDTO.Info selectedInfo = RoomDTO.Info.builder()
		  .name("selected room name")
		  .floor("selected room floor")
		  .build();

		given(roomService.selectOne(anyLong())).willReturn(selectedInfo);

		mockMvc.perform(get("/api/buildings/" + selectedBuildingId + "/rooms/" + selectRoomId))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.name").value(selectedInfo.getName()))
		  .andExpect(jsonPath("$.floor").value(selectedInfo.getFloor()));
		verify(roomService).selectOne(anyLong());
	}

}