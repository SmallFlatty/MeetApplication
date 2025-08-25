package com.meetapp.Services;

import com.meetapp.Model.UserEntity;

public interface UserService {
    UserEntity createUser(String email , String password , String fullName , String role);

    UserEntity updateActiveUser(String fullName , boolean active);

    boolean checkUser(String password , String fullName);

    void deleteUser(String userId);

    String getRole(long userId);

    String getUserId(String fullName);
}
