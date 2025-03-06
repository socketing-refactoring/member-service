package com.jeein.member.repository;

import com.jeein.member.entity.MemberDetail;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberDetailRepository extends JpaRepository<MemberDetail, UUID> {}
