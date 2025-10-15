package com.meetapp.Services;

import com.meetapp.Model.UserStatusEntity;
import com.meetapp.Repositories.UserStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatusServiceJPA implements UserStatusService {

    private final UserStatusRepository userStatusRepository;

    public UserStatusServiceJPA(UserStatusRepository userStatusRepository) {
        this.userStatusRepository = userStatusRepository;
    }

    @Transactional
    @Override
    public void changeUserStatus(String fullName, String status) {
        userStatusRepository.changeUserStatus(fullName,status);
    }

    @Override
    public void setNewUserStatus(String fullName) {
        UserStatusEntity userStatusEntity = new UserStatusEntity();
        userStatusEntity.setFullName(fullName);
        userStatusEntity.setStatus("Offline");

        userStatusRepository.save(userStatusEntity);
    }

    @Override
    public List<Object[]> getAllUserStatus() {
        return userStatusRepository.getAllUserStatuses();
    }

}
