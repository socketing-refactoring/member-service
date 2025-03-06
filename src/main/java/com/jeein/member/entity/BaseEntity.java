package com.jeein.member.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@ToString
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    @LastModifiedDate
    private Instant updatedAt;
}
