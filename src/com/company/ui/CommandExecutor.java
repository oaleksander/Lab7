package com.company.ui;

import com.company.commands.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main command execution runnable
 */
public class CommandExecutor {

    public static final List<User> registeredUsers = Collections.synchronizedList(new ArrayList<>());

    /**
     * All possible commands
     */
    public static final CommandAction[] allCommands = {
            new Help(),
            new Info(),
            new Show(),
            new Insert(),
            new UpdateID(),
            new RemoveKey(),
            new Clear(),
            new Save(),
            new Execute_script(),
            new Exit(),
            new ReplaceIfGreaterAge(),
            new RemoveGreaterKey(),
            new RemoveLowerKey(),
            new RemoveAllAge(),
            new FilterLessThanType(),
            new PrintDescending(),
            new Read(),
            new CsvInsert(),
            new CsvUpdateID(),
            new CsvReplaceIfGreaterAge(),
            new Register()
    };
    /**
     * Commands that user can use
     */
    public static final CommandAction[] userCommands = {
            new Help(),
            new Info(),
            new Show(),
            new Insert(),
            new UpdateID(),
            new RemoveKey(),
            new Clear(),
            new Execute_script(),
            new Exit(),
            new ReplaceIfGreaterAge(),
            new RemoveGreaterKey(),
            new RemoveLowerKey(),
            new RemoveAllAge(),
            new FilterLessThanType(),
            new PrintDescending(),
            new Register()
            //new Read(),
            //new CsvInsert(),
            //new CsvUpdateID(),
            //new CsvReplaceIfGreaterAge()
    };
    /**
     * Programs that execute_script can use
     */
    public static final CommandAction[] scriptCommands = {
            new Help(),
            new Info(),
            new Show(),
            //new Insert(),
            //new UpdateID(),
            new RemoveKey(),
            new Clear(),
            new Save(),
            //new Execute_script(),
            new Exit(),
            //new ReplaceIfGreaterAge(),
            new RemoveGreaterKey(),
            new RemoveLowerKey(),
            new RemoveAllAge(),
            new FilterLessThanType(),
            new PrintDescending(),
            //new Read(),
            new CsvInsert(),
            new CsvUpdateID(),
            new CsvReplaceIfGreaterAge()
    };
    private static File file = new File("C:\\Users\\muram\\IdeaProjects\\Lab5\\file.csv");
    private final PrintStream printStream;
    private final CommandAction[] availableCommands;

    /**
     * User runnable constructor
     *
     * @param availableCommands set of available commands
     * @param printStream       PrintStream to output responses to
     */
    public CommandExecutor(CommandAction[] availableCommands, PrintStream printStream) {
        this.availableCommands = availableCommands;
        this.printStream = printStream;
    }

    /**
     * Get file specified by command line argument
     *
     * @return File
     */
    public static File getFile() {
        return file;
    }

    /**
     * Set file to save/read collection from
     *
     * @param fileName File name
     */
    public static void setFile(String fileName) {
        file = new File(fileName);
    }

    /**
     * Execute specified user command
     *
     * @param command User command
     */
    public void execute(CommandReader.Command command) {
        AtomicReference<String> response = new AtomicReference<>("Command gave no response.");
        if(registeredUsers.stream().noneMatch(user -> user.equals(command.user)) && !command.commandString.equals("register"))
            response.set("Unauthorized access denied.");
        if (Arrays.stream(availableCommands).parallel().noneMatch(availableCommand -> availableCommand.getLabel().equals(command.commandString))) {
            response.set("Unknown command \"" + command.commandString + "\". try \"help\" for list of commands");
        } else
            try {
                Arrays.stream(availableCommands).forEach(availableCommand -> {
                    if (command.commandString.equals(availableCommand.getLabel()))
                        response.set(availableCommand.execute(command.user, command.argumentString));
                });
            } catch (IllegalArgumentException e) {
                response.set(e.getMessage());
            } catch (Exception e) {
                response.set("Unexpected error: " + e.getMessage() + ". This is a bug!");
                e.printStackTrace();
            }
        printStream.println(response.get());
    }
}

