package com.meetapp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class MessageController {
    private final JavaMailSender mailSender;

    public MessageController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, String> text){

        String usersProblem = text.get("usersProblem");

        if(usersProblem == null || usersProblem.isEmpty()){
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("rpsuport.help@gmail.com");
            message.setSubject("New problem request");
            message.setText(usersProblem);

            mailSender.send(message);

            return ResponseEntity.ok("All Good");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Some problems with sending message");
        }
    }
}
