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
            choice = choice.toLowerCase();
            switch (choice){
                case "register":
                        System.out.println("Enter your full name:");
                        String fullNameR = scanner.nextLine();

                        System.out.println("Enter your password:");
                        String passwordR = scanner.nextLine();

                        System.out.println("Enter your password confirmation:");
                        String passwordConfirmation = scanner.nextLine();

                        if(!passwordR.equals(passwordConfirmation)){
                            System.out.println("Passwords do not match");
                            break;
                        }

                        System.out.println("Enter your email:");
                        String email = scanner.nextLine();

                        System.out.println("Enter your role: ADMIN or WORKER");
                        String role = scanner.nextLine();

                        if(!role.equals("ADMIN") && !role.equals("WORKER")) {
                            System.out.println("Invalid role");
                            break;
                        }

                        userService.createUser(email, passwordR, fullNameR, role);
                        long userId = userService.getUserId(fullNameR);

                        MainMenu menu = new MainMenu(userService, meetService);
                        menu.Menu(role,userId);

                        return;
                case "signup":
                    int countTries = 1;
                    while (countTries < 4) {
                        System.out.println("Enter your full name:");
                        String fullNameS = scanner.nextLine();

                        System.out.println("Enter your password:");
                        String passwordS = scanner.nextLine();

                        boolean confirmation = userService.checkUser(passwordS, fullNameS);

                        if (confirmation) {
                            long userID = userService.getUserId(fullNameS);
                            String roleS = userService.getRole(userID);

                            MainMenu mainMenu = new MainMenu(userService, meetService);
                            mainMenu.Menu(roleS, userID);
                            break;
                        }
                        System.out.println("Invalid password or full name , tries "+countTries+"/3");
                        countTries++;
                    }
                    if(countTries == 4)
                        System.out.println("Too many attempts , try again later");

                    break;

                default:
                    System.out.println("Invalid choice");
                    continue;
            }
        }
    }
}
