package Programmation_concurrente_distribuée;


import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;

public class TicTacToe implements ActionListener{
	private JFrame frame = new JFrame();
    private JPanel title_panel = new JPanel();
    private JPanel button_panel = new JPanel();
    private JLabel textfield = new JLabel();
    private JButton[] buttons = new JButton[9];
    private boolean player1_turn;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServerSocket serverSocket;
    private boolean isMyTurn;
    private boolean isServer;
    private JButton replayButton = new JButton("Replay");
 
    TicTacToe() {
        String ip = JOptionPane.showInputDialog(frame, "Enter IP Address:", "localhost");
        int port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Port:", "22222"));
        while (port < 1 || port > 65535) {
            port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Invalid port. Enter Port:", "22222"));
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(25, 255, 0));
        textfield.setFont(new Font("Ink Free", Font.BOLD, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Tic-Tac-Toe");
        textfield.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, 800, 100);

        button_panel.setLayout(new GridLayout(3, 3));
        button_panel.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }

        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel);
        replayButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        replayButton.setBackground(Color.BLACK);
        replayButton.setForeground(Color.WHITE);
        replayButton.addActionListener(e -> resetGame());
        title_panel.add(replayButton, BorderLayout.SOUTH);

        if (shouldStartServer()) {
            initializeServer(port);
            player1_turn = true;
            isMyTurn = true;
            isServer = true; 
        } else {
            if (!connectToServer(ip, port)) {
                System.out.println("Failed to connect to the server.");
                return;
            }
            player1_turn = false;
            isMyTurn = false;
            isServer = false; 
        }
        updateTextfield();
    }

    public void actionPerformed(ActionEvent e) {
        if (!isMyTurn) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i] && buttons[i].getText().equals("")) {
                buttons[i].setForeground(isServer ? Color.RED : Color.BLUE);
                buttons[i].setText(isServer ? "X" : "O"); 
                sendMove(i);
                isMyTurn = false;
                player1_turn = !player1_turn;
                updateTextfield();
                check();
                break;
            }
        }
    }

    private void updateTextfield() {
        if (isMyTurn) {
            textfield.setText(isServer ? "Your turn (X)" : "Your turn (O)");
        } else {
            textfield.setText(isServer ? "Opponent's turn (O)" : "Opponent's turn (X)");
        }
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(new JButton().getBackground());
        }
        player1_turn = true; 
        isMyTurn = isServer; 
        updateTextfield();
    }
 public void check() {

		if((buttons[0].getText()=="X") &&
		   (buttons[1].getText()=="X") &&
		   (buttons[2].getText()=="X")) {
		    xWins(0,1,2);
		}
		if((buttons[3].getText()=="X") &&
		   (buttons[4].getText()=="X") &&
		   (buttons[5].getText()=="X")) {
			xWins(3,4,5);
		}
		if((buttons[6].getText()=="X") &&
		   (buttons[7].getText()=="X") &&
		   (buttons[8].getText()=="X")) {
		    xWins(6,7,8);
		}
		if((buttons[0].getText()=="X") &&
		   (buttons[3].getText()=="X") &&
		   (buttons[6].getText()=="X")) {
		    xWins(0,3,6);
		}
		if((buttons[1].getText()=="X") &&
		   (buttons[4].getText()=="X") &&
		   (buttons[7].getText()=="X")) {
		    xWins(1,4,7);
		}
		if((buttons[2].getText()=="X") &&
		   (buttons[5].getText()=="X") &&
		   (buttons[8].getText()=="X")) {
		    xWins(2,5,8);
		}
		if((buttons[0].getText()=="X") &&
		   (buttons[4].getText()=="X") &&
		   (buttons[8].getText()=="X")) {
		    xWins(0,4,8);
		}
		if((buttons[2].getText()=="X") &&
		   (buttons[4].getText()=="X") &&
		   (buttons[6].getText()=="X")) {
		    xWins(2,4,6);
		}

		if((buttons[0].getText()=="O") &&
		   (buttons[1].getText()=="O") &&
		   (buttons[2].getText()=="O")) {
		    oWins(0,1,2);
		}
		if((buttons[3].getText()=="O") &&
		   (buttons[4].getText()=="O") &&
		   (buttons[5].getText()=="O")) {
		    oWins(3,4,5);
		}
		if((buttons[6].getText()=="O") &&
		   (buttons[7].getText()=="O") &&
		   (buttons[8].getText()=="O")) {
		    oWins(6,7,8);
		}
		if((buttons[0].getText()=="O") &&
		   (buttons[3].getText()=="O") &&
		   (buttons[6].getText()=="O")) {
		    oWins(0,3,6);
		}
		if((buttons[1].getText()=="O") &&
		   (buttons[4].getText()=="O") &&
		   (buttons[7].getText()=="O")) {
		    oWins(1,4,7);
		}
		if((buttons[2].getText()=="O") &&
		   (buttons[5].getText()=="O") &&
		   (buttons[8].getText()=="O")) {
		    oWins(2,5,8);
		}
		if((buttons[0].getText()=="O") &&
		   (buttons[4].getText()=="O") &&
		   (buttons[8].getText()=="O")) {
		    oWins(0,4,8);
		}
		if((buttons[2].getText()=="O") &&
		   (buttons[4].getText()=="O") &&
		   (buttons[6].getText()=="O")) {
		    oWins(2,4,6);
		}
        if(areAllButtonsUsed(buttons))
        {
	    	draw();
        }
	}

 
 public void xWins(int a,int b,int c) {
 buttons[a].setBackground(Color.GREEN);
 buttons[b].setBackground(Color.GREEN);
 buttons[c].setBackground(Color.GREEN);
 
 for(int i=0;i<9;i++) {
	buttons[i].setEnabled(false);
 }
 textfield.setText("X wins");
 }
 public void oWins(int a,int b,int c) {
	buttons[a].setBackground(Color.GREEN);
	buttons[b].setBackground(Color.GREEN);
	buttons[c].setBackground(Color.GREEN);
	
	for(int i=0;i<9;i++) {
	    buttons[i].setEnabled(false);
	}
	textfield.setText("O wins");
 }
 
 public void draw()
 {
	 for(int i=0;i<9;i++) {
		    buttons[i].setBackground(Color.GRAY);
		    buttons[i].setEnabled(false);
	  }
	 textfield.setText("DRAAAW !");
 }
 
 public boolean areAllButtonsUsed(JButton[] buttons) {
	    for (int i = 0; i < buttons.length; i++) {
	        if (buttons[i].getText().equals("")) {
	            return false;
	        }
	    }
	    return true;
	}

 private void initializeServer(int port) {
     try {
         serverSocket = new ServerSocket(port);
         System.out.println("Server started. Waiting for a client ...");
         socket = serverSocket.accept();
         setupStreams();
     } catch (IOException e) {
         e.printStackTrace();
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
         new Thread(new Listener()).start();
     } catch (IOException e) {
         e.printStackTrace();
     }
 }
 private class Listener implements Runnable {
	 public void run() {
         while (true) {
             try {
                 String message = (String) ois.readObject();
	                if (message.startsWith("MOVE:")) {
	                    String[] parts = message.split(":");
	                    int position = Integer.parseInt(parts[1]);
	                    SwingUtilities.invokeLater(() -> {
	                        if (buttons[position].getText().equals("")) {
	                            buttons[position].setText(isServer ? "O" : "X"); 
	                            player1_turn = !player1_turn;
	                            isMyTurn = true;
	                            updateTextfield();
	                            check();
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


 private void sendMove(int move) {
     try {
         oos.writeObject("MOVE:" + move + ":" + (isServer ? "X" : "O"));
     } catch (IOException e) {
         e.printStackTrace();
     }
 }




 private boolean shouldStartServer() {
     int response = JOptionPane.showConfirmDialog(null, "Do you want to start the server?", "Server or Client", JOptionPane.YES_NO_OPTION);
     return response == JOptionPane.YES_OPTION;
 }



 public static void main(String[] args) {
	@SuppressWarnings("unused")
	TicTacToe tictactoe = new TicTacToe();
 }
 
}

