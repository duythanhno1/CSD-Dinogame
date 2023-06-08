package edu.fpt.se1605.group6.dinogame.entities;

import java.awt.*;

public class Cactus extends GameEntity {

    private final Image image;

    public Cactus(Image image) {
        this.image = image;
        setSize(image.getWidth(null), image.getHeight(null));
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, getX(), getY(), image.getWidth(null), image.getHeight(null), null);
    }
}
