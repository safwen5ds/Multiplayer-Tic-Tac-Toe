
package Programmation_concurrente_distribuée;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.SwingUtilities;

public class Listener implements Runnable {
    private ObjectInputStream ois;
    private Game game; 

    public Listener(ObjectInputStream ois, Game game) {
        this.ois = ois;
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = (String) ois.readObject();
                if (message.startsWith("MOVE:")) {
                    String[] parts = message.split(":");
                    int position = Integer.parseInt(parts[1]);
                    SwingUtilities.invokeLater(() -> game.processOpponentMove(position));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

