package edu.fpt.se1605.group6.dinogame.entities;

import edu.fpt.se1605.group6.library.image.AnimatedImage;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class Dino extends GameEntity {

    public enum State {
        IDLE,
        RUNNING,
        JUMPING,
        DUCKING,
        DEAD
    }

    private State state = State.IDLE;
    private final Map<State, AnimatedImage> animations;

    public Dino(Map<String, Image[]> spriteMap) {
        super();
        this.animations = new EnumMap<>(State.class);
        this.animations.put(State.IDLE, new AnimatedImage(spriteMap.get("idle"), 4));
        this.animations.put(State.RUNNING, new AnimatedImage(spriteMap.get("run"), 12));
        this.animations.put(State.JUMPING, new AnimatedImage(spriteMap.get("jump"), 12));
        this.animations.put(State.DUCKING, new AnimatedImage(spriteMap.get("duck"), 12));
        this.animations.put(State.DEAD, new AnimatedImage(spriteMap.get("death"), 1));
        this.animations.values().forEach(animation -> {
            animation.start();
            animation.setPause(true);
        });
    }

    public void setState(State state) {
        AnimatedImage image = getImage();
        image.setPause(true);
        this.state = state;
        image = getImage();
        Image sprite = image.getDisplayImage();
        int width = sprite.getWidth(null);
        int height = sprite.getHeight(null);
        image.setPause(false);
        setSize(width, height);
    }

    public State getState() {
        return this.state;
    }

    public AnimatedImage getImage() {
        return this.animations.get(getState());
    }

    public void draw(Graphics g) {
        AnimatedImage image = getImage();
        Image sprite = image.getDisplayImage();
        g.drawImage(sprite, getX(), getY(), sprite.getWidth(null), sprite.getHeight(null), null);
    }

    public void update() {

    }
}
