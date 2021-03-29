package com.company.commands;

import com.company.storables.DragonHolder;
import com.company.ui.User;

import java.util.LinkedList;

public class RemoveLowerKey implements CommandAction {
    @Override
    public String getLabel() {
        return "remove_lower_key";
    }

    @Override
    public String getArgumentLabel() {
        return "{key}";
    }

    @Override
    public String getDescription() {
        return "Removes elements from collection with keys less than {key}";
    }

    @Override
    public String execute(User commandedUser, String argument) {
        int keyThreshold;
        if (argument == null || argument.isEmpty())
            throw new IllegalArgumentException("Please specify Dragon key.");
        try {
            keyThreshold = Integer.parseInt(argument);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Illegal key: " + e.getMessage() + ".");
        }
        LinkedList<Integer> toRemove = new LinkedList<>();
        DragonHolder.getCollection().forEach((key, dragon) -> {
            if (key < keyThreshold && dragon.getOwner().equals(commandedUser.getUsername())) toRemove.add(key);
        });
        toRemove.forEach(key -> DragonHolder.getCollection().remove(key));
        return "Removed " + toRemove.size() + " Dragons.";
    }
}
