package Programmation_concurrente_distribuée;

import javax.swing.*;
import java.awt.*;

public class TranslucentPanel extends JPanel {
    public TranslucentPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(255, 255, 255, 128)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
