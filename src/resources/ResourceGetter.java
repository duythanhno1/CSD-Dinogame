package resources;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

public class ResourceGetter {
    private ResourceGetter(){
        // private constructor
    }

    public static URL getResource(String path) {
        return ResourceGetter.class.getClassLoader().getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return ResourceGetter.class.getClassLoader().getResourceAsStream(path);
    }

    public static Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(getResource(path));
    }

    public static Image getPreloadedImage(String path) {
        return new ImageIcon(getResource(path)).getImage();
    }

    public static BufferedImage getBufferedImage(String path) {
        Image image = getPreloadedImage(path);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }
}
