package com.meetapp.Services;

import com.meetapp.Model.UserEntity;
import com.meetapp.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceJPA implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceJPA(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserEntity createUser(String email, String password, String fullName, String role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        userEntity.setPassword(encodedPassword);

        userEntity.setFullName(fullName);
        userEntity.setRole(role);

        return userRepository.save(userEntity);
    }

    @Override
    public boolean checkUser(String password, String fullName) {

        if(password == null || fullName == null){
            throw new IllegalArgumentException("Password or Full Name is invalid");
        }
        String hashedPassword = passwordEncoder.encode(password);;
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

    @Override
    public UserEntity getUser(String fullName, String password) {
        UserEntity user = userRepository.getUser(fullName);

        if(user == null){
            return null;
        }

        boolean check = passwordEncoder.matches(password, user.getPassword());

        return check ? user : null;
    }

    @Override
    public UserEntity getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public UserEntity getAllUserInformation(String fullName) {
        return userRepository.AllUserInformation(fullName);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void changeName(String oldName, String newName) {
        userRepository.changeName(oldName, newName);
    }

    @Override
    public void changePassword(String Name, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.changePassword(Name, encodedPassword);

    }

    @Override
    public void changeEmail(String Name, String newEmail) {
        userRepository.changeEmail(Name, newEmail);
    }

}
