package com.jeein.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true, exclude = "member")
public class MemberDetail extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String password;

    @OneToOne private Member member;

    public void updatePassword(String password) {
        this.password = password;
    }
}
