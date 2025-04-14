package com.jeein.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true, exclude = "managerDetail")
public class Manager extends DeletableEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @OneToOne(
            mappedBy = "manager",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private ManagerDetail managerDetail;

    public void addManagerDetail(ManagerDetail managerDetail) {
        this.managerDetail = managerDetail;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
