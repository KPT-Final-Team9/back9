package com.core.back9.repository;

import com.core.back9.common.config.AuditingConfig;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
@Import(value = AuditingConfig.class)
class ScoreRepositoryTest {

	@Autowired
	private ScoreRepository scoreRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Score score;
	private Building building;
	private Room room;
	private Member admin;
	private Member owner;
	private Member user;

	@BeforeEach
	public void initSetting() {
		admin = Member.builder()
		  .email("admin@gmail.com")
		  .role(Role.ADMIN)
		  .status(Status.REGISTER)
		  .build();
		memberRepository.save(admin);
		owner = Member.builder()
		  .email("owner@gmail.com")
		  .role(Role.OWNER)
		  .status(Status.REGISTER)
		  .build();
		memberRepository.save(owner);
		user = Member.builder()
		  .email("user@gmail.com")
		  .role(Role.USER)
		  .status(Status.REGISTER)
		  .build();
		memberRepository.save(user);

		room = Room.builder()
		  .building(building)
		  .name("room name 1")
		  .floor("room floor 1")
		  .area(84F)
		  .usage(Usage.OFFICES)
		  .member(owner)
		  .build();
		roomRepository.save(room);
	}

	@Test
	public void givenScoreEntityWhenSaveScore() {
		Score newScore = Score.builder()
		  .score(0)
		  .comment("")
		  .bookmark(false)
		  .ratingType(RatingType.FACILITY)
		  .room(room)
		  .member(user)
		  .status(Status.REGISTER)
		  .build();

		Score savedScore = scoreRepository.save(newScore);
		assertThat(savedScore).isNotNull();
		assertThat(savedScore.getScore()).isEqualTo(newScore.getScore());
		assertThat(savedScore.getRatingType()).isEqualTo(newScore.getRatingType());
		assertThat(savedScore.getRoom().getMember()).isEqualTo(owner);
		assertThat(savedScore.getMember()).isEqualTo(user);
	}

	@Test
	public void givenUpdateRequestWhenUpdateEntityThenUpdateResponse() {
		Score newScore = Score.builder()
		  .score(0)
		  .comment("")
		  .bookmark(false)
		  .ratingType(RatingType.FACILITY)
		  .room(room)
		  .member(user)
		  .status(Status.REGISTER)
		  .build();

		Score savedScore = scoreRepository.save(newScore);
		long savedScoreId = savedScore.getId();

		ScoreDTO.UpdateRequest request = ScoreDTO.UpdateRequest.builder()
		  .score(90)
		  .comment("hello")
		  .build();

		Score validScore = scoreRepository.getValidScoreWithIdAndMemberIdAndStatus(savedScoreId, user.getId(), Status.REGISTER);
		validScore.updateScore(request);

		assertThat(validScore).isNotNull();
		assertThat(validScore.getScore()).isEqualTo(request.getScore());
		assertThat(validScore.getComment()).isEqualTo(request.getComment());
	}

}
