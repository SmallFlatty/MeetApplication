package com.meetapp.Services;

import com.meetapp.Model.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity createUser(String email , String password , String fullName , String role);

    boolean checkUser(String password , String fullName);

    void deleteUser(long userId);

    String getRole(long userId);

    long getUserId(String fullName);

    String getFullName(long userId);

    List<Object[]> getAllUsers();

    long getAdminId();

    UserEntity getUser(String email , String password);

    UserEntity getUserById(long userId);

    UserEntity getAllUserInformation(String fullName);

    boolean emailExists(String email);

}
