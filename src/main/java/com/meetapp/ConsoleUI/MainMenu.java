package com.meetapp.ConsoleUI;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Services.MeetService;
import com.meetapp.Services.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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
                        String choice = scanner.nextLine().toLowerCase();
                        switch (choice) {
                            case "dym":
                                List<MeetEntity> myWorkerMeetings = meetService.getMeetsForPerson(userId);

                                if(myWorkerMeetings.isEmpty()){
                                    System.out.println("❌ No meetings found.");
                                }

                                System.out.printf("%-4s | %-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "ID", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-----------------------------------------------------------------------------------------------------------");

                                int indexMy = 1;
                                for (MeetEntity m : myWorkerMeetings) {
                                    System.out.printf("%-4d | %-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexMy++,
                                            m.getId(),
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }

                                System.out.println("Write the meet id which you want to delete");
                                long myMeetId = scanner.nextLong();

                                boolean isCreatedByAdmin = meetService.meetsCreatedByAdmin(myMeetId);

                                if(isCreatedByAdmin){
                                    System.out.println("You cant delete your meet which was created by admin");
                                    break;
                                }else{
                                    meetService.deleteMeet(myMeetId);
                                    System.out.println("Your meet successfully deleted.");
                                }

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine3 =  scanner.nextLine();
                                if(CommandLine3.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine3.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;//I cant to delete the meeting which created by Admin!!!!!!!!!!!!!
                            case "cym":
                                System.out.println("Write a description of your meets");
                                String title = scanner.nextLine();

                                System.out.println("Enter date and time for meeting to start (format: yyyy-MM-ddTHH:mm):");
                                String startTime = scanner.nextLine();

                                LocalDateTime startsAt;

                                try{
                                    startsAt = LocalDateTime.parse(startTime);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid start time. Try format like: 2025-08-31T14:30");
                                    break;
                                }

                                System.out.println("Enter date and time for meeting to end (format: yyyy-MM-ddTHH:mm):");
                                String endTime = scanner.nextLine();

                                LocalDateTime endsAt;

                                try{
                                    endsAt = LocalDateTime.parse(endTime);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid end time. Try format like: 2025-08-31T15:30");
                                    break;
                                }

                                if(startsAt.isAfter(endsAt)){
                                    System.out.println("❌ End time must be after start time.");
                                    break;
                                }

                                System.out.println("Write a customer name ");
                                String customerName = scanner.nextLine();

                                meetService.createMeet(title,startsAt,endsAt,customerName,username,userId);

                                System.out.println("Your meet successfully created.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine4 =  scanner.nextLine();
                                if(CommandLine4.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine4.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;
                            case "sym":
                                List<MeetEntity> myMeets = meetService.getMeetsForPerson(userId);

                                if(myMeets.isEmpty()){
                                    System.out.println("X Empty Array");
                                    break;
                                }

                                System.out.printf("%-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-------------------------------------------------------------------------------------------");

                                int indexMyAdmin = 1;
                                for (MeetEntity m : myMeets) {
                                    System.out.printf("%-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexMyAdmin++,
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }
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
                        String choice = scanner.nextLine().toLowerCase();
                        switch (choice) {
                            case "du":
                                List<Object[]> users = userService.getAllUsers();

                                for(Object[] user : users){
                                    long id = (long)user[0];
                                    String fullName = (String)user[1];
                                    System.out.println(id + " - " + fullName);
                                }

                                System.out.println("Write a workers id which you want to delete");
                                long workerId = scanner.nextLong();

                                userService.deleteUser(workerId);
                                System.out.println("Your user successfully deleted.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine1 =  scanner.nextLine();
                                if(CommandLine1.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine1.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;
                            case "dwm":
                                List<MeetEntity> allAdminMeetings = meetService.getAllWorkersMeets(userId);

                                if (allAdminMeetings.isEmpty()) {
                                    System.out.println("❌ No meetings found.");
                                    return;
                                }

                                System.out.printf("%-4s | %-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "ID", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-----------------------------------------------------------------------------------------------------------");

                                int indexPer = 1;
                                for (MeetEntity m : allAdminMeetings) {
                                    System.out.printf("%-4d | %-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexPer++,
                                            m.getId(),
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }

                                System.out.println("Write the meet id which you want to delete");
                                long perMeetId = scanner.nextLong();

                                meetService.deleteMeet(perMeetId);
                                System.out.println("Your meet successfully deleted.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine2 =  scanner.nextLine();
                                if(CommandLine2.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine2.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;

                            case "dym":
                                List<MeetEntity> myAdminMeetings = meetService.getMeetsForPerson(userId);

                                if(myAdminMeetings.isEmpty()){
                                    System.out.println("❌ No meetings found.");
                                }

                                System.out.printf("%-4s | %-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "ID", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-----------------------------------------------------------------------------------------------------------");

                                int indexMy = 1;
                                for (MeetEntity m : myAdminMeetings) {
                                    System.out.printf("%-4d | %-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexMy++,
                                            m.getId(),
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }

                                System.out.println("Write the meet id which you want to delete");
                                long myMeetId = scanner.nextLong();

                                meetService.deleteMeet(myMeetId);
                                System.out.println("Your meet successfully deleted.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine3 =  scanner.nextLine();
                                if(CommandLine3.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine3.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;

                            case "cym":
                                System.out.println("Write a description of your meets");
                                String title = scanner.nextLine();

                                System.out.println("Enter date and time for meeting to start (format: yyyy-MM-ddTHH:mm):");
                                String startTime = scanner.nextLine();

                                LocalDateTime startsAt;

                                try{
                                    startsAt = LocalDateTime.parse(startTime);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid start time. Try format like: 2025-08-31T14:30");
                                    break;
                                }

                                System.out.println("Enter date and time for meeting to end (format: yyyy-MM-ddTHH:mm):");
                                String endTime = scanner.nextLine();

                                LocalDateTime endsAt;

                                try{
                                    endsAt = LocalDateTime.parse(endTime);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid end time. Try format like: 2025-08-31T15:30");
                                    break;
                                }

                                if(startsAt.isAfter(endsAt)){
                                    System.out.println("❌ End time must be after start time.");
                                    break;
                                }

                                System.out.println("Write a customer name ");
                                String customerName = scanner.nextLine();

                                meetService.createMeet(title,startsAt,endsAt,customerName,username,userId);

                                System.out.println("Your meet successfully created.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine4 =  scanner.nextLine();
                                if(CommandLine4.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine4.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;
                            case "cwm":
                                System.out.println("Write a description of your meets");
                                String titleForPerson = scanner.nextLine();

                                System.out.println("Enter date and time for meeting to start (format: yyyy-MM-ddTHH:mm):");
                                String startTimeForPerson = scanner.nextLine();

                                LocalDateTime startsAtForPerson;

                                try{
                                    startsAtForPerson = LocalDateTime.parse(startTimeForPerson);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid start time. Try format like: 2025-08-31T14:30");
                                    break;
                                }

                                System.out.println("Enter date and time for meeting to end (format: yyyy-MM-ddTHH:mm):");
                                String endTimeForPerson = scanner.nextLine();

                                LocalDateTime endsAtForPerson;

                                try{
                                    endsAtForPerson = LocalDateTime.parse(endTimeForPerson);
                                }catch(Exception e){
                                    System.out.println("❌ Invalid end time. Try format like: 2025-08-31T15:30");
                                    break;
                                }

                                if(startsAtForPerson.isAfter(endsAtForPerson)){
                                    System.out.println("❌ End time must be after start time.");
                                    break;
                                }

                                System.out.println("Write a customer name ");
                                String customerNameForPerson = scanner.nextLine();

                                List<Object[]> usersForPerson = userService.getAllUsers();

                                for(Object[] user : usersForPerson){
                                    long id = (long)user[0];
                                    String allUsers = (String)user[1];
                                    System.out.println(id + " - " + allUsers);
                                }

                                System.out.println("Write a users id for meeting");
                                long idForUser = Long.parseLong(scanner.nextLine());


                                meetService.createMeet(titleForPerson,startsAtForPerson,endsAtForPerson,customerNameForPerson,username,idForUser);
                                System.out.println("Your meet successfully created.");

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine5 =  scanner.nextLine();
                                if(CommandLine5.equalsIgnoreCase("logout")){
                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine5.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;

                            case "swm":
                                List<MeetEntity> workerMeets = meetService.getAllWorkersMeets(userId);

                                if(workerMeets.isEmpty()){
                                    System.out.println("X Empty Array");
                                    break;
                                }

                                System.out.printf("%-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-------------------------------------------------------------------------------------------");

                                int indexWork = 1;

                                for (MeetEntity m : workerMeets) {
                                    System.out.printf("%-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexWork++,
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }

                                System.out.println("Back - back to main menu");
                                System.out.println("LogOut - log out from your account ");
                                String CommandLine6 =  scanner.nextLine();
                                if(CommandLine6.equalsIgnoreCase("logout")){

                                    ConsoleUI consoleUI = new ConsoleUI(userService, meetService);
                                    consoleUI.start();
                                }else if(CommandLine6.equalsIgnoreCase("back")){
                                    break;
                                }
                                break;
                            case "sym":
                                List<MeetEntity> myAdminMeets = meetService.getMeetsForPerson(userId);

                                if(myAdminMeets.isEmpty()){
                                    System.out.println("X Empty Array");
                                    break;
                                }

                                System.out.printf("%-4s | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                        "#", "Title", "Start", "End", "Customer", "Created By");
                                System.out.println("-------------------------------------------------------------------------------------------");

                                int indexMyAdmin = 1;
                                for (MeetEntity m : myAdminMeets) {
                                    System.out.printf("%-4d | %-20s | %-16s | %-16s | %-20s | %-15s%n",
                                            indexMyAdmin++,
                                            m.getTitle(),
                                            m.getStart_at(),
                                            m.getEnd_at(),
                                            m.getCustomer_name(),
                                            m.getCreated_by());
                                }
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
