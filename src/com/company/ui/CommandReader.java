package com.company.ui;

import com.company.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Class designed to get commands from buffered readers and strings
 *
 * @see CommandExecutor
 */
public class CommandReader {

    BufferedReader bufferedReader;

    /**
     * Command reader constructor
     *
     * @param bufferedReader buffered reader to get commands from
     */
    public CommandReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * Get an array of strings from System.in (separated by spaces)
     *
     * @return Array of strings
     */
    public static String[] getStringsFromTerminal() {
        return new CommandReader(new BufferedReader(new InputStreamReader(System.in))).getStringFromBufferedReader().split(" ");
    }

    /**
     * Get a command from string
     *
     * @param user User
     * @param singleString string to parse from
     * @return Command
     */
    public static Command readCommandFromString(User user, String singleString) {
        return (readCommandFromString(user,singleString.split(" ", 2)));
    }

    /**
     * Get a command from strings
     *
     * @param user User
     * @param input strings to parse from
     * @return Command
     */
    public static Command readCommandFromString(User user, String[] input) {
        if (input.length != 0) {
            input[0] = input[0].toLowerCase();
            if (input.length > 1)
                return new Command(user,input[0], input[1]);
            else
                return new Command(user,input[0]);
        } else return new Command(user);
    }

    /**
     * Gets a string from buffered reader line
     *
     * @return Received string
     */
    public String getStringFromBufferedReader() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() + ".");
            return "";
        }
    }

    /**
     * Get a command from Buffered Reader
     *
     * @param user User
     * @return Command
     */
    public Command readCommandFromBufferedReader(User user) {
        return readCommandFromString(user,getStringFromBufferedReader());
    }

    /**
     * User command class
     */
    public static class Command implements Serializable {
        public User user;
        public String commandString = "";
        public String argumentString = "";

        /**
         * User command constructor with argument
         *
         * @param user User
         * @param commandString  Command
         * @param argumentString Argument
         */
        public Command(User user, String commandString, String argumentString) {
            this.user = user;
            this.commandString = commandString;
            this.argumentString = argumentString;
        }

        /**
         * User command constructor without argument
         *
         * @param user User
         * @param CommandString Command
         */
        public Command(User user, String CommandString) {
            this.user = user;
            this.commandString = CommandString;
        }

        public Command(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Command{" + commandString + (argumentString.isBlank() ? "" : " ") + argumentString + "}";
        }
    }
}
