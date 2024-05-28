package com.core.back9.mapper;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.Setting;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE,
  uses = MemberMapper.class
)
public interface RoomMapper {

	@Autowired
	MemberMapper memberMapper = new MemberMapperImpl();

	@Mapping(target = "building", expression = "java(building)")
	@Mapping(target = "setting", expression = "java(setting)")
	Room toEntity(@Context Building building, RoomDTO.Request request, @Context Setting setting);

	RoomDTO.Response toResponse(Room room);

	RoomDTO.Info toInfo(Room room);

	List<RoomDTO.Info> toInfoList(List<Room> rooms);

	@Mapping(target = "ownerInfo", expression = "java(toOwnerInfo(member))")
	@Mapping(source = "room.id", target = "id")
	@Mapping(source = "room.status", target = "status")
	@Mapping(source = "member.id", target = "ownerInfo.ownerId")
	@Mapping(source = "member.email", target = "ownerInfo.ownerEmail")
	@Mapping(source = "member.role", target = "ownerInfo.role")
	@Mapping(source = "member.status", target = "ownerInfo.ownerStatus")
	RoomDTO.InfoWithOwner toInfoWithOwner(Room room, Member member);

	@Named("toInfoPage")
	default Page<RoomDTO.Info> toInfoPage(List<Room> roomList, Pageable pageable) {
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), roomList.size());
		List<RoomDTO.Info> dtoList = toInfoList(roomList.subList(start, end));
		return new PageImpl<>(dtoList, pageable, roomList.size());
	}

	@Named("toOwnerInfo")
	default MemberDTO.OwnerInfo toOwnerInfo(Member member) {
		return memberMapper.toOwnerInfo(member);
	}

}
