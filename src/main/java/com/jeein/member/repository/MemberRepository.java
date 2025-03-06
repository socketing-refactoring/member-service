package com.jeein.member.repository;

import com.jeein.member.entity.Member;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL")
    List<Member> findAll();

    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Member> findById(@Param("id") UUID id);

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.nickname = :nickname AND m.deletedAt IS NULL")
    Optional<Member> findByNickname(String nickname);

    @Query(
            "SELECT m FROM Member m LEFT JOIN FETCH m.memberDetail WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Member> findMemberWithDetailsByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Member m SET m.deletedAt = CURRENT_TIMESTAMP WHERE m.id = :id AND m.deletedAt IS NULL")
    void softDelete(@Param("id") UUID id);
}
