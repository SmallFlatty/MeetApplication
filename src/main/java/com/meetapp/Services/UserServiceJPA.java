package com.meetapp.Services;

import com.meetapp.Model.UserEntity;
import com.meetapp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceJPA implements UserService {
    private final UserRepository userRepository;

    public UserServiceJPA(UserRepository userRepository){
        this.userRepository = userRepository;
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
    public UserEntity updateActiveUser(String fullName, boolean active) {
        return null;
    }

    @Override
    public boolean checkUser(String password, String fullName) {
        String hashedPassword = null;

        try{
            hashedPassword = HashPassword.hashPassword(password);
        }catch(Exception e){
            System.out.println("Hash Password Error" + e.getMessage());
        }


        return userRepository.checkUser(hashedPassword, fullName);
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public String getRole(long userId) {
        return userRepository.getRole(userId);
    }

    @Override
    public String getUserId(String fullName) {
        return  null;
    }
}
