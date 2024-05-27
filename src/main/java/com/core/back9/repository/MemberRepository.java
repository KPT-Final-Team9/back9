package com.core.back9.repository;

import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from member m join fetch m.tenant where m.email = :email and m.status = :status")
    Optional<Member> findByEmailAndStatus(String email, Status status);

    Optional<Member> findByPhoneNumberAndStatus(String phoneNumber, Status status);

}
