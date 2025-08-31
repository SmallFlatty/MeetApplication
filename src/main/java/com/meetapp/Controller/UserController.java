package com.meetapp.Controller;

import com.meetapp.Model.UserEntity;
import com.meetapp.Services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController  {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user")
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userService.createUser(
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getRole()
        );
    }

    @GetMapping("/validation")
    public boolean checkUser(@RequestParam String password , @RequestParam String fullName) {
        return userService.checkUser(password, fullName);
    }

    @DeleteMapping("/delete-user")
    public void deleteUser(@RequestParam long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/role")
    public String getRole(@RequestParam long userId) {
        return userService.getRole(userId);
    }

    @GetMapping("/get-id")
    public long getUserId(@RequestParam String fullName) {
        return userService.getUserId(fullName);
    }

    @GetMapping("/get-name")
    public String getName(@RequestParam long userId) {
        return userService.getFullName(userId);
    }

    @GetMapping("/get-users")
    public List<Object[]> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get-admin-role")
    public long getAdminRole() {
        return userService.getAdminId();
    }
}
