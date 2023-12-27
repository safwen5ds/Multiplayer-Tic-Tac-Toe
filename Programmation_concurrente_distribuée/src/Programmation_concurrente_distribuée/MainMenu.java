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
import java.net.URISyntaxException;
import java.net.URL;

public class MainMenu extends JFrame {
	private static final long serialVersionUID = 1L;
    private Clip clip;
    private boolean isMusicPlaying = true;
	
	/**
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 * @throws URISyntaxException 
	 */
	
	@SuppressWarnings("deprecation")
	public MainMenu() throws UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException {
		super("Demonstrating BoxLayout");

        if (!CheckAndInstallFont.isFontInstalled("FontsFree-Net-Bookerly")) {
        	CheckAndInstallFont.installFont("/Images/FontsFree-Net-Bookerly.ttf");
        }
		
		BufferedImage cursorImg = ImageIO.read(getClass().getResource("/Images/rifle_cursor.png"));
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0,0), "custom cursor"));
		
		Color c = new Color(251, 251, 127);
		Color c2 = new Color(40, 39, 38);
		

		setLayout(new BorderLayout());
		

		BufferedImage backgroundImg = ImageIO.read(getClass().getResource("/Images/menu_background.png"));
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImg));
        setContentPane(backgroundLabel);

		Box left = Box.createVerticalBox();
		
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
		option.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        showOptionsWindow(); 
		    }
		});
		about.setFocusPainted(false);
		about.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		about.setForeground(new Color(57,57,57));
		about.setFont(new Font("Bookerly", Font.BOLD, 20));
		about.setBackground(c2);
		about.setOpaque(false);
		about.setContentAreaFilled(false);
		about.setBorderPainted(false);
		about.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        showAboutWindow(); 
		    }
		});
		
		exit.setFocusPainted(false);
		exit.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(97, 93, 92)));
		exit.setForeground(new Color(57,57,57));
		exit.setFont(new Font("Bookerly", Font.BOLD, 20));
		exit.setBackground(c2);
		exit.setOpaque(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);

	    AudioInputStream stream;
	    AudioFormat format;
	    DataLine.Info info;

		
	    URL backgroundMusicURL = getClass().getResource("/Images/videoplayback.wav");
	    stream = AudioSystem.getAudioInputStream(backgroundMusicURL);
	    format = stream.getFormat();
	    info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(stream);
	    
	    clip.loop(Clip.LOOP_CONTINUOUSLY);

		
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
				clip.stop(); 
			}
		};

	    exit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
				clip.stop();  
            }
        });
	    
		secondPanel.add(Box.createVerticalStrut(1));
		JLabel label = new JLabel("<html><center>TIC-TAC-TOE<br>GAME</center></html>", SwingConstants.CENTER);
	    label.setForeground(Color.black);
	    label.setFont(new Font("Bookerly", Font.BOLD, 20));
	    secondPanel.add(label);
		secondPanel.add(Box.createVerticalStrut(1));
		newGame.addMouseListener(ml);
		exit.addMouseListener(ml);
		about.addMouseListener(ml);
		option.addMouseListener(ml);
		
		secondPanel.add(newGame);
		secondPanel.add(about);
		secondPanel.add(option);
		secondPanel.add(exit);
		
		left.add(secondPanel);
		Box top = Box.createHorizontalBox();
		top.add(left);
		
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(top, BorderLayout.WEST);

        setSize(backgroundImg.getWidth(), backgroundImg.getHeight());
		

        setUndecorated(true);
        setLocationRelativeTo(null); 
        setVisible(true);

		show();
	}
	private void showAboutWindow() {
	    JDialog aboutDialog = new JDialog(this, "About", true);
	    aboutDialog.setLayout(new BorderLayout());
	    aboutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    String aboutText = "<html><center>Project Developed By<br><br>" +
	                       "▨ Gharbi Safwen<br>" +
	                       "▨ Melki Mohamed<br>" +
	                       "▨ Chayoukhi Malek<br><br>" +
	                       "Faculté des Sciences de Bizerte CI1 © 2023</center></html>";
	    JLabel aboutLabel = new JLabel(aboutText, SwingConstants.CENTER);
	    aboutLabel.setFont(new Font("Bookerly", Font.PLAIN, 16)); 
	    aboutDialog.add(aboutLabel, BorderLayout.CENTER);


	    aboutDialog.setSize(640, 640); 
	    aboutDialog.setLocationRelativeTo(this); 

	    aboutDialog.setVisible(true);
	}
	private void showOptionsWindow() {
	    JDialog optionsDialog = new JDialog(this, "Options", true);
	    optionsDialog.setLayout(new FlowLayout());
	    optionsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    JToggleButton musicToggleButton = new JToggleButton("Music: " + (isMusicPlaying ? "ON" : "OFF"), isMusicPlaying);
	    musicToggleButton.addItemListener(new ItemListener() {
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	            if (e.getStateChange() == ItemEvent.SELECTED) {
	                musicToggleButton.setText("Music: ON");
	                isMusicPlaying = true;
	                clip.start();
	            } else {
	                musicToggleButton.setText("Music: OFF");
	                isMusicPlaying = false;
	                clip.stop();
	            }
	        }
	    });

	    optionsDialog.add(musicToggleButton);

	    optionsDialog.setSize(200, 100);
	    optionsDialog.setLocationRelativeTo(this);

	    optionsDialog.setVisible(true);
	}

	public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException {
		MainMenu app = new MainMenu();

		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
}