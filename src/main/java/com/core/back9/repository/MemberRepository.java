package com.core.back9.repository;

import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from member m join fetch m.tenant where m.email = :email and m.role = :role and m.status = :status")
    Optional<Member> findUserByEmailAndStatus(String email, Role role, Status status);

    Optional<Member> findFirstByIdAndRoleAndStatus(Long id, Role role, Status status);

    Optional<Member> findByEmailAndStatus(String email, Status status);

    Optional<Member> findByEmailAndRoleAndStatus(String email, Role role, Status status);

    Optional<Member> findByPhoneNumberAndRoleAndStatus(String phoneNumber, Role role, Status status);

    default Member getValidMemberWithIdAndRole(Long id, Role role, Status status) {
        return findFirstByIdAndRoleAndStatus(id, role, status)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_PRINCIPAL));
    }

    Optional<Member> findFirstByEmailAndStatus(String email, Status status);

    default Member getValidMemberWithEmailAndStatus(String email, Status status) {
        return findFirstByEmailAndStatus(email, status)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER));
    }

    List<Member> findAllByTenantIdAndStatus(Long tenantId, Status status);

}
