package edu.fpt.se1605.group6.dinogame.scenes;

import edu.fpt.se1605.group6.dinogame.Score;
import edu.fpt.se1605.group6.dinogame.background.Ground;
import edu.fpt.se1605.group6.dinogame.background.Landscape;
import edu.fpt.se1605.group6.dinogame.background.Sky;
import edu.fpt.se1605.group6.dinogame.entities.Cactus;
import edu.fpt.se1605.group6.dinogame.entities.Dino;
import edu.fpt.se1605.group6.dinogame.entities.GameEntity;
import edu.fpt.se1605.group6.dinogame.entities.Pterodactyl;
import edu.fpt.se1605.group6.library.image.ImageUtilities;
import edu.fpt.se1605.group6.library.scene.Scene;
import edu.fpt.se1605.group6.library.scene.SceneManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static edu.fpt.se1605.group6.dinogame.DinoGame.FONT;
import static edu.fpt.se1605.group6.library.image.ImageUtilities.*;

public class GameScene extends Scene implements KeyListener {

    private static final float SCALE = 4.0f;
    private static final float GRAVITY = 8.5f;
    private static final float JUMP_VELOCITY = -GRAVITY * 3;
    private static final float DUCK_VELOCITY = GRAVITY * 3;
    private static final float RUN_VELOCITY = 8f;
    private static final Random RANDOM = new Random();
    private static final long SPAWN_INTERVAL = 1050;
    private static final int GROUND_SCROLL_ACCELERATION = 2;
    private static final int LANDSCAPE_SCROLL_ACCELERATION = 1;
    private static final float PTERODACTYL_SPEED = 5;

    private final transient Map<String, Image> imageMap;
    private final transient List<GameEntity> entities;
    private final JButton btnStart;
    private final JTextField txtName;
    private final JLabel lblErrorFeedback;
    private final JLabel lblPlayerName;
    private final JLabel lblScore;
    private final transient List<Score> highScore;
    private final Set<Integer> heldKeys = new ConcurrentSkipListSet<>();
    private transient Sky sky;
    private transient Landscape landscape;
    private transient Ground ground;
    private transient Dino dino;
    private String playerName;
    private boolean isPlaying;
    private float score;
    private boolean initialized = false;
    private int groundTopY;
    private int speedX = 0;
    private float speedY = 0;
    private transient Image[] cactusVariants;
    private long lastSpawn = 0;
    private JTextArea leaderboard;
    private JPanel lbPanel;
    private transient Image[] pterodactylImages;

    public GameScene(Map<String, Image> imageMap, List<Score> highScore) {
        super(60f);
        setSize(960, 544);
        setPreferredSize(new Dimension(960, 544));
        setMaximumSize(new Dimension(960, 544));
        setMinimumSize(new Dimension(960, 544));
        this.imageMap = imageMap;
        this.entities = new CopyOnWriteArrayList<>();
        this.highScore = highScore;
        this.playerName = "Dino";
        this.score = 0;
        this.btnStart = new JButton();
        this.txtName = new JTextField();
        this.lblErrorFeedback = new JLabel();
        this.lblPlayerName = new JLabel();
        this.lblScore = new JLabel();
        addKeyListener(this);
    }

