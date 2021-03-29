package com.company.commands;

import com.company.storables.Dragon;
import com.company.storables.DragonHolder;
import com.company.ui.User;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CsvUpdateID implements CommandAction {
    @Override
    public String getLabel() {
        return "update_csv";
    }

    @Override
    public String getArgumentLabel() {
        return "{Dragon,in,csv,style}";
    }

    @Override
    public String getDescription() {
        return "Update {element} of collection by its id (CSV version).";
    }

    @Override
    public String execute(User commandedUser, String argument) {
        Dragon newDragon = new Dragon(argument);
        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference<Integer> dragonKey = new AtomicReference<>();
        AtomicReference<Long> dragonId = new AtomicReference<>();
        AtomicReference<Date> dragonCreationDate = new AtomicReference<>();
        DragonHolder.getCollection().forEach((key, value) -> {
            if (value.getId() == newDragon.getId()) {
                if(!value.getOwner().equals(commandedUser.getUsername()))
                    throw new IllegalArgumentException("Unauthorized Dragon access with id " + value.getId() +".");
                dragonKey.set(key);
                dragonId.set(value.getId());
                dragonCreationDate.set(value.getCreationDate());
                found.set(true);
            }
        });
        if (!found.get()) throw new IllegalArgumentException("Dragon with id '" + argument + "' not found");
        else {
            newDragon.setCreationDate(dragonCreationDate.get());
            DragonHolder.getCollection().put(dragonKey.get(), newDragon);
        }
        return "Update successful.";
    }
}