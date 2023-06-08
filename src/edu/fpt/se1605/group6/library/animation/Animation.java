package edu.fpt.se1605.group6.library.animation;

public abstract class Animation implements Animatable {

    private Thread thread;
    private boolean running;
    private boolean paused;

    private float frameRate;
    private float frameInterval;
    private long lastFrameTime;

    protected Animation(float frameRate) {
        this.frameRate = frameRate;
        this.frameInterval = 1000f / frameRate;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
        if (isRunning()) {
            start();
        } else {
            stop();
        }
    }

    @Override
    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
        this.frameInterval = 1000f / frameRate;
    }

    @Override
    public float getFrameRate() {
        return frameRate;
    }

    @Override
    public void setPause(boolean pause) {
        this.paused = pause;
    }

    @Override
    public boolean isPause() {
        return this.paused;
    }

    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        setRunning(true);
        this.thread = new Thread(() -> {
            synchronized (this) {
                while (isRunning()) {
                    long now = System.currentTimeMillis();
                    long delta = now - lastFrameTime;
                    if (delta >= frameInterval) {
                        if (!isPause()) {
                            update();
                        }
                        draw();
                        lastFrameTime = now;
                    } else {
                        try {
                            Thread.sleep((long) (frameInterval - delta));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }
        setRunning(false);
        thread.interrupt();
    }
}
