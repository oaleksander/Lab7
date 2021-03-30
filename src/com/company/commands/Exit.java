package com.company.commands;

import com.company.Server;
import com.company.ui.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Saves registered users to file and exits the program
 */
public class Exit implements CommandAction {

    @Override
    public String getLabel() {
        return "exit";
    }

    public String getDescription() {
        return "Exit the program (without saving collection).";
    }

    public String execute(User commandedUser, String argument) {
        try {
            Server.registeredUsersFile.createNewFile();
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(Server.registeredUsersFile));
            Server.registeredUsers.forEach(user -> {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(user);
                    fileWriter.write(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())+ "\n");
                    objectOutputStream.close();
                } catch (IOException e) {
                    System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Error writing user " + user.toString() + "to file.");
                }
            });
            fileWriter.close();
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Saved users.");
        } catch (FileNotFoundException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Can't find file \"" + Server.registeredUsersFile.toString() + "\".");
        } catch (SecurityException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Can't access file \"" + Server.registeredUsersFile.toString() + "\".");
        } catch (IOException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Error saving users to file " + Server.registeredUsersFile.toString() + ".");
        }
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Exiting program...");
        System.exit(0);
        return "Exited.";
    }
}
