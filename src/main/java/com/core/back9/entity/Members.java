package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.SignType;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "members") // 향후 platform 로그인 고려 시 index 추가해 검색속도 높이는 것도 고려해보면 좋을 듯함
public class Members extends BaseEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign_type")
    private SignType signType;

    @Column(name = "firebase_uid")
    private String firebaseUid; // 즉시 적용여부가 확실치 않아 nullable = true로 남겨두었음

    @Column(name = "firebase_fcm_token")
    private String firebaseFcmToken; // 즉시 적용여부가 확실치 않아 nullable = true로 남겨두었음

    @Builder
    private Members(String email, String password, Role role, String phoneNumber, Status status, SignType signType, String firebaseUid, String firebaseFcmToken) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.signType = signType;
        this.firebaseUid = firebaseUid;
        this.firebaseFcmToken = firebaseFcmToken;
    }

}
