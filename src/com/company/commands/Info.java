package com.company.commands;

import com.company.storables.DragonHolder;
import com.company.ui.User;

public class Info implements CommandAction {
    public String getLabel() {
        return "info";
    }

    public String getDescription() {
        return "Gives the info about collection.";
    }

    public String execute(User commandedUser, String argument) {
        return "Dragon collection, initialization date: " + DragonHolder.getInitializationDate() + ", number of elements: " + DragonHolder.getCollection().size() + ".";
    }
}
