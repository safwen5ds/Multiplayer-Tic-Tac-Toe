package Programmation_concurrente_distribuée.Controller;

import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Programmation_concurrente_distribuée.View.TicTacToe;

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
		    final JDialog dialog = new JDialog();
		    dialog.setModal(true); 
		    dialog.setUndecorated(true);
		    dialog.setTitle("Server Status");
		    dialog.setLayout(new BorderLayout());

		    JLabel label = new JLabel("Server started. Waiting for a client ...", SwingConstants.CENTER);
		    dialog.add(label, BorderLayout.CENTER);

		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(e -> {
		    	closeFirewallPort();
		        closeNetworkConnection();
		        tic.getFrame().dispose();
		        tic.getPointsFrame().dispose();
		        dialog.dispose();
		    });
		    dialog.add(cancelButton, BorderLayout.SOUTH);

		    dialog.setSize(300, 100); 
		    dialog.setLocationRelativeTo(null); 

		    new Thread(() -> {
		        try {
		            if (serverSocket != null && !serverSocket.isClosed()) {
		                serverSocket.close();
		            }
		            serverSocket = new ServerSocket(port);
		            SwingUtilities.invokeLater(() -> dialog.setVisible(true)); 
		            socket = serverSocket.accept(); 
		            SwingUtilities.invokeLater(() -> dialog.dispose()); 
		            setupStreams();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }).start();
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
	                 System.out.println("Received message: " + message); 
	                 if (message.startsWith("NUMBER_OF_MATCHES:")) {
	                     int numberOfMatches = Integer.parseInt(message.split(":")[1]);
	                     tic.setNumberOfMatches(numberOfMatches);
	                 }
	                 
	                 if (message.startsWith("DOUBLE_POINTS_MATCH:")) {
	                     String[] parts = message.split(":");
	                     int matchNumber = Integer.parseInt(parts[1]);
	                     tic.setDoublePointsMatch(matchNumber);
	                 }
	                 
	                 if (message.startsWith("MATCH_NUMBER:")) {
	                     int matchNumber = Integer.parseInt(message.split(":")[1]);
	                     SwingUtilities.invokeLater(() -> {
	                         tic.setmatchnumber(matchNumber);
	                     });
	                 }
	                 
	                 if (message.startsWith("NEXT_MATCH:")) {
	                     String nextMatchMessage = message.substring("NEXT_MATCH:".length());
	                     SwingUtilities.invokeLater(() -> {
	                         JOptionPane.showMessageDialog(tic.getFrame(), nextMatchMessage);
	                         tic.resetGame();
	                     });
	                 }
	                 
	                 if (message.startsWith("DOUBLE_START"))
	                 {
	                	 JOptionPane.showMessageDialog(tic.getFrame(),
	                			    "<html><body style='text-align: center;'>" +
	                			    "<strong>⚡️ Double Points Round! ⚡️</strong><br>" +
	                			    "<strong>Make it count!</strong>" +
	                			    "</body></html>",
	                			    "Special Match!",
	                			    JOptionPane.INFORMATION_MESSAGE);


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

		                 if (message.startsWith("GAME_OUTCOME:") ) {
		                	    String winnerText = message.substring("GAME_OUTCOME:".length());
		                	    SwingUtilities.invokeLater(() -> {
		                	        JOptionPane.showMessageDialog(tic.getFrame(), winnerText);
		                	        if (isServer() && winnerText.equalsIgnoreCase("Player 1 Wins!"))
		                	        {
		                	        	tic.getTextfield().setText("NICE WIN Player 1 !!!");
		                	        }else  if (!isServer() && winnerText.equalsIgnoreCase("Player 2 Wins!"))
		                	        {
		                	        	tic.getTextfield().setText("NICE WIN Player 2 !!!");
		                	        }else if (isServer() && winnerText.equalsIgnoreCase("It's a Draw!"))
		                	        {
		                	        	tic.getTextfield().setText("TIE !!!");
		                	        }else if(!isServer() && winnerText.equalsIgnoreCase("It's a Draw!"))
		                	        {
		                	        	tic.getTextfield().setText("TIE !!!");
		                	        }else
		                	        {
		                	        	tic.getTextfield().setText("Game Over");
		                	        }
		                	        
		                	        for (JButton button : tic.getButtons()) {
		                	            button.setEnabled(false);
		                	        }
		                	    });
		                	}

	             } catch (SocketException e) {
	                 SwingUtilities.invokeLater(() -> {
	                     JOptionPane.showMessageDialog(null, "The connection to the other player was lost. Please check your network connection and try reconnecting.", "Connection Lost", JOptionPane.ERROR_MESSAGE);
	                 });
	                 break; 
	             } catch (EOFException e) {
	                 SwingUtilities.invokeLater(() -> {
	                     JOptionPane.showMessageDialog(null, "The game was disconnected. This might be due to network issues or the other player leaving the game.", "Disconnected", JOptionPane.ERROR_MESSAGE);
	                 });
	                 break; 
	             } catch (IOException | ClassNotFoundException e) {
	                 SwingUtilities.invokeLater(() -> {
	                     JOptionPane.showMessageDialog(null, "An unexpected error occurred. Please try restarting the game.", "Error", JOptionPane.ERROR_MESSAGE);
	                 });
	                 break; 
	             }
	         }
	     }
	 }

public void openFirewallPort() {
	int port = tic.getPort();
    String command = "netsh advfirewall firewall add rule name=\"TicTacToePort\" dir=in action=allow protocol=TCP localport=" + port;
    executeCommand(command, "Firewall port " + port + " opened successfully.", "Failed to open port on firewall.");
}


public void closeFirewallPort() {
	int port = tic.getPort(); 
    String command = "netsh advfirewall firewall delete rule name=\"TicTacToePort\" protocol=TCP localport=" + port;
    executeCommand(command, "Firewall port " + port + " closed successfully.", "Failed to close port on firewall.");
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
//abc

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

public void sendDoublePointsMatch(int matchNumber) {
    try {
        oos.writeObject("DOUBLE_POINTS_MATCH:" + matchNumber);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void sendNumberOfMatches(int numberOfMatches) {
 try {
     oos.writeObject("NUMBER_OF_MATCHES:" + numberOfMatches);
 } catch (IOException e) {
     e.printStackTrace();
 }
}










}
