package Programmation_concurrente_distribuée;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TCP {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServerSocket serverSocket;
    private boolean isServer;
    private TicTacToe tic ;   
	public TCP(TicTacToe tic) {
           this.tic = tic;
	}

	public void starting() {
	    if (shouldStartServer()) {
	        if (serverSocket == null || serverSocket.isClosed()) {
	            openFirewallPort();
	            initializeServer(tic.getPort());
	        }
	        tic.setPlayer1_turn(true);
	        tic.setMyTurn(true);
	        isServer = true;

	        // Always prompt for the number of matches
	        String matchesInput = JOptionPane.showInputDialog(tic.getFrame(), "Enter Number of Matches:", "5");
	        int numMatches = Integer.parseInt(matchesInput);
	        tic.setNumberOfMatches(numMatches);
	        tic.setRandomDoublePointsMatch();
	    } else {
	        if (socket == null || socket.isClosed()) {
	            if (!connectToServer(tic.getIp(), tic.getPort())) {
	                System.out.println("Failed to connect to the server.");
	                return;
	            }
	        }
	        tic.setPlayer1_turn(false);
	        tic.setMyTurn(false);
	        isServer = false;
	    }
	}

    
	public boolean shouldStartServer() {
	     int response = JOptionPane.showConfirmDialog(null, "Do you want to start the server?", "Server or Client", JOptionPane.YES_NO_OPTION);
	     return response == JOptionPane.YES_OPTION;
	 }
	 
	 public void setupStreams() {
	     try {
	         oos = new ObjectOutputStream(socket.getOutputStream());
	         ois = new ObjectInputStream(socket.getInputStream());
	         new Thread(new Listener()).start();
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	 }
public void initializeServer(int port) {
	     try {
	    	 if (serverSocket != null && !serverSocket.isClosed()) {
	             serverSocket.close();  
	         }
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
	    	 if (socket != null && !socket.isClosed()) {
	             socket.close();  
	         }
	         socket = new Socket(ip, port);
	         setupStreams();
	         return true;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return false;
	     }
	 }
public void sendMove(int move) {
	     try {
	         oos.writeObject("MOVE:" + move + ":" + (isServer ? "X" : "O"));
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	 }
public class Listener implements Runnable {
		 public void run() {
	         while (true) {
	             try {
	                 String message = (String) ois.readObject();
	                 if (message.equals("GAME_OVER")) {
	                     SwingUtilities.invokeLater(() -> {
	                         for (JButton button : tic.getButtons()) {
	                             button.setEnabled(false);
	                         }
	                         tic.getTextfield().setText("Game Over");
	                     });
	                     break; 
	                 }
		                if (message.startsWith("MOVE:")) {
		                    String[] parts = message.split(":");
		                    int position = Integer.parseInt(parts[1]);
		                    SwingUtilities.invokeLater(() -> {
		                        if (tic.getButtons()[position].getText().equals("")) {
		                        	tic.getButtons()[position].setText(isServer ? "O" : "X"); 
		                        	tic.setPlayer1_turn(!tic.isPlayer1_turn());
                                    tic.setMyTurn(true);
		                            tic.updateTextfield();
		                            tic.check();
		                        }
		                    });
		                }
		            } catch (IOException | ClassNotFoundException e) {
		                e.printStackTrace();
		                break;
		            }
		        }
		    }
		}

public void openFirewallPort() {
    String command = "netsh advfirewall firewall add rule name=\"TicTacToePort\" dir=in action=allow protocol=TCP localport=22222";
    executeCommand(command, "Firewall port 22222 opened successfully.", "Failed to open port on firewall.");
}

public void closeFirewallPort() {
    String command = "netsh advfirewall firewall delete rule name=\"TicTacToePort\" protocol=TCP localport=22222";
    executeCommand(command, "Firewall port 22222 closed successfully.", "Failed to close port on firewall.");
}

public void executeCommand(String command, String successMessage, String errorMessage) {
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

public Socket getSocket() {
	return socket;
}

public void setSocket(Socket socket) {
	this.socket = socket;
}

public ObjectOutputStream getOos() {
	return oos;
}

public void setOos(ObjectOutputStream oos) {
	this.oos = oos;
}

public ObjectInputStream getOis() {
	return ois;
}

public void setOis(ObjectInputStream ois) {
	this.ois = ois;
}

public ServerSocket getServerSocket() {
	return serverSocket;
}

public void setServerSocket(ServerSocket serverSocket) {
	this.serverSocket = serverSocket;
}

public boolean isServer() {
	return isServer;
}

public void setServer(boolean isServer) {
	this.isServer = isServer;
}

public void sendMessage(String message) {
    try {
        oos.writeObject(message);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void closeNetworkConnection() {
    try {
        if (ois != null) {
            ois.close();
            ois = null;
        }
        if (oos != null) {
            oos.close();
            oos = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        if (isServer && serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public void restartNetwork() {
    closeNetworkConnection();  


    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    starting();
}






}
