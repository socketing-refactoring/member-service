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
@ToString(callSuper = true, exclude = "manager")
public class ManagerDetail extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String password;

    @OneToOne private Manager manager;

    public void updatePassword(String password) {
        this.password = password;
    }
}
