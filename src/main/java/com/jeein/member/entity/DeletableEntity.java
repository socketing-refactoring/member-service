package com.jeein.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@ToString
@Getter
public class DeletableEntity extends BaseEntity {
    @Column(updatable = false)
    private Instant deletedAt;
}
