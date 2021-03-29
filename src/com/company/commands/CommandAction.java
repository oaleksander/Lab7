package com.company.commands;

import com.company.ui.User;

/**
 * Command that every program action implements
 */
public interface CommandAction {

    /**
     * Command label
     *
     * @return string that user has to input to use the command
     */
    String getLabel();

    /**
     * Command argument format
     *
     * @return argument format
     */
    default String getArgumentLabel() {
        return "";
    }

    /**
     * Command description
     *
     * @return description
     */
    String getDescription();

    /**
     * Execute the command
     *
     * @param commandedUser User who issued the command
     * @param argument command arguments
     * @return Command execution result
     */
    String execute(User commandedUser, String argument);

}
