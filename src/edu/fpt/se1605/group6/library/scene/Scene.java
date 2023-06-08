package edu.fpt.se1605.group6.library.scene;

import edu.fpt.se1605.group6.library.animation.Animatable;
import edu.fpt.se1605.group6.library.animation.Animation;
import edu.fpt.se1605.group6.library.animation.SimpleAnimation;

import javax.swing.*;

public abstract class Scene extends JPanel implements Animatable {

    private static final long serialVersionUID = 8238819206962468690L;

    private final transient Animation animation;
    private transient SceneManager sceneManager;

    protected Scene(float frameRate) {
        this.animation = new SimpleAnimation(this, frameRate);
    }

    public void setFrameRate(float frameRate) {
        this.animation.setFrameRate(frameRate);
    }

    public float getFrameRate() {
        return this.animation.getFrameRate();
    }

    @Override
    public boolean isRunning() {
        return this.animation.isRunning();
    }

    @Override
    public void setRunning(boolean running) {
        this.animation.setRunning(running);
    }

    public void start() {
        this.animation.start();
    }

    public void stop() {
        this.animation.stop();
    }

    @Override
    public Thread getThread() {
        return this.animation.getThread();
    }

    @Override
    public void setPause(boolean pause) {
        this.animation.setPause(pause);
    }

    @Override
    public boolean isPause() {
        return this.animation.isPause();
    }

    public void draw() {
        repaint();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public SceneManager getSceneManager() {
        return this.sceneManager;
    }
}
