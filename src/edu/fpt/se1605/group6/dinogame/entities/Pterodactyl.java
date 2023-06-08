package edu.fpt.se1605.group6.dinogame.entities;

import edu.fpt.se1605.group6.library.image.AnimatedImage;

import java.awt.*;

public class Pterodactyl extends GameEntity {

    private final AnimatedImage image;

    public Pterodactyl(Image[] frames) {
        this.image = new AnimatedImage(frames, 12);
        Image sprite = frames[0];
        setSize(sprite.getWidth(null), sprite.getHeight(null));
    }

    public void setFrameRate(float frameRate) {
        image.setFrameRate(frameRate);
    }

    public void start() {
        image.start();
    }

    public void stop() {
        image.stop();
    }

    @Override
    public void draw(Graphics g) {
        AnimatedImage image = this.image;
        Image sprite = image.getDisplayImage();
        g.drawImage(sprite, getX(), getY(), getWidth(), getHeight(), null);
    }
}
