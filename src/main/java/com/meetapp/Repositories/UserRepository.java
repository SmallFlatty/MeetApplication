package com.meetapp.Repositories;

import com.meetapp.Model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UserEntity u WHERE u.password = :password AND u.fullName = :fullName")
    boolean checkUser(@Param("password") String password,
                      @Param("fullName") String fullName);

    @Query(value = "SELECT role FROM users WHERE id = :userId", nativeQuery = true)
    String getRole(@Param("userId") long userId);

}
