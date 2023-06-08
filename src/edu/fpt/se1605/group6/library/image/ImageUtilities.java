package edu.fpt.se1605.group6.library.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImageUtilities {

    private ImageUtilities() {
        // prevent instantiation
    }

    public static Image scaleImage(Image image, float scale, int type, ImageObserver observer) {
        return image.getScaledInstance(Math.round(image.getWidth(observer) * scale), Math.round(image.getHeight(observer) * scale), type);
    }

    public static Image scaleImage(Image image, float scale) {
        return scaleImage(image, scale, Image.SCALE_DEFAULT, null);
    }

    public static Image[] scaleImages(Image[] images, float scale) {
        Image[] scaledImages = new Image[images.length];
        for (int i = 0; i < images.length; i++) {
            scaledImages[i] = scaleImage(images[i], scale);
        }
        return scaledImages;
    }

    public static BufferedImage toBufferedImage(Image image) {
        return toBufferedImage(image, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage[] splitImageX(BufferedImage image, int n) {
        BufferedImage[] images = new BufferedImage[n];
        int width = image.getWidth(null) / n;
        int height = image.getHeight(null);
        for (int i = 0; i < n; i++) {
            images[i] = image.getSubimage(i * width, 0, width, height);
        }
        return images;
    }

    public static BufferedImage[] splitImageY(BufferedImage image, int n) {
        BufferedImage[] images = new BufferedImage[n];
        int width = image.getWidth(null);
        int height = image.getHeight(null) / n;
        for (int i = 0; i < n; i++) {
            images[i] = image.getSubimage(0, i * height, width, height);
        }
        return images;
    }

    public static BufferedImage toBufferedImage(Image image, int type) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }
}
