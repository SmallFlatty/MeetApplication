package com.meetapp.Services;

import com.meetapp.Model.UserEntity;

public interface UserService {
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(UserEntity user);

    boolean checkUser(String password);
    void deleteUser(String id);

}
