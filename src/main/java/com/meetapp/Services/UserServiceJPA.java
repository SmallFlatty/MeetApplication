package com.meetapp.Services;

import com.meetapp.Model.UserEntity;
import com.meetapp.Repositories.MeetRepository;
import com.meetapp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceJPA implements UserService {
    private final UserRepository userRepository;
    private final MeetRepository meetRepository;

    public UserServiceJPA(UserRepository userRepository, MeetRepository meetRepository){
        this.userRepository = userRepository;
        this.meetRepository = meetRepository;
    }


    @Override
    public UserEntity createUser(String email, String password, String fullName, String role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        String hashedPassword = null;
        try {
            hashedPassword = HashPassword.hashPassword(password);
        } catch (Exception e) {
            System.out.println("Hash Password Error" + e.getMessage());
        }

        userEntity.setPassword(hashedPassword);
        userEntity.setFullName(fullName);
        userEntity.setRole(role);

        return userRepository.save(userEntity);
    }

    @Override
    public boolean checkUser(String password, String fullName) {

        if(password == null || fullName == null){
            throw new IllegalArgumentException("Password or Full Name is invalid");
        }

        String hashedPassword = null;

        try{
            hashedPassword = HashPassword.hashPassword(password);
        }catch(Exception e){
            System.out.println("Hash Password Error" + e.getMessage());
        }


        return userRepository.checkUser(hashedPassword, fullName);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String getRole(long userId) {
        return userRepository.getRole(userId);
    }

    @Override
    public long getUserId(String fullName) {
        return  userRepository.getUserId(fullName);
    }

    @Override
    public String getFullName(long userId) {
        return userRepository.getFullName(userId);
    }

    @Override
    public List<Object[]> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public long getAdminId() {
        return userRepository.getAdminId();
    }


}
