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


}
