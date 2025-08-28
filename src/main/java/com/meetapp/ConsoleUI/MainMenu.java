package com.meetapp.ConsoleUI;


import com.meetapp.Repositories.UserRepository;
import com.meetapp.Services.MeetService;
import com.meetapp.Services.UserService;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MainMenu {

    private final UserService userService;
    private final MeetService meetService;


    public MainMenu(UserService userService, MeetService meetService) {

        this.userService = userService;
        this.meetService = meetService;
    }


    protected void Menu(String role , long userId){
        Scanner scanner = new Scanner(System.in);
        String username = userService.getFullName(userId);
        while(true){
            switch (role){
                case "WORKER":
                    while(true){
                        System.out.println("DYM - for deleting your meets");
                        System.out.println("CYM - for creating your meets");
                        System.out.println("SYM - get your meets");
                        System.out.print(username + " : ");
                        String choice = scanner.nextLine();
                        switch (choice) {
                            case "DYM":
                            case "CYM":
                            case "SYM":

                        }

                    }
                case "ADMIN":
                    while(true) {
                        System.out.println("DU - for deleting user");
                        System.out.println("DWM - for deleting workers meet");
                        System.out.println("DYM - for deleting your meets");
                        System.out.println("CYM - for creating your meets");
                        System.out.println("CWM - for creating workers meets");
                        System.out.println("SWM - get all workers meets");
                        System.out.println("SYM - get your meets");
                        System.out.print(username + " : ");
                        String choice = scanner.nextLine();
                        switch (choice) {
                            case "DU":
                                //Write new method to get all users full names and id
                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine1 =  scanner.nextLine();
                                if(CommandLine1.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine1.equalsIgnoreCase("back")){
                                    break;
                                }
                            case "DWM":
                            case "DYM":
                            case "CYM":
                            case "CWM":
                            case "SWM":

                            case "SYM":
                                meetService.getMeetsForPerson(userId);//Arrays
                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine7 =  scanner.nextLine();
                                if(CommandLine7.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine7.equalsIgnoreCase("back")){
                                    break;
                                }
                        }
                    }
            }
        }
    }
}
