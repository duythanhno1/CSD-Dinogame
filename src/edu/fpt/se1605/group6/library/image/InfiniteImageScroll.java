package edu.fpt.se1605.group6.library.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InfiniteImageScroll {

    private final BufferedImage image;
    protected int offset;
    protected int direction;
    protected int speed;
    protected int previousSpeed;

    public InfiniteImageScroll(BufferedImage image, int offset, int direction, int speed) {
        this.image = image;
        this.offset = offset;
        this.direction = direction;
        this.speed = 0;
        this.previousSpeed = speed;
    }

    public void pause() {
        previousSpeed = speed;
        speed = 0;
    }

    public void resume() {
        speed = previousSpeed;
    }

    public void update() {
        offset += direction * speed;
        if (offset > image.getWidth()) {
            offset = 0;
        } else if (offset < 0) {
            offset = image.getWidth();
        }
    }

    public void draw(Graphics2D g2d, int x, int y) {
        int offsetX = offset;
        g2d.drawImage(image, x + offsetX, y, null);
        g2d.drawImage(image, x + offsetX - image.getWidth(), y, null);
    }


    public int getHeight() {
        return image.getHeight();
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }
}
