package com.company.commands;

import com.company.ui.User;

public class Exit implements CommandAction {

    @Override
    public String getLabel() {
        return "exit";
    }

    public String getDescription() {
        return "Exit the program (without saving).";
    }

    public String execute(User commandedUser, String argument) {
        System.exit(0);
        return "Exited.";
    }
}
