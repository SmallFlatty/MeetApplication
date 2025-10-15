package com.meetapp.Services;

import java.util.List;

public interface UserStatusService {
    void changeUserStatus(String fullName , String status);

    void setNewUserStatus(String fullName);

    List<Object []> getAllUserStatus();
}
