package edu.fpt.se1605.group6.dinogame;

import edu.fpt.se1605.group6.dinogame.scenes.GameScene;
import edu.fpt.se1605.group6.dinogame.scenes.LoadingScene;
import edu.fpt.se1605.group6.library.scene.Scene;
import edu.fpt.se1605.group6.library.scene.SceneManager;
import resources.ResourceGetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DinoGame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(DinoGame.class.getName());

    public static Logger getLogger() {
        return LOGGER;
    }

    public static final Font FONT;

    static {
        InputStream is = ResourceGetter.getResourceAsStream("resources/fonts/Coders_Crux.ttf");
        Font font = new Font("Arial", Font.BOLD, 12);
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            LOGGER.log(Level.SEVERE, "ERROR", ex);
        }
        Map<TextAttribute, Object> attributes = new HashMap<>();

        attributes.put(TextAttribute.FAMILY, font.getFamily());
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        attributes.put(TextAttribute.SIZE, 12);
        font.deriveFont(attributes);
        FONT = font;
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        DinoGame game = new DinoGame();
        game.setupLookAndFeel();
        game.run();
    }

    private final transient SceneManager sceneManager;

    public DinoGame() {
        this.sceneManager = new SceneManager(this);
        setTitle("SE1605 - Group 6 - Dino");
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                save();
                System.exit(0);
            }
        });
        setDefaultLookAndFeelDecorated(true);
        load();
        initComponents();
    }

    private void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            getLogger().log(Level.SEVERE, "Error setting up look and feel", ex);
        }
        setDefaultLookAndFeelDecorated(true);
    }

    private final List<Score> highScores = new ArrayList<>();
    private static final File HISCORE_FILE = new File("highscores.txt");

    private void save() {
        try (FileWriter writer = new FileWriter(HISCORE_FILE)) {
            StringBuilder sb = new StringBuilder();
            for (Score score : highScores) {
                sb.append(score).append('\n');
            }
            writer.write(sb.toString());
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Error saving high scores", ex);
        }
    }

    private void load() {
        if (HISCORE_FILE.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(HISCORE_FILE))) {
                String line;
                int n = 0;
                while ((line = reader.readLine()) != null && n < 10) {
                    highScores.add(Score.parse(line));
                    n++;
                }
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "Error loading high scores", ex);
            }
        }
    }

    private void initComponents() {
        Map<String, Image> images = new HashMap<>();
        images.put("cactus", ResourceGetter.getImage("resources/images/cactus.png"));
        images.put("ground", ResourceGetter.getImage("resources/images/ground.png"));
        images.put("player_death", ResourceGetter.getImage("resources/images/player_death.png"));
        images.put("player_ducking", ResourceGetter.getImage("resources/images/player_ducking.png"));
        images.put("player_running", ResourceGetter.getImage("resources/images/player_running.png"));
        images.put("player_standing", ResourceGetter.getImage("resources/images/player_standing.png"));
        images.put("pterodactyl", ResourceGetter.getImage("resources/images/pterodactyl.png"));
        images.put("sandy", ResourceGetter.getImage("resources/images/sandy.png"));
        images.put("sky", ResourceGetter.getImage("resources/images/sky.png"));
        images.put("play_button", ResourceGetter.getImage("resources/images/button_play.png"));
        images.put("restart_button", ResourceGetter.getImage("resources/images/button_restart.png"));
        Scene loadingScene = new LoadingScene(images);
        Scene gameScene = new GameScene(images, highScores);
        sceneManager.addScene(loadingScene);
        sceneManager.addScene(gameScene);
        setLayout(new BorderLayout());
        add(loadingScene, BorderLayout.CENTER);
        add(gameScene, BorderLayout.CENTER);
        sceneManager.start();
    }

    private void run() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }


}
