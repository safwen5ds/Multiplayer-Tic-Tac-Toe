package Programmation_concurrente_distribuée;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Network {
	private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Game game; // Reference to the game instance to update the game state
    private boolean isServer;
    private int port; // Added to store the port number

    public Network(Game game, boolean isServer, int port) {
        this.game = game;
        this.isServer = isServer;
        this.port = port; // Store the port number
        if (isServer) {
            openFirewallPort(port); // Open firewall port when initializing as a server
        }
    }

    public void initializeConnection(String ip, int port) {
        if (isServer) {
            initializeServer(port);
        } else {
            connectToServer(ip, port);
        }
    }

    private void initializeServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for a client ...");
            socket = serverSocket.accept();
            setupStreams();
        } catch (IOException e) {
            e.printStackTrace();
            closeFirewallPort(port); // Close the firewall port in case of an error
        }
    }

    private boolean connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            setupStreams();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupStreams() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            new Thread(new Listener(ois, game)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int move, String symbol) {
        try {
            oos.writeObject("MOVE:" + move + ":" + symbol);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openFirewallPort(int port) {
        String command = "netsh advfirewall firewall add rule name=\"TicTacToePort\" dir=in action=allow protocol=TCP localport=" + port;
        executeCommand(command, "Firewall port " + port + " opened successfully.", "Failed to open port on firewall.");
    }

    public void closeFirewallPort(int port) {
        String command = "netsh advfirewall firewall delete rule name=\"TicTacToePort\" protocol=TCP localport=" + port;
        executeCommand(command, "Firewall port " + port + " closed successfully.", "Failed to close port on firewall.");
    }

    private void executeCommand(String command, String successMessage, String errorMessage) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(successMessage);
            } else {
                System.out.println(errorMessage + " Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (isServer) {
            closeFirewallPort(port); 
        }

    }


    
}
