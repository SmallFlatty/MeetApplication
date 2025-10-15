package com.meetapp.Repositories;

import com.meetapp.Model.UserStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatusEntity,Long> {

    @Modifying
    @Query("UPDATE UserStatusEntity u SET u.status = :status WHERE u.fullName = :fullName")
    void changeUserStatus(@Param("fullName") String fullName, @Param("status") String status);

    @Query("SELECT u.fullName , u.status FROM UserStatusEntity u ")
    List<Object []> getAllUserStatuses();
}
