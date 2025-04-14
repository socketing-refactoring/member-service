package com.jeein.member.repository;

import com.jeein.member.entity.Manager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {

    @Query("SELECT m FROM Manager m WHERE m.deletedAt IS NULL")
    List<Manager> findAll();

    @Query("SELECT m FROM Manager m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Manager> findById(@Param("id") UUID id);

    @Query("SELECT m FROM Manager m WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Manager> findByEmail(String email);

    @Query("SELECT m FROM Manager m WHERE m.nickname = :nickname AND m.deletedAt IS NULL")
    Optional<Manager> findByNickname(String nickname);

    @Query(
            "SELECT m FROM Manager m LEFT JOIN FETCH m.managerDetail WHERE m.email = :email AND m.deletedAt IS NULL")
    Optional<Manager> findManagerWithDetailsByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Manager m SET m.deletedAt = :now WHERE m.id = :id AND m.deletedAt IS NULL")
    void softDelete(@Param("id") UUID id, @Param("now") Instant now);
}
