package com.meetapp.Controller;

import com.meetapp.Model.UserEntity;
import com.meetapp.Services.UserStatusService;
import com.meetapp.Services.VerificationNameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/verify")
public class VerificationNameController {

    private final VerificationNameService verificationNameService;

    public VerificationNameController(VerificationNameService verificationNameService) {
        this.verificationNameService = verificationNameService;
    }

    @GetMapping("/all-info")
    public ResponseEntity<?> getUser(@RequestParam String signed) {
        if (!HmacUtils.verifySignedName(signed)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        String fullName = HmacUtils.extractFullName(signed);
        Optional<UserEntity> user = userRepository.findByFullName(fullName);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user.get());
    }

}
