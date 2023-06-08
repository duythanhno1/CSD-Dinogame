package edu.fpt.se1605.group6.library.scene;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    private int current;
    private final List<Scene> scenes;
    private final JFrame parent;

    public SceneManager(JFrame parent) {
        this.parent = parent;
        this.scenes = new ArrayList<>();
    }

    public void next() {
        Scene previous = getCurrentScene();
        previous.setVisible(false);
        previous.stop();
        if (current < scenes.size() - 1) {
            current++;
        } else {
            current = 0;
        }
        start();
    }

    public void previous() {
        Scene previous = getCurrentScene();
        previous.setVisible(false);
        previous.stop();
        if (current > 0) {
            current--;
        } else {
            current = scenes.size() - 1;
        }
        start();
    }

    public int addScene(Scene scene) {
        scenes.add(scene);
        scene.setSceneManager(this);
        scene.setVisible(false);
        return scenes.size() - 1;
    }

    public void setScene(int index) {
        current = index;
    }

    public Scene getCurrentScene() {
        return scenes.get(current);
    }

    public Scene getScene(int index) {
        return scenes.get(index);
    }

    public int size() {
        return scenes.size();
    }

    public JFrame getParent() {
        return parent;
    }

    public void start() {
        Scene scene = getCurrentScene();
        JFrame parent = getParent();
        parent.setSize(scene.getSize());
        parent.setLocationRelativeTo(null);
        scene.setVisible(true);
        scene.start();
    }
}
