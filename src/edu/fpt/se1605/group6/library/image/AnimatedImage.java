package edu.fpt.se1605.group6.library.image;

import edu.fpt.se1605.group6.library.animation.Animatable;
import edu.fpt.se1605.group6.library.animation.Animation;
import edu.fpt.se1605.group6.library.animation.SimpleAnimation;

import java.awt.*;

public class AnimatedImage implements Animatable {

    private Image[] frames;
    private int frame;
    private final Animation animation;
    private Image displayImage;

    public AnimatedImage(Image[] frames, float frameRate) {
        this.animation = new SimpleAnimation(this, frameRate);
        this.frame = 0;
        this.frames = frames;
        this.displayImage = frames[0];
    }

    public void setFrames(Image[] frames) {
        this.frames = frames;
    }

    public Image[] getFrames() {
        return this.frames;
    }

    public Image getFrame() {
        return getFrame(this.frame);
    }

    public Image getFrame(int frame) {
        return this.frames[frame % frames.length];
    }

    public void setFrame(int frame) {
        this.frame = frame % frames.length;
    }

    @Override
    public void update() {
        setFrame(frame + 1);
    }

    @Override
    public void draw() {
        this.displayImage = getFrame();
    }

    @Override
    public void start() {
        this.animation.start();
    }

    @Override
    public void stop() {
        this.animation.stop();
    }

    @Override
    public boolean isRunning() {
        return this.animation.isRunning();
    }

    @Override
    public void setRunning(boolean running) {
        this.animation.setRunning(running);
    }

    @Override
    public void setFrameRate(float frameRate) {
        this.animation.setFrameRate(frameRate);
    }

    @Override
    public float getFrameRate() {
        return this.animation.getFrameRate();
    }

    @Override
    public void setPause(boolean pause) {
        this.animation.setPause(pause);
    }

    @Override
    public boolean isPause() {
        return this.animation.isPause();
    }

    @Override
    public Thread getThread() {
        return this.animation.getThread();
    }

    public Image getDisplayImage() {
        return this.displayImage;
    }
}
