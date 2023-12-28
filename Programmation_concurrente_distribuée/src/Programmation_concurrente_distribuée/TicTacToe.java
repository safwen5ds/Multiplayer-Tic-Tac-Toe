package Programmation_concurrente_distribuée;


import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

public class TicTacToe implements ActionListener{
	private JFrame frame = new JFrame();
    private JPanel title_panel = new JPanel();
    private JPanel button_panel = new JPanel();
    private JLabel textfield = new JLabel();
    private JButton[] buttons = new JButton[16];
    private boolean player1_turn;
    private boolean isMyTurn;
    private JButton replayButton = new JButton("Replay");
    private String ip;
    private int port ;
    private TCP tcp = new TCP(this);
    private int player1Points = 0;
    private int player2Points = 0;
    private int currentMatch = 0;
    private int numberOfMatches ;
    private JLabel pointsLabel = new JLabel();
    private JPanel controlPanel = new JPanel();
    private JFrame pointsFrame = new JFrame();
	private int doublePointsMatch;


 
	private boolean Not_valid_port(int port)
    {
    	return port < 1 || port > 65535;
    }
    private boolean valid_ip(String ip)
    {
    	String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(ip);
    	if (ip.equalsIgnoreCase("localhost"))
    	{
    		return true;
    	}
    	return matcher.matches();
    }
    TicTacToe(MainMenu mainMenu) {
    	 frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	 frame.addWindowListener(new WindowAdapter() {
    		    @Override
    		    public void windowClosing(WindowEvent e) {
    		        tcp.closeFirewallPort();
    		        tcp.closeNetworkConnection();
    		        frame.dispose();
    		        pointsFrame.dispose();
    		        mainMenu.setVisible(true); 
    		    }
    		});
         SwingUtilities.invokeLater(() -> mainMenu.setVisible(false));

    	 ip = JOptionPane.showInputDialog(frame, "Enter IP Address:", "localhost");
    	 if (ip == null) {
    		SwingUtilities.invokeLater(() -> mainMenu.setVisible(true));
    	    return; 
    	 }

    	 try {
    	     port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Port:", "22222"));
    	 } catch (NumberFormatException e) {
    		 SwingUtilities.invokeLater(() -> mainMenu.setVisible(true));
    		 return;
    	 }

    	 while (!valid_ip(ip)) {
    	     ip = JOptionPane.showInputDialog(frame, "Enter IP Address:", "localhost");
    	     if (ip == null) {    	 
    	    	 SwingUtilities.invokeLater(() -> mainMenu.setVisible(true));
    	    	 return;
    	     }
    	 }

    	 while (Not_valid_port(port)) {
    	     try {
    	         port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Invalid port. Enter Port:", "22222"));
    	     } catch (NumberFormatException e) {
    	    	 SwingUtilities.invokeLater(() -> mainMenu.setVisible(true));
    	    	 return;
    	     }
    	 }

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
        pointsLabel.setHorizontalAlignment(JLabel.CENTER);
        pointsLabel.setFont(new Font("Ink Free", Font.BOLD, 20));
        title_panel.add(pointsLabel, BorderLayout.CENTER);
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(replayButton);
        replayButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        title_panel.add(controlPanel, BorderLayout.SOUTH);
        controlPanel.add(replayButton);
        title_panel.add(controlPanel, BorderLayout.SOUTH);
        button_panel.setLayout(new GridLayout(4, 4));
        button_panel.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < 16; i++) {
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
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	tcp.closeFirewallPort();
 		        tcp.closeNetworkConnection();
 		        frame.dispose();
 		        pointsFrame.dispose();
 		        mainMenu.setVisible(true); 
            }
        });



        title_panel.add(replayButton, BorderLayout.SOUTH);

       tcp.starting();
       if (tcp.isServer())
       {
    	   String matchesInput = JOptionPane.showInputDialog(frame, "Enter Number of Matches:", "5");
    	   if (matchesInput == null) {  
    		 tcp.closeFirewallPort();
		     tcp.closeNetworkConnection();
		     frame.dispose();
		     pointsFrame.dispose();
  	    	 SwingUtilities.invokeLater(() -> mainMenu.setVisible(true));
  	    	 return;
  	     }
           numberOfMatches = Integer.parseInt(matchesInput);
           tcp.sendNumberOfMatches(numberOfMatches );
       }
         setRandomDoublePointsMatch();
       
        updateTextfield();
        frame.validate();
        frame.repaint();
        setupPointsFrame();
    }
    private void setupPointsFrame() {
        pointsFrame.setSize(200, 100);
        pointsFrame.setLayout(new BorderLayout());
        pointsFrame.add(pointsLabel, BorderLayout.CENTER);
        pointsFrame.setVisible(true);
    }


   

    public void actionPerformed(ActionEvent e) {
        if (!isMyTurn) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            if (e.getSource() == buttons[i] && buttons[i].getText().equals("")) {
                buttons[i].setForeground(tcp.isServer() ? Color.RED : Color.BLUE);
                buttons[i].setText(tcp.isServer() ? "X" : "O"); 
                tcp.sendMove(i);
                isMyTurn = false;
                player1_turn = !player1_turn;
                updateTextfield();
                check();
                break;
            }
        }
    }

    public void updateTextfield() {
        if (isMyTurn) {
            textfield.setText(tcp.isServer() ? "Your turn (X)" : "Your turn (O)");
        } else {
            textfield.setText(tcp.isServer() ? "Opponent's turn (O)" : "Opponent's turn (X)");
        }
        
    }

    public void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(new JButton().getBackground());
        }
        player1_turn = true; 
        isMyTurn = tcp.isServer(); 
        updateTextfield();
    }
    




 public void check() {

	for (int i = 0;i<16;i+=4)
	{
		if (buttons[i].getText().equals("X") &&
			  buttons[i].getText().equals(buttons[i+1].getText()) &&
			  buttons[i].getText().equals(buttons[i+2].getText()) &&
			  buttons[i].getText().equals(buttons[i+3].getText()))
		{
			xWins(i, i+1, i+2, i+3);
		}
		
		if (buttons[i].getText().equals("O") &&
				  buttons[i].getText().equals(buttons[i+1].getText()) &&
				  buttons[i].getText().equals(buttons[i+2].getText()) &&
				  buttons[i].getText().equals(buttons[i+3].getText()))
			{
				oWins(i, i+1, i+2, i+3);
			}
	}

		for (int i = 0;i<4;i++)
		{
			if (buttons[i].getText().equals("X") &&
				  buttons[i].getText().equals(buttons[i+4].getText()) &&
				  buttons[i].getText().equals(buttons[i+8].getText()) &&
				  buttons[i].getText().equals(buttons[i+12].getText()))
			{
				xWins(i, i+4, i+8, i+12);
			}
			
			if (buttons[i].getText().equals("O") &&
					  buttons[i].getText().equals(buttons[i+4].getText()) &&
					  buttons[i].getText().equals(buttons[i+8].getText()) &&
					  buttons[i].getText().equals(buttons[i+12].getText()))
				{
					oWins(i, i+4, i+8, i+12);
				}
		
	}
		if (buttons[0].getText().equals("X") &&
				  buttons[0].getText().equals(buttons[5].getText()) &&
				  buttons[0].getText().equals(buttons[10].getText()) &&
				  buttons[0].getText().equals(buttons[15].getText()))
			{
				xWins(0, 5, 10, 15);

			}
			
			if (buttons[0].getText().equals("O") &&
					  buttons[0].getText().equals(buttons[5].getText()) &&
					  buttons[0].getText().equals(buttons[10].getText()) &&
					  buttons[0].getText().equals(buttons[15].getText()))
				{
					oWins(0, 5, 10, 15);

				}
			if (buttons[3].getText().equals("X") &&
					  buttons[3].getText().equals(buttons[6].getText()) &&
					  buttons[3].getText().equals(buttons[9].getText()) &&
					  buttons[3].getText().equals(buttons[12].getText()))
				{
					xWins(3, 6, 9, 12);

				}
				
				if (buttons[3].getText().equals("O") &&
						  buttons[3].getText().equals(buttons[6].getText()) &&
						  buttons[3].getText().equals(buttons[9].getText()) &&
						  buttons[3].getText().equals(buttons[12].getText()))
					{
						oWins(3, 6, 9, 12);

					}
		
	
        if(areAllButtonsUsed(buttons))
        {
	    	draw();

        }

	}

 public void checkForEndOfGame() {
	 System.out.println("checkForEndOfGame : "+currentMatch +" | "+numberOfMatches);
	    if (currentMatch == numberOfMatches) {
	        declareWinner();
	   
	       
	        
	    } 
	}


