package Programmation_concurrente_distribuée;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class CheckAndInstallFont {

    public static void main(String[] args) {
        // Check if the font is installed
        if (!isFontInstalled("FontsFree-Net-Bookerly")) {
            // If not, install the font from resources
            installFont("/Images/FontsFree-Net-Bookerly.ttf");
        } else {
            System.out.println("Font already installed.");
        }
    }

   public  static boolean isFontInstalled(String fontName) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String font : fonts) {
            if (fontName.equals(font)) {
                return true;
            }
        }
        return false;
    }

public static void installFont(String fontResourcePath) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            InputStream is = CheckAndInstallFont.class.getResourceAsStream(fontResourcePath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            ge.registerFont(font);
            System.out.println("Font installed successfully.");
        } catch (Exception e) {
            System.out.println("Font installation failed: " + e.getMessage());
        }
    }
}
