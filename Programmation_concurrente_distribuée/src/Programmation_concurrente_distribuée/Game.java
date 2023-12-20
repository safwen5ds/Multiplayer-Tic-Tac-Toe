package Programmation_concurrente_distribuée;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game implements ActionListener {
    private JFrame frame = new JFrame("Tic-Tac-Toe");
    private JPanel titlePanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JLabel textField = new JLabel();
    private JButton[] buttons = new JButton[9];
    private JButton replayButton = new JButton("Replay");
    private boolean player1Turn;
    private Network network;
    private boolean isServer;

    public Game(String ip, int port, boolean isServer) {
        this.isServer = isServer;
        initializeUI();
        network = new Network(this, isServer, port);
        network.initializeConnection(ip, port);
        player1Turn = isServer;
    }


    private void initializeUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());

        textField.setBackground(new Color(25, 25, 25));
        textField.setForeground(new Color(25, 255, 0));
        textField.setFont(new Font("Ink Free", Font.BOLD, 75));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Tic-Tac-Toe");
        textField.setOpaque(true);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0, 0, 800, 100);

        buttonPanel.setLayout(new GridLayout(4, 4)); 
        buttonPanel.setBackground(new Color(150, 150, 150));

        buttons = new JButton[16]; 
        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton();
            buttonPanel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD, 90)); 
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }

        titlePanel.add(textField);
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttonPanel);

        replayButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        replayButton.setBackground(Color.BLACK);
        replayButton.setForeground(Color.WHITE);
        replayButton.addActionListener(e -> resetGame());
        titlePanel.add(replayButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i] && buttons[i].getText().equals("") && player1Turn == isServer) {
                buttons[i].setForeground(isServer ? Color.RED : Color.BLUE);
                buttons[i].setText(isServer ? "X" : "O");
                network.sendMove(i, isServer ? "X" : "O");
                player1Turn = !player1Turn;
                updateTextField();
                checkWinCondition();
                break;
            }
        }
    }

    public void processOpponentMove(int position) {
        buttons[position].setText(isServer ? "O" : "X");
        buttons[position].setForeground(isServer ? Color.BLUE : Color.RED);
        player1Turn = !player1Turn;
        updateTextField();
        checkWinCondition();
    }

    private void updateTextField() {
        if (player1Turn == isServer) {
            textField.setText(isServer ? "Your turn (X)" : "Your turn (O)");
        } else {
            textField.setText(isServer ? "Opponent's turn (O)" : "Opponent's turn (X)");
        }
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(new JButton().getBackground());
        }
        player1Turn = isServer;
        updateTextField();
    }

    private void checkWinCondition() {
        for (int i = 0; i < 4; i++) {

            if (checkLine(buttons[i * 4], buttons[i * 4 + 1], buttons[i * 4 + 2], buttons[i * 4 + 3])) return;


            if (checkLine(buttons[i], buttons[i + 4], buttons[i + 8], buttons[i + 12])) return;
        }


        if (checkLine(buttons[0], buttons[5], buttons[10], buttons[15])) return;
        if (checkLine(buttons[3], buttons[6], buttons[9], buttons[12])) return;
    }

    private boolean checkLine(JButton b1, JButton b2, JButton b3, JButton b4) {
        if (b1.getText().isEmpty()) return false;
        
        boolean win = b1.getText().equals(b2.getText()) &&
                      b1.getText().equals(b3.getText()) &&
                      b1.getText().equals(b4.getText());
        
        if (win) {
            b1.setBackground(Color.GREEN);
            b2.setBackground(Color.GREEN);
            b3.setBackground(Color.GREEN);
            b4.setBackground(Color.GREEN);
            for (JButton button : buttons) {
                button.setEnabled(false);
            }
            textField.setText(b1.getText() + " wins");
        }
        
        return win;
    }


    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Enter IP Address:", "localhost");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Enter Port:", "22222"));
        boolean isServer = JOptionPane.showConfirmDialog(null, "Do you want to start the server?", "Server or Client", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        
        new Game(ip, port, isServer);
    }
}
