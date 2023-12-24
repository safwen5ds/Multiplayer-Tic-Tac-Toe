package Programmation_concurrente_distribuée;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JFrame {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	
	@SuppressWarnings("deprecation")
	public MainMenu() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		super("Demostrating BoxLayout");
		
		BufferedImage cursorImg = ImageIO.read(getClass().getResource("/Images/rifle_cursor.png"));
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0,0), "custom cursor"));
		
		Color c = new Color(251, 251, 127);
		Color c2 = new Color(40, 39, 38);
		
		//Set the layout of our frame as a BorderLayout
		setLayout(new BorderLayout());
		
		//Adding the background image
		BufferedImage backgroundImg = ImageIO.read(getClass().getResource("/Images/menu_background.png"));
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImg));
        setContentPane(backgroundLabel);
		//Creating  a Box that contains everything
		Box left = Box.createVerticalBox();
		
		//Creating all buttons
		JButton newGame = new JButton("NEW GAME"), exit = new JButton("EXIT GAME"),
				option = new JButton("OPTIONS"), about = new JButton("ABOUT");
		newGame.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        new TicTacToe(MainMenu.this); 
		        MainMenu.this.setVisible(false); 
		    }
		});


		left.setPreferredSize(new Dimension(350, 350));


		TranslucentPanel secondPanel = new TranslucentPanel();
		secondPanel.setLayout(new GridLayout(0, 1, 0, 0));
		secondPanel.setPreferredSize(new Dimension(330, 380));
		secondPanel.setMaximumSize(new Dimension(330, 380));
		secondPanel.setBorder(BorderFactory.createEmptyBorder(0, 103, 40, 0));
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
		secondPanel.setBorder(blackLine);
		
		//Design setting only. I�ve left the code to each other repeated in case you want
		//want something like each button with a different color or font.
		newGame.setFocusPainted(false);
		newGame.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		newGame.setForeground(new Color(57,57,57));
		newGame.setFont(new Font("Bookerly", Font.BOLD, 20));
		newGame.setBackground(c2);
		newGame.setOpaque(false);
		newGame.setContentAreaFilled(false);
		newGame.setBorderPainted(false);

		option.setFocusPainted(false);
		option.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		option.setForeground(new Color(57,57,57));
		option.setFont(new Font("Bookerly", Font.BOLD, 20));
		option.setBackground(c2);
		option.setOpaque(false);
		option.setContentAreaFilled(false);
		option.setBorderPainted(false);

		about.setFocusPainted(false);
		about.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		about.setForeground(new Color(57,57,57));
		about.setFont(new Font("Bookerly", Font.BOLD, 20));
		about.setBackground(c2);
		about.setOpaque(false);
		about.setContentAreaFilled(false);
		about.setBorderPainted(false);
		
		exit.setFocusPainted(false);
		exit.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		exit.setForeground(new Color(57,57,57));
		exit.setFont(new Font("Bookerly", Font.BOLD, 20));
		exit.setBackground(c2);
		exit.setOpaque(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);

	    //If you want to have a background music add the path of it here and uncomment it
	    
		//Necessary variables for the audios
	    AudioInputStream stream;
	    AudioFormat format;
	    DataLine.Info info;
	    Clip clip;
		
	    File background_music = new File("/Images/videoplayback.mp3");
	    
	    stream = AudioSystem.getAudioInputStream(background_music);
	    format = stream.getFormat();
	    info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(stream);
	    
	   clip.start(); 
		
		MouseAdapter ml = new MouseAdapter() {

			public void mouseEntered(MouseEvent e) {

				AudioClip sound = null;
				try {
					sound = Applet.newAudioClip(getClass().getResource("/Images/button_hover.wav"));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				sound.play();
				((AbstractButton) e.getSource()).setBorderPainted(true);
				((JComponent) e.getSource()).setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, c));
			}

			public void mouseExited(MouseEvent e) {
				((AbstractButton) e.getSource()).setBorderPainted(false);
				((JComponent) e.getSource()).setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, Color.GRAY));
			}

			public void mouseClicked(MouseEvent e) {
				
				AudioClip sound = null;
				sound = Applet.newAudioClip(getClass().getResource("/Images/action.wav"));
				sound.play();
				// clip.stop(); --> uncomment if you have added a background music
			}
		};

	    exit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
				//clip.stop();  --> uncomment if you have added a background music
            }
        });
	    
	    //to add an extra space
		secondPanel.add(Box.createVerticalStrut(1));
		//add the title
	    JLabel label = new JLabel("<html>TIC-TAC-TOE<br>&nbsp;&nbsp;&nbsp;GAME</html>", SwingConstants.CENTER);
	    //Design settings
	    label.setForeground(Color.black);
	    label.setFont(new Font("Bookerly", Font.BOLD, 20));
	    //Add the title to the the Panel
	    secondPanel.add(label);
		secondPanel.add(Box.createVerticalStrut(1));
		//Add the mouse Listeners
		newGame.addMouseListener(ml);
		exit.addMouseListener(ml);
		about.addMouseListener(ml);
		option.addMouseListener(ml);
		
		//Add the buttons to the Panel
		secondPanel.add(newGame);
		secondPanel.add(about);
		secondPanel.add(option);
		secondPanel.add(exit);
		
		//add the panel to the box
		left.add(secondPanel);
		Box top = Box.createHorizontalBox();
		top.add(left);
		
		//get the content pane from the frame and add there the "top" box
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(top, BorderLayout.WEST);

		//set frame size
        setSize(backgroundImg.getWidth(), backgroundImg.getHeight());
		

        setUndecorated(true);
        setLocationRelativeTo(null); // center the frame
        setVisible(true);

		show();
	}

	public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		MainMenu app = new MainMenu();

		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
}