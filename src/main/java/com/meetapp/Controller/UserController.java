package com.meetapp.Controller;

import com.meetapp.Model.UserEntity;
import com.meetapp.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;


import java.io.File;
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

    @GetMapping("all-info")
    public UserEntity getAllUserInformation(@RequestParam String fullName) {
       return userService.getAllUserInformation(fullName);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadUserAvatar(@RequestParam("file") MultipartFile file, @RequestParam String fullName) {
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String folderPath = new File("UsersAvatar").getAbsolutePath();
            String avatarPath = folderPath + File.separator + fullName + "." + ext;

            File oldFile = new File(avatarPath);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            file.transferTo(new File(avatarPath));
            return ResponseEntity.ok("upload successfully");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(500).body("Upload failed " + ex.getMessage());
        }
    }

    // Save user with reload page
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String fullName, @RequestParam String password, HttpSession session) {
        UserEntity user = userService.getUser(fullName,password);

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        session.setAttribute("userId", user.getId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/checkme")
    public ResponseEntity<?> checkMe(HttpSession session) {
        Object userIdAttr = session.getAttribute("userId");

        if (userIdAttr == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Long userId;
        try {
            userId = Long.valueOf(userIdAttr.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("Invalid session user ID");
        }

        UserEntity user = userService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        return ResponseEntity.ok(user);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("userId");
        return ResponseEntity.ok("logged out");
    }
    //

}