public void xWins(int a, int b, int c, int d) {
  highlightWinningButtons(a, b, c, d);
  textfield.setText("X wins");
  if (tcp.isServer())
  {
	  displayGifOverlay("/Images/win.gif");
  }else
  {
	  displayGifOverlay("/Images/lose.gif");
  }

	  if (currentMatch!=doublePointsMatch)
	  {
		  player1Points += 1500;
	      player2Points += 0;
	  }else
	  {
		  player1Points += 3000;
	      player2Points += 0;
	  }
		  
	  
	  System.out.println("player 1 : "+player1Points+" | Player 2 : "+player2Points);
     
  



  System.out.println(" the incrementation worked !!! current match is "+currentMatch);
  currentMatch++;
  handleMatchCompletion();
  updatePointsDisplay();
}

public void oWins(int a, int b, int c, int d) {
  highlightWinningButtons(a, b, c, d);
  textfield.setText("O wins");
  if (tcp.isServer())
  {
	  displayGifOverlay("/Images/lose.gif");
  }else
  {
	  displayGifOverlay("/Images/win.gif");
  }


	  if (currentMatch!=doublePointsMatch)
	  {
		  player1Points += 0;
	      player2Points += 1500;
	  }else
	  {
		  player1Points += 0;
	      player2Points += 3000;
	  }
  
	  System.out.println("player 1 : "+player1Points+" | Player 2 : "+player2Points);

	  currentMatch++;
	  handleMatchCompletion();
	  updatePointsDisplay();

}