    private static int randomInt(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    private void initComponents() {
        setLayout(null);
        FontMetrics fm;
        lblPlayerName.setText("Player name:");
        lblPlayerName.setFont(FONT.deriveFont(24f));
        fm = lblPlayerName.getFontMetrics(lblPlayerName.getFont());
        lblPlayerName.setSize(fm.stringWidth(lblPlayerName.getText()), fm.getHeight());

        txtName.setFont(FONT.deriveFont(24f));
        txtName.setBackground(Color.LIGHT_GRAY);
        txtName.setForeground(Color.BLACK);
        int borderWidth = 4;
        int padding = 5;
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, borderWidth, true),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
        txtName.setBorder(border);
        fm = txtName.getFontMetrics(txtName.getFont());
        int outer = (borderWidth + padding) * 2;
        txtName.setSize(fm.charWidth('A') * 16 + outer, fm.getHeight() + outer);
        txtName.addActionListener(e -> btnStart.doClick());

        lblErrorFeedback.setFont(FONT.deriveFont(Font.ITALIC, 18f));
        fm = lblErrorFeedback.getFontMetrics(lblErrorFeedback.getFont());
        lblErrorFeedback.setSize(fm.stringWidth("Please enter your name!") + 20, fm.getHeight());
        lblErrorFeedback.setForeground(Color.RED);
        lblErrorFeedback.setSize(150, 30);
        BufferedImage raw = toBufferedImage(scaleImage(imageMap.get("play_button"), SCALE));
        Image[] playButtonSprites = splitImageX(raw, 3);
        int width = playButtonSprites[0].getWidth(null);
        int height = playButtonSprites[0].getHeight(null);
        btnStart.setText("");
        btnStart.setIcon(new ImageIcon(playButtonSprites[0]));
        btnStart.setRolloverIcon(new ImageIcon(playButtonSprites[1]));
        btnStart.setPressedIcon(new ImageIcon(playButtonSprites[2]));
        btnStart.setSize(width, height);
        btnStart.setBackground(new Color(0, true));
        btnStart.setBorder(BorderFactory.createEmptyBorder());
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(this::onSetName);

        int totalHeight = lblPlayerName.getHeight() + txtName.getHeight() + lblErrorFeedback.getHeight() + btnStart.getHeight();
        int halfWidth = getWidth() / 2;
        int firstY = (getHeight() - totalHeight) / 2;
        int txtNameX = halfWidth - txtName.getWidth() / 2;

        lblPlayerName.setLocation(txtNameX, firstY);
        txtName.setLocation(txtNameX, firstY + lblPlayerName.getHeight());
        lblErrorFeedback.setLocation(halfWidth - lblErrorFeedback.getWidth() / 2, (int) txtName.getBounds().getMaxY() - 10);
        btnStart.setLocation(halfWidth - btnStart.getWidth() / 2, (int) lblErrorFeedback.getBounds().getMaxY());

        add(lblPlayerName);
        add(txtName);
        add(lblErrorFeedback);
        add(btnStart);
        txtName.requestFocusInWindow();

        lblScore.setFont(FONT.deriveFont(Font.BOLD, 32f));
        lblScore.setText(String.format("%s: %06d", playerName, Math.round(score)));
        fm = lblScore.getFontMetrics(lblScore.getFont());
        lblScore.setSize(getWidth() - 10, fm.getHeight() + 2);
        lblScore.setLocation(10, 10);
        add(lblScore);

        leaderboard = new JTextArea();
        leaderboard.setFont(new Font("Monospaced", Font.PLAIN, 24));
        leaderboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboard.setAlignmentY(Component.CENTER_ALIGNMENT);
        fm = leaderboard.getFontMetrics(leaderboard.getFont());
        leaderboard.setSize(fm.stringWidth("0") * 40, fm.getHeight() * 12);
        leaderboard.setLocation(getWidth() / 2 - leaderboard.getWidth() / 2, getHeight() / 2 - leaderboard.getHeight() / 2);
        leaderboard.setBackground(Color.LIGHT_GRAY);
        leaderboard.setForeground(Color.BLACK);
        leaderboard.setText("Loading...");
        leaderboard.setEditable(false);
        leaderboard.setLineWrap(false);
        leaderboard.setFocusable(false);
        lbPanel = new JPanel(new BorderLayout());
        lbPanel.setBackground(new Color(0, true));
        lbPanel.setLayout(new BorderLayout());
        lbPanel.add(leaderboard, BorderLayout.CENTER);
        lbPanel.setBorder(BorderFactory.createTitledBorder(border,
                        "LEADERBOARD",
                        TitledBorder.CENTER,
                        TitledBorder.CENTER,
                        FONT.deriveFont(Font.BOLD, 32f),
                        Color.BLACK
                )
        );
        lbPanel.setSize(leaderboard.getWidth() + 10, leaderboard.getHeight() + 10);
        lbPanel.setLocation(getWidth() / 2 - lbPanel.getWidth() / 2, getHeight() / 2 - lbPanel.getHeight() / 2);
        lbPanel.setVisible(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideLeaderboard();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    hideLeaderboard();
                }
            }
        });
        add(lbPanel);

    }

    private void hideLeaderboard() {
        if (lbPanel.isVisible()) {
            lbPanel.setVisible(false);
        }
    }

    private boolean onSetName(ActionEvent e) {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            lblErrorFeedback.setText("Please enter your name!");
            txtName.selectAll();
            txtName.requestFocusInWindow();
            return false;
        } else {
            setPlayerName(name);
            lblErrorFeedback.setText("");
            btnStart.setVisible(false);
            txtName.setVisible(false);
            lblPlayerName.setVisible(false);
            lblErrorFeedback.setVisible(false);
            lblScore.setVisible(true);
            play();
            return true;
        }
    }

    private void resetComponents() {
        btnStart.setVisible(true);
        txtName.setVisible(true);
        lblPlayerName.setVisible(true);
        lblErrorFeedback.setText("");
        lblErrorFeedback.setVisible(true);
        txtName.setText(playerName);
        txtName.selectAll();
        txtName.requestFocusInWindow();
        lblScore.setVisible(false);
    }

    public void reset() {
        heldKeys.clear();
        entities.clear();
        ground.pause();
        landscape.pause();
        dino.setState(Dino.State.IDLE);
        dino.setX(dino.getWidth());
        dino.setY(groundTopY - dino.getHeight() + 10);
        score = 0;
    }

    private void restart() {
        int actualScore = Math.round(score);
        highScore.add(new Score(playerName, actualScore));
        highScore.sort(Comparator.comparingInt(Score::getScore).reversed());
        landscape.pause();
        ground.pause();
        lbPanel.setVisible(true);
        StringBuilder sb = new StringBuilder();
        if (highScore.size() > 10) {
            while (highScore.size() > 10) {
                highScore.remove(highScore.size() - 1);
            }
        }
        for (int i = 0; i < 10; i++) {
            String name = "Dino";
            int score = 0;
            if (i < highScore.size()) {
                name = highScore.get(i).getName();
                score = highScore.get(i).getScore();
            }
            if (name.length() > 16) {
                name = name.substring(0, 13) + "...";
            }
            sb.append(String.format("#%-2d %-16s %09d", i + 1, name, score));
            if (name.equals(playerName) && score == actualScore) {
                sb.append(" (YOU)");
            }
            sb.append('\n');
        }
        leaderboard.setText(sb.toString());
        AtomicReference<KeyListener> kl = new AtomicReference<>();
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetComponents();
                removeMouseListener(this);
                removeKeyListener(kl.get());
            }
        };
        kl.set(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetComponents();
                    removeKeyListener(this);
                    removeMouseListener(ma);
                }
            }
        });
        addMouseListener(ma);
        addKeyListener(kl.get());
        BufferedImage raw = toBufferedImage(scaleImage(imageMap.get("restart_button"), SCALE));
        Image[] playButtonSprites = splitImageX(raw, 3);
        btnStart.setIcon(new ImageIcon(playButtonSprites[0]));
        btnStart.setRolloverIcon(new ImageIcon(playButtonSprites[1]));
        btnStart.setPressedIcon(new ImageIcon(playButtonSprites[2]));
        btnStart.removeAll();
        btnStart.addActionListener(a -> {
            if (onSetName(a)) {
                reset();
                play();
            }
        });
    }

    public void play() {
        dino.setState(Dino.State.RUNNING);
        ground.setSpeed(4);
        landscape.setSpeed(2);
        setPlaying(true);
        requestFocusInWindow();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private void initSky() {
        Image skyImg = scaleImage(imageMap.get("sky"), SCALE);
        int width = skyImg.getWidth(null);
        int height = skyImg.getHeight(null);
        setSize(width, height);
        getSceneManager().getParent().setSize(width, height);
        getSceneManager().getParent().setLocationRelativeTo(null);
        this.sky = new Sky(skyImg);
    }

    private void initLandscape() {
        Image landscapeImg = scaleImage(imageMap.get("sandy"), SCALE);
        this.landscape = new Landscape(landscapeImg);
    }

    private void initGround() {
        Image groundImg = scaleImage(imageMap.get("ground"), SCALE);
        this.ground = new Ground(groundImg);
        this.groundTopY = sky.getHeight() - ground.getHeight();
    }

    private void initPlayer() {
        Map<String, Image[]> playerImageMap = new HashMap<>();

        BufferedImage raw;
        Image[] sprites;

        raw = toBufferedImage(scaleImage(imageMap.get("player_standing"), SCALE));
        sprites = splitImageX(raw, 2);
        playerImageMap.put("idle", sprites);

        raw = toBufferedImage(scaleImage(imageMap.get("player_running"), SCALE));
        sprites = splitImageX(raw, 2);
        playerImageMap.put("run", sprites);
        playerImageMap.put("jump", sprites);

        raw = toBufferedImage(scaleImage(imageMap.get("player_ducking"), SCALE));
        sprites = splitImageX(raw, 2);
        playerImageMap.put("duck", sprites);

        raw = toBufferedImage(scaleImage(imageMap.get("player_death"), SCALE));
        sprites = splitImageX(raw, 1);
        playerImageMap.put("death", sprites);

        dino = new Dino(playerImageMap);
    }

    private void checkCollisions() {
        int width = dino.getWidth() / 2;
        int height = dino.getHeight() / 2;
        Rectangle dinoRect = new Rectangle(dino.getX() + width / 2, dino.getY() + height / 2, width, height);
        if (dino.getState() == Dino.State.DUCKING) {
            height /= 2;
            dinoRect.setBounds(dinoRect.x, (int) (dino.getBounds().getMaxY() - height), width, height);
        }
        for (GameEntity entity : entities) {
            if (dinoRect.intersects(entity.getBounds())) {
                dino.setState(Dino.State.DEAD);
                return;
            }
        }
    }

    private void updatePlayer() {
        dino.setX(dino.getX() + speedX);
        speedY += GRAVITY / 5;
        dino.setY(Math.round(dino.getY() + speedY));
        // check if dino is out of bounds
        if (dino.getY() <= 0) {
            dino.setY(0);
        } else if (dino.getY() > groundTopY - dino.getHeight() + 10) {
            dino.setY(groundTopY - dino.getHeight() + 10);
        }
        if (dino.getX() < 0) {
            dino.setX(0);
            if (dino.getState() != Dino.State.RUNNING || dino.getState() != Dino.State.JUMPING || dino.getState() != Dino.State.DUCKING) {
                dino.setState(Dino.State.RUNNING);
            }
        } else if (dino.getX() > getWidth() - dino.getWidth()) {
            dino.setX(getWidth() - dino.getWidth());
        }
    }

    private void spawnEntities() {
        long now = System.currentTimeMillis();
        if (entities.size() <= 1 || now - lastSpawn > SPAWN_INTERVAL) {
            lastSpawn = now;
            int random = randomInt(0, 1000);
            if (random <= 150) {
                spawnCactus(getWidth());
                spawnCactus(getWidth() + randomInt(20, 40));
                spawnCactus(getWidth() + randomInt(40, 60));
                spawnCactus(getWidth() + randomInt(60, 70));
            } else if (random <= 300) {
                spawnCactus(getWidth());
                spawnCactus(getWidth() + randomInt(20, 30));
            } else if (random <= 500) {
                spawnCactus(getWidth());
            }
            if (score >= 1000 && randomInt(0, 1000) <= 100) {
                spawnPterodactyl(randomInt(100, groundTopY - dino.getHeight() / 2 - pterodactylImages[0].getHeight(null)));
            }
        }
    }

    private void updateEntities() {
        List<GameEntity> toRemove = new ArrayList<>();
        for (GameEntity entity : entities) {
            float speed = ground.getSpeed();
            entity.setX(Math.round(entity.getX() - speed));
            if (entity instanceof Pterodactyl) {
                Pterodactyl pterodactyl = (Pterodactyl) entity;
                speed += PTERODACTYL_SPEED;
                pterodactyl.setX((int) (pterodactyl.getX() - PTERODACTYL_SPEED));
                pterodactyl.setFrameRate(speed / 2f);
            }
            if (entity.getX() < -entity.getWidth()) {
                toRemove.add(entity);
                if (entity instanceof Pterodactyl) {
                    ((Pterodactyl) entity).stop();
                }
            }
        }
        entities.removeAll(toRemove);
    }

    private void updateScore() {
        int actualScore = Math.round(score);
        if (actualScore < 10000 && actualScore % 1000 == 0 || actualScore >= 10000 && actualScore % 5000 == 0) {
            landscape.setSpeed(landscape.getSpeed() + LANDSCAPE_SCROLL_ACCELERATION);
            ground.setSpeed(ground.getSpeed() + GROUND_SCROLL_ACCELERATION);
        }
        score += ground.getSpeed() / 4f;
        String scoreStr = String.format("%s: %06d", playerName, Math.round(score));
        if (!highScore.isEmpty()) {
            scoreStr = String.format("%s    HI: %06d (%s)", scoreStr, highScore.get(0).getScore(), highScore.get(0).getName());
        }
        lblScore.setText(scoreStr);
    }

    private void handleKeys() {
        dino.setState(Dino.State.RUNNING);
        speedX = 0;
        for (Integer key : heldKeys) {
            switch (key) {
                case KeyEvent.VK_SHIFT:
                    speedX = -ground.getSpeed();
                    dino.setState(Dino.State.IDLE);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    speedX = -Math.round(RUN_VELOCITY);
                    dino.setState(Dino.State.RUNNING);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    speedX = Math.round(RUN_VELOCITY);
                    dino.setState(Dino.State.RUNNING);
                    break;
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    // only jump if on the ground
                    if (dino.getY() == groundTopY - dino.getHeight() + 10) {
                        speedY = JUMP_VELOCITY;
                        dino.setState(Dino.State.JUMPING);
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (dino.getState() != Dino.State.DUCKING) {
                        speedY = DUCK_VELOCITY;
                        dino.setState(Dino.State.DUCKING);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initPterodactyl() {
        BufferedImage pterodactylImage = toBufferedImage(scaleImage(imageMap.get("pterodactyl"), SCALE));
        pterodactylImages = splitImageX(pterodactylImage, 2);
    }

    private void spawnPterodactyl(int y) {
        Pterodactyl pterodactyl = new Pterodactyl(pterodactylImages);
        pterodactyl.setX(getWidth());
        pterodactyl.setY(y);
        pterodactyl.start();
        entities.add(pterodactyl);
    }

    private void initCactus() {
        BufferedImage cactusImage = ImageUtilities.toBufferedImage(ImageUtilities.scaleImage(imageMap.get("cactus"), SCALE));
        cactusVariants = ImageUtilities.splitImageX(cactusImage, 8);
    }

    private void spawnCactus(int x) {
        Cactus cactus = new Cactus(cactusVariants[randomInt(0, cactusVariants.length)]);
        cactus.setX(x);
        cactus.setY(groundTopY - cactus.getHeight() + 10);
        entities.add(cactus);
    }

    @Override
    public void update() {
        if (sky != null) sky.update();
        if (isPlaying()) {
            updateScore();
            handleKeys();
            updatePlayer();
            checkCollisions();
            if (dino.getState() == Dino.State.DEAD) {
                setPlaying(false);
                landscape.pause();
                ground.pause();
                for (GameEntity entity : entities) {
                    if (entity instanceof Pterodactyl) {
                        ((Pterodactyl) entity).stop();
                    }
                }
                restart();
                return;
            }
            spawnEntities();
            updateEntities();
        }
        if (dino != null) dino.update();
        if (ground != null) ground.update();
        if (landscape != null) landscape.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (sky != null) {
            sky.draw(g2d, 0, 0);
            if (landscape != null) landscape.draw(g2d, 0, sky.getHeight() - landscape.getHeight());
            if (ground != null) ground.draw(g2d, 0, groundTopY);
        }
        if (dino != null) dino.draw(g2d);
        for (GameEntity entity : entities) {
            entity.draw(g2d);
        }
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        super.setSceneManager(sceneManager);
        getSceneManager().getParent().setTitle("Dino");
    }

    @Override
    public void start() {
        if (!initialized) {
            initSky();
            initLandscape();
            initGround();
            initPlayer();
            initComponents();
            initCactus();
            initPterodactyl();
            initialized = true;
        }
        resetComponents();
        reset();
        super.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyPressed(KeyEvent e) {
        heldKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }
}