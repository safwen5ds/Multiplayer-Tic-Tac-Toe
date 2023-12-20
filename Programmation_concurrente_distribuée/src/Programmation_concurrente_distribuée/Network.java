package Programmation_concurrente_distribuée;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Network {
    private TicTacToe ticTacToe;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Network(TicTacToe ticTacToe) {
        this.ticTacToe = ticTacToe;
    }

    public void openFirewallPort() {
        String command = "netsh advfirewall firewall add rule name=\"TicTacToePort\" dir=in action=allow protocol=TCP localport=22222";
        executeCommand(command, "Firewall port 22222 opened successfully.", "Failed to open port on firewall.");
    }

    public void closeFirewallPort() {
        String command = "netsh advfirewall firewall delete rule name=\"TicTacToePort\" protocol=TCP localport=22222";
        executeCommand(command, "Firewall port 22222 closed successfully.", "Failed to close port on firewall.");
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

    public boolean shouldStartServer() {
        int response = JOptionPane.showConfirmDialog(null, "Do you want to start the server?", "Server or Client", JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }

    public void initializeServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for a client ...");
            socket = serverSocket.accept();
            setupStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connectToServer(String ip, int port) {
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
            new Thread(new Listener()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int move) {
        try {
            oos.writeObject("MOVE:" + move + ":" + (ticTacToe.isServer() ? "X" : "O"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Listener implements Runnable {
        public void run() {
            while (true) {
                try {
                    String message = (String) ois.readObject();
                    if (message.startsWith("MOVE:")) {
                        String[] parts = message.split(":");
                        int position = Integer.parseInt(parts[1]);
                        // Handle the move received from the opponent
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