public void draw() {
  for (int i = 0; i < 16; i++) {
      buttons[i].setBackground(Color.GRAY);
      buttons[i].setEnabled(false);
  }
  textfield.setText("Draw!");

  if (currentMatch!=doublePointsMatch)
  {
	  player1Points += 500;
	  player2Points += 500;
  }else
  {
	  player1Points += 1000;
	  player2Points += 1000;
  }
  System.out.println("player 1 : "+player1Points+" | Player 2 : "+player2Points);

  currentMatch++;
  handleMatchCompletion();
  updatePointsDisplay();

}


private void handleMatchCompletion() {
    checkForEndOfGame();
    System.out.println("handleMatchCompletion : " + currentMatch + " | " + numberOfMatches);
    tcp.sendMessage("MATCH_NUMBER:" + currentMatch);
    if (currentMatch < numberOfMatches) {
    	int nextMatchNumber = currentMatch + 1;
    	String nextMatchMessage = "<html><body style='text-align: center;'>" +
                "<strong>Get Ready! 🔥 Match " + nextMatchNumber + " is up next! 🔥</strong>" +
                "</body></html>";
        tcp.sendMessage("NEXT_MATCH:" + nextMatchMessage);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            public void run() {
                resetGame();
            }
        };

        scheduler.schedule(task, 5, TimeUnit.SECONDS); 
    } 
}


private void highlightWinningButtons(int a, int b, int c, int d) {
  buttons[a].setBackground(Color.GREEN);
  buttons[b].setBackground(Color.GREEN);
  buttons[c].setBackground(Color.GREEN);
  buttons[d].setBackground(Color.GREEN);

  for (int i = 0; i < 16; i++) {
      buttons[i].setEnabled(false);
  }
}


 
 public boolean areAllButtonsUsed(JButton[] buttons) {
	    for (int i = 0; i < buttons.length; i++) {
	        if (buttons[i].getText().equals("")) {
	            return false;
	        }
	    }
	    return true;
	}

 public JFrame getFrame() {
	return frame;
}

public void setFrame(JFrame frame) {
	this.frame = frame;
}

public JPanel getTitle_panel() {
	return title_panel;
}

public void setTitle_panel(JPanel title_panel) {
	this.title_panel = title_panel;
}

public JPanel getButton_panel() {
	return button_panel;
}

public void setButton_panel(JPanel button_panel) {
	this.button_panel = button_panel;
}

public JLabel getTextfield() {
	return textfield;
}

public void setTextfield(JLabel textfield) {
	this.textfield = textfield;
}

public JButton[] getButtons() {
	return buttons;
}

public void setButtons(JButton[] buttons) {
	this.buttons = buttons;
}

