package com.meetapp.Controller;

import com.meetapp.Services.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status")
public class UserStatusController {
    private final UserStatusService userStatusService;

    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @PutMapping("/change-status")
    public void changeUserStatus(@RequestParam String fullName, @RequestParam String status) {
        userStatusService.changeUserStatus(fullName, status);
    }

    @PostMapping("/create-status")
    public void createUserStatus(@RequestParam String fullName){
        userStatusService.setNewUserStatus(fullName);
    }

    @GetMapping("/users-status")
    List<Object []> getUserStatuses(){
       return userStatusService.getAllUserStatus();
    }
}