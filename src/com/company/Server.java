package com.company;

import com.company.commands.Read;
import com.company.storables.DragonHolder;
import com.company.ui.CommandExecutor;
import com.company.ui.CommandReader;
import com.company.ui.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main server class
 * @see Client
 */
public class Server {

    public static final List<User> registeredUsers = Collections.synchronizedList(new ArrayList<>());
    public static final File registeredUsersFile = new File("C:\\Users\\muram\\IdeaProjects\\Lab7\\users.txt");
    private static final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private static final PrintStream serverResponseStream = new PrintStream(outputStream);
    private static final CommandExecutor localExecutor = new CommandExecutor(CommandExecutor.allCommands, System.out);
    private static final CommandExecutor userExecutor = new CommandExecutor(CommandExecutor.allCommands, serverResponseStream);
    public static final User internalUser = new User("admin","admin");

    /**
     * Main server function
     *
     * @param args filename
     * @see CommandExecutor
     * @see Client
     */
    public static void main(String[] args) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Starting Server...");
        readCollectionFromFile(args);
        readUsersFromFile();
        BufferedReader localInput = new BufferedReader(new InputStreamReader(System.in));
        Selector selector = null;
        try {
            selector = Selector.open();
            selector.wakeup();
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress("localhost", 3333));
            datagramChannel.configureBlocking(false);
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Failed to start server. " + e.getMessage() + ".");
            System.exit(-2);
        }
        ExecutorService responseExecutorService = Executors.newCachedThreadPool();
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Server is active.");
        try {
            while (true) {
                try {
                    String line;
                    if (localInput.ready())
                        if ((line = localInput.readLine()) != null)
                            localExecutor.executeCommand(CommandReader.readCommandFromString(internalUser,line));
                    selector.select(1500);
                    selector.selectedKeys().stream().parallel().forEach(selectionKey ->
                    {
                        try {
                            if (selectionKey.isValid())
                                if (selectionKey.isReadable())
                                    responseExecutorService.submit(processCommand(selectionKey));
                        } catch (IOException e) {
                            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "IOException " + e.getMessage());
                        } catch (NullPointerException ignored) {}
                    });
                    Executors.newCachedThreadPool().submit(() -> {
                    });
                } catch (IOException e) {
                    System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "IOException " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(-3);
                }
            }
        } finally {
            responseExecutorService.shutdown();
        }
    }

    /**
     * Processes a command from client
     * @param key Selection key
     * @throws IOException If problem occurred while sending data to client
     * @return Server response
     */
    private static Runnable processCommand(SelectionKey key)
            throws IOException {
        DatagramChannel client = (DatagramChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(65536);
        SocketAddress address = client.receive(buffer);
        if (address == null) throw new NullPointerException("Address is null");
        byte[] data = buffer.array();
        buffer.clear();
        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
        byte[] response;
        try {
            CommandReader.Command command = (CommandReader.Command) iStream.readObject();
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + command.user.getUsername() + address.toString() + ": " + command.toString() + ".");
            //Отправляем данные клиенту
            try {
                userExecutor.executeCommand(command);
                response = outputStream.toByteArray();
                outputStream.reset();
            } catch (IllegalArgumentException e) {
                response = e.getMessage().getBytes();
            }
        } catch (StreamCorruptedException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            response = "Server received invalid packet. Please try again.".getBytes();
        }
        byte[] finalResponse = response;
        return ()-> {
            try {
                client.send(ByteBuffer.wrap(finalResponse), address);
            } catch (IOException e) {
                System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "IOException " + e.getMessage());
            }
        };
    }

    /**
     * Reads a collection from file
     * @see Read
     * @param filename Filename
     */
    private static void readCollectionFromFile(String[] filename) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Reading collection from file...");
        if (filename.length == 0)
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Input filename not specified by command line argument. Skipping...");
        else {
            try {
                CommandExecutor.setFile(filename[0]);
                try {
                    System.out.println(new Read().execute(internalUser,""));
                } catch (Exception e) {
                    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + e.getMessage() + " Skipping...");
                }
            } catch (NullPointerException e) {
                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Input filename is empty. Skipping...");
            }
        }
    }

    /**
     * Reads registered users from file
     * @see com.company.commands.Exit
     */
    private static void readUsersFromFile() {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Getting registered users...");
        ArrayList<User> userArrayList = new ArrayList<>();
        try {
            BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(registeredUsersFile));
            StringBuilder stringBuilder = new StringBuilder();
            while (fileReader.available() > 0)
                stringBuilder.append((char) fileReader.read());
            Arrays.stream(stringBuilder.toString().split("[\\r\\n]+"))
                    .forEach(line -> {
                        byte[] data = Base64.getDecoder().decode(line);
                        try {
                            ObjectInputStream objectInputStream = new ObjectInputStream(
                                    new ByteArrayInputStream(data));
                            User user = (User) objectInputStream.readObject();
                            userArrayList.add(user);
                            objectInputStream.close();
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Error reading from line " + line);
                            e.printStackTrace();
                        }
                    });
            registeredUsers.clear();
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Found " + userArrayList.size() + " registered users.");
            registeredUsers.addAll(userArrayList);
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Can't find file \"" + registeredUsersFile + "\".");
        } catch (SecurityException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Can't access file \"" + registeredUsersFile + "\".");
        } catch (IOException e) {
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + "Error occurred accessing file \"" + registeredUsersFile + "\".");
        }

    }
}
