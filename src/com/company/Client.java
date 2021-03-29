package com.company;

import com.company.storables.DragonUtils;
import com.company.ui.CommandExecutor;
import com.company.ui.CommandReader;
import com.company.ui.User;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;

/**
 * Main client class
 * @see Server
 */
public class Client {

    private static final String[] dragonCommands = {"insert", "update", "replace_if_greater_age"};
    private static final InetSocketAddress address = new InetSocketAddress("localhost", 3333);
    private static User user = new User("notYetLoggedInUser","");
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static ObjectOutput objectOutput;
    private static DatagramSocket datagramSocket = null;

    /**
     * Main client function
     *
     * @param args filename
     * @see CommandExecutor
     * @see Server
     */
    public static void main(String[] args) {
        BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

        String username;
        String password;
        try {
            System.out.println("Username: ");
            username = cin.readLine();
            System.out.println("Password: ");
            password = cin.readLine();
            user = new User(username,password);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        CommandReader commandReader = new CommandReader(cin);
        byteArrayOutputStream = new ByteArrayOutputStream();

        System.out.println("Logging in...");
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            datagramSocket.setSoTimeout(1500);
            byteArrayOutputStream.reset();
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            sendCommand(new CommandReader.Command(user,"register", username+" "+password));
            System.out.print(getResponse());
        } catch (IOException e) {
            System.out.println("Can't connect to server.");
            System.exit(-1);
        }

        System.out.println("Welcome to interactive Dragon Hashtable server. To get help, enter \"help\".");
        //noinspection InfiniteLoopStatement
        while (true) {
            CommandReader.Command command = commandReader.readCommandFromBufferedReader(user);
            try {
                byteArrayOutputStream.reset();
                objectOutput = new ObjectOutputStream(byteArrayOutputStream);
                if (sendCommand(command))
                    System.out.print(getResponse());
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.err.println(". Trying again...");
                try {
                    if (sendCommand(command))
                        System.out.print(getResponse());
                } catch (IOException e2) {
                    System.err.println("Failed. Please try again later.");
                }
            }
        }
    }

    /**
     * Gets a command, verifies it and sends it to server (or exits the program if that is the case)
     * @param userCommand Command input
     * @return true if Command was verified and sent
     * @throws IOException If problem occurred while sending to server
     */
    private static boolean sendCommand(CommandReader.Command userCommand) throws IOException {
        if (userCommand.commandString.equals("exit")) {
            System.out.println("Exiting...");
            byteArrayOutputStream.close();
            datagramSocket.close();
            System.exit(0);
        }
        if (userCommand.commandString.equals("register")) {
            String[] arguments = userCommand.argumentString.split(" ",2);
            if(arguments.length == 2)
                user = new User(arguments[0],arguments[1]);
            else if(arguments.length == 1)
                user = new User(arguments[0]);
        }
        if (Arrays.stream(CommandExecutor.userCommands).parallel().noneMatch(command -> command.getLabel().equals(userCommand.commandString))) {
            System.err.println("Unknown command \"" + userCommand.commandString + "\". try \"help\" for list of commands");
            return false;
        }
        if (!Arrays.stream(CommandExecutor.userCommands).parallel().filter(command -> command.getLabel().equals(userCommand.commandString)).findAny().get().getArgumentLabel().equals("")
                && userCommand.argumentString.isBlank()) {
            System.err.println("Please specify command argument.");
            return false;
        }
        Date date = new Date();
        String dragonString;
        if (Arrays.stream(dragonCommands).parallel().anyMatch(command -> command.equals(userCommand.commandString))) {
            userCommand.commandString += "_csv";
            if (userCommand.commandString.equals("update_id_csv")) {
                try {
                    dragonString = DragonUtils.inputDragonFromConsole(user,Long.parseLong(userCommand.argumentString), date).toCsvString();
                    userCommand.argumentString = dragonString;
                } catch (NumberFormatException e) {
                    System.err.println("Can't parse dragon ID from " + userCommand.argumentString + ".");
                }
            } else {
                dragonString = DragonUtils.inputDragonFromConsole(user,-1, date).toCsvString(); //-1 value will be replaced with new ID
                userCommand.argumentString = userCommand.argumentString + "," + dragonString;
            }

        }
        objectOutput.writeObject(userCommand);
        byte[] data = byteArrayOutputStream.toByteArray();
        datagramSocket.send(new DatagramPacket(data, data.length, address));
        return true;
    }

    /**
     * Gets a message from a server
     * @return message
     * @throws IOException If problem occurred while receiving from server
     */
    private static String getResponse() throws IOException {
        byte[] data = new byte[65536];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        datagramSocket.receive(receivePacket);
        return new String(receivePacket.getData());
    }
}
