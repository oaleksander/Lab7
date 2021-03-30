package com.company.commands;

import com.company.Server;
import com.company.ui.User;

import java.util.Arrays;

public class Register implements CommandAction{

    @Override
    public String getLabel() {
        return "register";
    }

    @Override
    public String getArgumentLabel() {
        return "{username} [password]";
    }

    @Override
    public String getDescription() {
        return "Register a new user";
    }

    @Override
    public String execute(User commandedUser, String argument) {
        String[] arguments = argument.split(" ",2);
        User newUser;
        if(arguments.length == 2)
            newUser = new User(arguments[0],arguments[1]);
        else if(arguments.length == 1)
            newUser = new User(arguments[0]);
        else
            return "Failed to register. Did you input valid username and password? Try typing register {username} [password] to try again.";
        if(Server.registeredUsers.stream().anyMatch(newUser::equals))
            return "Logged in successfully.";
        if (Server.registeredUsers.stream().anyMatch(user -> user.getUsername().equals(newUser.getUsername()) && !Arrays.equals(user.getPasswordMD5(), newUser.getPasswordMD5())))
            return "Wrong password. Further commands will not be accepted. Try typing register {username} [password] to try again.";
        Server.registeredUsers.add(newUser);
        return "Registered new user successfully.";
    }
}
