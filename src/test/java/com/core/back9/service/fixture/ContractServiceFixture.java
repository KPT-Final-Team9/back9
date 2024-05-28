package com.core.back9.service.fixture;

import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Usage;
import com.core.back9.mapper.ContractMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.TenantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class ContractServiceFixture {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BuildingRepository buildingRepository;


    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ContractMapper contractMapper;

    protected Building building;

    protected Room room1;
    protected Room room2;
    protected Room room3;

    protected Tenant tenant1;
    protected Tenant tenant2;
    protected Tenant tenant3;

    @BeforeEach
    void setUp() {

        em.createNativeQuery("ALTER TABLE members AUTO_INCREMENT = 1;")
                .executeUpdate();
        em.createNativeQuery("ALTER TABLE buildings AUTO_INCREMENT = 1;")
                .executeUpdate();
        em.createNativeQuery("ALTER TABLE rooms AUTO_INCREMENT = 1;")
                .executeUpdate();
        em.createNativeQuery("ALTER TABLE tenants AUTO_INCREMENT = 1;")
                .executeUpdate();
        em.createNativeQuery("ALTER TABLE contracts AUTO_INCREMENT = 1;")
                .executeUpdate();

        Member user = Member.builder()
                .email("test@test.com")
                .role(Role.USER)
                .build();
        Member owner = Member.builder()
                .email("test@test.com")
                .role(Role.OWNER)
                .build();
        Member admin = Member.builder()
                .email("test@test.com")
                .role(Role.ADMIN)
                .build();

        memberRepository.saveAll(List.of(user, owner, admin));

        building = Building.builder()
                .name("빌딩1")
                .address("빌딩 주소1")
                .zipCode("우편변호1")
                .build();
        buildingRepository.save(building);

        room1 = Room.builder()
                .building(building)
                .name("호실1")
                .floor("1층")
                .area(0)
                .usage(Usage.OFFICES)
                .member(owner) // 2L
                .build();
        room2 = Room.builder()
                .building(building)
                .name("호실2")
                .floor("2층")
                .area(0)
                .usage(Usage.OFFICES)
                .member(owner) // 2L
                .build();
        room3 = Room.builder()
                .building(building)
                .name("호실3")
                .floor("3층")
                .area(0)
                .usage(Usage.OFFICES)
                .member(owner) // 2L
                .build();
        roomRepository.saveAll(List.of(room1, room2, room3));

        tenant1 = Tenant.builder()
                .name("입주사1")
                .companyNumber("02-000-0000")
                .build();
        tenant2 = Tenant.builder()
                .name("입주사2")
                .companyNumber("02-000-0001")
                .build();
        tenant3 = Tenant.builder()
                .name("입주사3")
                .companyNumber("02-000-0002")
                .build();

        tenantRepository.saveAll(List.of(tenant1, tenant2, tenant3));

        em.flush();
        em.clear();
    }
}
