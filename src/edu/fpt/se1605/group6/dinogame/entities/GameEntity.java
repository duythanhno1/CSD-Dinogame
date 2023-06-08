package edu.fpt.se1605.group6.dinogame.entities;

import java.awt.*;

public abstract class GameEntity {

    protected Rectangle bounds;

    protected GameEntity() {
        this.bounds = new Rectangle();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getX() {
        return (int) bounds.getX();
    }

    public void setX(int x) {
        bounds.setLocation(x, getY());
    }

    public int getY() {
        return (int) bounds.getY();
    }

    public void setY(int y) {
        bounds.setLocation(getX(), y);
    }

    public int getWidth() {
        return (int) bounds.getWidth();
    }

    public int getHeight() {
        return (int) bounds.getHeight();
    }

    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }

    public void setLocation(int x, int y) {
        bounds.setLocation(x, y);
    }

    public void setBounds(int x, int y, int width, int height) {
        bounds.setBounds(x, y, width, height);
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public abstract void draw(Graphics g);

    public void update() {

    }

}
