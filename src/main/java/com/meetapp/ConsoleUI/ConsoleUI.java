package com.meetapp.ConsoleUI;

import com.meetapp.Services.MeetService;
import com.meetapp.Services.UserService;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleUI {
    private final UserService userService;
    private final MeetService meetService;

    public ConsoleUI(UserService userService, MeetService meetService) {
        this.userService = userService;
        this.meetService = meetService;
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Register - for registration , SignUp - for sign up");
            System.out.println("Enter your choice:");
            String choice = scanner.nextLine();
            switch (choice){
                case "Register":
                    while (true){
                        System.out.println("Enter your full name:");
                        String fullName = scanner.nextLine();

                        System.out.println("Enter your password:");
                        String password = scanner.nextLine();

                        System.out.println("Enter your password confirmation:");
                        String passwordConfirmation = scanner.nextLine();

                            if(password.equals(passwordConfirmation)){
                                System.out.println("Enter your email:");
                                String email = scanner.nextLine();

                                System.out.println("Enter your role: ADMIN or WORKER");
                                String role = scanner.nextLine();

                                if(!role.equals("ADMIN") && !role.equals("WORKER")){
                                    System.out.println("Invalid role");
                                }else{
                                    userService.createUser(email, password, fullName, role);
                                    long userId = userService.getUserId(fullName);
                                    MainMenu menu = new MainMenu(userService, meetService);
                                    menu.Menu(role,userId);
                                }
                            }else{
                                continue;
                            }
                        }
                case "SignUp":
                    while (true) {
                        // write a method to check existing users full name in Data Base , add 3 tries to sign up
                        System.out.println("Enter your full name:");
                        String fullName = scanner.nextLine();

                        System.out.println("Enter your password:");
                        String password = scanner.nextLine();

                        boolean Confirmation = userService.checkUser(password, fullName);

                        if(Confirmation){
                            long userID = userService.getUserId(fullName);
                            String role = userService.getRole(userID);

                            MainMenu mainMenu = new MainMenu(userService,meetService);
                            mainMenu.Menu(role,userID);
                        }else{
                            break;
                        }

                    }
                default:
                    System.out.println("Invalid choice");
                    continue;
            }
        }
    }
}
