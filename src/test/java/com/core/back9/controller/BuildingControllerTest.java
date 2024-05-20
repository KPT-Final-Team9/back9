package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.service.BuildingService;
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

import java.time.LocalDateTime;
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
class BuildingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BuildingService buildingService;

	private List<BuildingDTO.Request> requestList;
	private List<BuildingDTO.Response> responseList;
	private List<BuildingDTO.Info> infoList;

	@BeforeEach
	public void initSetting() {
		requestList = List.of(
		  new BuildingDTO.Request("building1", "address1", "zipCode1"),
		  new BuildingDTO.Request("building2", "address2", "zipCode2"),
		  new BuildingDTO.Request("building3", "address3", "zipCode3")
		);
		responseList = List.of(
		  new BuildingDTO.Response(1L, "building1", "address1", "zipCode1",
			LocalDateTime.of(2024, 3, 5, 13, 30, 0)),
		  new BuildingDTO.Response(2L, "building1", "address1", "zipCode1",
			LocalDateTime.of(2024, 4, 5, 14, 40, 0)),
		  new BuildingDTO.Response(3L, "building1", "address1", "zipCode1",
			LocalDateTime.of(2024, 5, 5, 15, 50, 0))
		);
		infoList = List.of(
		  new BuildingDTO.Info(1L, "building1", "address1", "zipCode1",
			LocalDateTime.of(2024, 3, 5, 13, 30, 0),
			LocalDateTime.of(2024, 3, 5, 13, 30, 0)
		  ),
		  new BuildingDTO.Info(2L, "building2", "address2", "zipCode2",
			LocalDateTime.of(2024, 4, 5, 14, 40, 0),
			LocalDateTime.of(2024, 4, 5, 14, 40, 0)
		  ),
		  new BuildingDTO.Info(3L, "building3", "address3", "zipCode3",
			LocalDateTime.of(2024, 5, 5, 15, 50, 0),
			LocalDateTime.of(2024, 5, 5, 15, 50, 0)
		  )
		);
	}

	@DisplayName("빌딩 등록 성공")
	@Test
	public void givenRequestWhenCreateBuildingThenResponse() throws Exception {
		given(buildingService.create(any(BuildingDTO.Request.class))).willReturn(responseList.get(0));

		mockMvc.perform(
			post("/api/buildings")
			  .contentType(MediaType.APPLICATION_JSON)
			  .content(objectMapper.writeValueAsString(requestList.get(0))))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.name").value(requestList.get(0).getName()))
		  .andExpect(jsonPath("$.address").value(requestList.get(0).getAddress()))
		  .andExpect(jsonPath("$.zip_code").value(requestList.get(0).getZipCode()))
		  .andExpect(jsonPath("$.created_at").exists())
		  .andExpect(jsonPath("$.created_at").isNotEmpty())
		  .andExpect(jsonPath("$.created_at").value("2024-03-05 13:30:00"));
		verify(buildingService).create(any(BuildingDTO.Request.class));
	}

	@DisplayName("빌딩 전체 조회 성공:데이터 없음")
	@Test
	public void givenNothingWhenSelectAllBuildingsThenBuildingEmptyPageList() throws Exception {
		given(buildingService.selectAll(any(Pageable.class))).willReturn(Page.empty());

		mockMvc.perform(get("/api/buildings"))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.empty").value(true));
		verify(buildingService).selectAll(any(Pageable.class));
	}

	@DisplayName("빌딩 전체 조회 성공:데이터 있음")
	@Test
	public void givenNothingWhenSelectAllBuildingsThenBuildingPageList() throws Exception {
		int currentPage = 0;
		int pageSize = 10;
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<BuildingDTO.Info> responsePage = new PageImpl<>(infoList, pageable, infoList.size());

		given(buildingService.selectAll(any(Pageable.class))).willReturn(responsePage);

		mockMvc.perform(get("/api/buildings"))
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
		verify(buildingService).selectAll(any(Pageable.class));
	}

	@DisplayName("빌딩 아이디로 조회 성공")
	@Test
	public void givenBuildingIdWhenSelectOneBuildingThenBuildingInfo() throws Exception {
		long selectId = 1L;

		given(buildingService.selectOne(anyLong())).willReturn(infoList.get(0));

		mockMvc.perform(get("/api/buildings/" + selectId))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.id").value(infoList.get(0).getId()))
		  .andExpect(jsonPath("$.name").value(infoList.get(0).getName()))
		  .andExpect(jsonPath("$.address").value(infoList.get(0).getAddress()))
		  .andExpect(jsonPath("$.zip_code").value(infoList.get(0).getZipCode()))
		  .andExpect(jsonPath("$.created_at").value("2024-03-05 13:30:00"))
		  .andExpect(jsonPath("$.updated_at").value("2024-03-05 13:30:00"));
		verify(buildingService).selectOne(anyLong());
	}

	@DisplayName("빌딩 정보 수정 성공")
	@Test
	public void givenBuildingIdAndRequestWhenUpdateBuildingDataThenBuildingInfo() throws Exception {
		long updateId = 1L;
		BuildingDTO.Request updateRequest = BuildingDTO.Request.builder()
		  .name("updated name")
		  .address("updated address")
		  .zipCode("updated zipCode")
		  .build();
		BuildingDTO.Info updatedInfo = BuildingDTO.Info.builder()
		  .name("updated name")
		  .address("updated address")
		  .zipCode("updated zipCode")
		  .build();

		given(buildingService.update(anyLong(), any(BuildingDTO.Request.class))).willReturn(updatedInfo);

		mockMvc.perform(
			patch("/api/buildings/" + updateId)
			  .contentType(MediaType.APPLICATION_JSON)
			  .content(objectMapper.writeValueAsString(updateRequest))
		  )
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.name").value(updateRequest.getName()))
		  .andExpect(jsonPath("$.address").value(updateRequest.getAddress()))
		  .andExpect(jsonPath("$.zip_code").value(updateRequest.getZipCode()));
		verify(buildingService).update(anyLong(), any(BuildingDTO.Request.class));
	}

	@DisplayName("빌딩 삭제 성공")
	@Test
	public void givenBuildingIdWhenDeleteBuildingThenSuccessResult() throws Exception {
		long deleteId = 1L;
		given(buildingService.delete(anyLong())).willReturn(true);

		mockMvc.perform(delete("/api/buildings/" + deleteId))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(content().string("true"));
		verify(buildingService).delete(anyLong());
	}

}