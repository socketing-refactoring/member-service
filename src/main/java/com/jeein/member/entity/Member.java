package com.jeein.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true, exclude = "memberDetail")
public class Member extends DeletableEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @OneToOne(
            mappedBy = "member",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private MemberDetail memberDetail;

    public void addMemberDetail(MemberDetail memberDetail) {
        this.memberDetail = memberDetail;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