public boolean isPlayer1_turn() {
	return player1_turn;
}

public void setPlayer1_turn(boolean player1_turn) {
	this.player1_turn = player1_turn;
}

public boolean isMyTurn() {
	return isMyTurn;
}

public void setMyTurn(boolean isMyTurn) {
	this.isMyTurn = isMyTurn;
}

public JButton getReplayButton() {
	return replayButton;
}

public void setReplayButton(JButton replayButton) {
	this.replayButton = replayButton;
}

public String getIp() {
	return ip;
}

public void setIp(String ip) {
	this.ip = ip;
}

public int getPort() {
	return port;
}

public void setPort(int port) {
	this.port = port;
}


public TCP getTcp() {
	return tcp;
}
public void setTcp(TCP tcp) {
	this.tcp = tcp;
}
public int getPlayer1Points() {
	return player1Points;
}
public void setPlayer1Points(int player1Points) {
	this.player1Points = player1Points;
}
public int getPlayer2Points() {
	return player2Points;
}
public void setPlayer2Points(int player2Points) {
	this.player2Points = player2Points;
}
public int getCurrentMatch() {
	return currentMatch;
}
public void setCurrentMatch(int currentMatch) {
	this.currentMatch = currentMatch;
}
public JLabel getPointsLabel() {
	return pointsLabel;
}
public void setPointsLabel(JLabel pointsLabel) {
	this.pointsLabel = pointsLabel;
}
public JPanel getControlPanel() {
	return controlPanel;
}
public void setControlPanel(JPanel controlPanel) {
	this.controlPanel = controlPanel;
}
public JFrame getPointsFrame() {
	return pointsFrame;
}
public void setPointsFrame(JFrame pointsFrame) {
	this.pointsFrame = pointsFrame;
}
public int getNumberOfMatches() {
	return numberOfMatches;
}
public int getDoublePointsMatch() {
	return doublePointsMatch;
}
public void setNumberOfMatches(int numberOfMatches) {
    this.numberOfMatches = numberOfMatches;
}
public void setRandomDoublePointsMatch() {
    if (tcp.isServer() && numberOfMatches > 2) {
        Random rand = new Random();
        this.doublePointsMatch = numberOfMatches / 2 + rand.nextInt(numberOfMatches / 2);
        tcp.sendDoublePointsMatch(this.doublePointsMatch);
    }
}

public void declareWinner() {

		 String winnerText = player1Points > player2Points ? "Player 1 Wins!" : "Player 2 Wins!";
		    if (player1Points == player2Points) {
		        winnerText = "It's a Draw!";
		    }

		    if (currentMatch == numberOfMatches) {
		        tcp.sendMessage("GAME_OUTCOME:" + winnerText) ;
		        
		    }

		    }

   






private void updatePointsDisplay() {
	System.out.println("update Points worked !! ");
    pointsLabel.setText("Player 1 Points: " + player1Points + ", Player 2 Points: " + player2Points);
    pointsFrame.repaint();
    if (currentMatch == doublePointsMatch)
    {
    	tcp.sendMessage("DOUBLE_START");
    	System.out.println("Sending Double_Start is done with current match: "+currentMatch+" double match: "+doublePointsMatch);
    }
}
public void setDoublePointsMatch(int matchNumber) {
    this.doublePointsMatch = matchNumber;
}




public void setmatchnumber(int matchNumber) {
	this.currentMatch = matchNumber;
	
}
private void displayGifOverlay(String gifResourcePath) {
	URL gifUrl = getClass().getResource(gifResourcePath);
	if (gifUrl == null) {
        System.out.println("Resource not found: " + gifResourcePath);
        return;
    }
    ImageIcon gifIcon = new ImageIcon(gifUrl);
    JLabel gifLabel = new JLabel(gifIcon);
    gifLabel.setSize(button_panel.getSize());
    button_panel.setLayout(null);
    button_panel.add(gifLabel);
    gifLabel.setLocation(0, 200);
    gifLabel.setVisible(true);
    button_panel.setComponentZOrder(gifLabel, 0);
    frame.repaint(); 
    Timer timer = new Timer(5000, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            button_panel.remove(gifLabel);
            button_panel.revalidate();
            button_panel.repaint();
            button_panel.setLayout(new GridLayout(4, 4)); 
        }
    });
    timer.setRepeats(false);
    timer.start();
}


}


