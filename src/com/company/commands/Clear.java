package com.company.commands;

import com.company.storables.DragonHolder;
import com.company.ui.User;

public class Clear implements CommandAction {
    public String getLabel() {
        return "clear";
    }

    public String getDescription() {
        return "Clear the collection.";
    }

    @Override
    public String execute(User commandedUser, String argument) {
        DragonHolder.getCollection().clear();
        return "Collection cleared.";
    }
}
