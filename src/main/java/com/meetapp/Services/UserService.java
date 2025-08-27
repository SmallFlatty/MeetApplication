package com.meetapp.Services;

import com.meetapp.Model.UserEntity;

public interface UserService {
    UserEntity createUser(String email , String password , String fullName , String role);

    boolean checkUser(String password , String fullName);

    void deleteUser(long userId);

    String getRole(long userId);

    long getUserId(String fullName);
}
