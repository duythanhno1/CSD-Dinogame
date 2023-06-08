package edu.fpt.se1605.group6.dinogame.scenes;

import edu.fpt.se1605.group6.library.image.ImageUtilities;
import edu.fpt.se1605.group6.library.scene.Scene;
import edu.fpt.se1605.group6.library.scene.SceneManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class LoadingScene extends Scene {

    private static final float STEP = 1f;
    private static final int ALPHA_STEP = 10;
    private static final int LOADING_BAR_HEIGHT = 30;
    private static final int LOADING_BAR_WIDTH = 320;
    private static final int LOADING_BAR_BORDER = 3;
    private static final int LOADING_BAR_PADDING = 4;
    private static final int LOADING_BAR_FILL_WIDTH = LOADING_BAR_WIDTH - LOADING_BAR_PADDING * 2;
    private static final int LOADING_BAR_FILL_HEIGHT = LOADING_BAR_HEIGHT - LOADING_BAR_PADDING * 2;
    private static final Stroke LOADING_BAR_STROKE = new BasicStroke(LOADING_BAR_BORDER);

    private final MediaTracker tracker;
    private float percentage; // percentage
    private final int imageCount;
    private int imageDone = 0;
    private Color barColor = Color.GRAY;
    private boolean fadeOut = false;
    private final transient Map<String, Image> images;
    private final String[] idMap;

    public LoadingScene(Map<String, Image> imageMap) {
        super(60);
        setSize(960, 544);
        this.imageCount = imageMap.size();
        tracker = new MediaTracker(this);
        this.images = imageMap;
        this.idMap = new String[imageCount];
        int id = 0;
        for (Map.Entry<String, Image> entry : images.entrySet()) {
            idMap[id] = entry.getKey();
            tracker.addImage(entry.getValue(), id);
            id++;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(barColor);
        int loadingBarX = (getWidth() - LOADING_BAR_WIDTH) / 2;
        int loadingBarY = (getHeight() - LOADING_BAR_HEIGHT) / 2;
        g2d.setStroke(LOADING_BAR_STROKE);
        g2d.drawRect(loadingBarX - LOADING_BAR_PADDING, loadingBarY - LOADING_BAR_PADDING,
                LOADING_BAR_WIDTH + LOADING_BAR_PADDING * 2, LOADING_BAR_HEIGHT + LOADING_BAR_PADDING * 2);
        g2d.fillRect(loadingBarX + LOADING_BAR_PADDING, loadingBarY + LOADING_BAR_PADDING,
                Math.round(LOADING_BAR_FILL_WIDTH * percentage / 100), LOADING_BAR_FILL_HEIGHT);
    }

    @Override
    public void update() {
        if (percentage < 100) {
            // check if image is loaded and update percentage
            // otherwise, increase percentage smoothly
            if (tracker.checkID(imageDone, true)) {
                // convert loaded image to buffered image and store it back
                BufferedImage image = ImageUtilities.toBufferedImage(images.get(idMap[imageDone]));
                images.put(idMap[imageDone], image);
                // if images done equals image count, then set percentage to 100 and set color to green
                // otherwise, set percentage by calculating the percentage of images done
                if (++imageDone == imageCount) {
                    percentage = 100f;
                    barColor = new Color(0x00F04F);
                } else {
                    percentage = ((float) imageDone / imageCount) * 100.0f;
                }
            } else {
                percentage += STEP;
            }
        } else {
            if (fadeOut) {
                if (barColor.getAlpha() - ALPHA_STEP <= 0) {
                    // completely transparent
                    barColor = new Color(0, true);
                    this.stop();
                    getSceneManager().next();
                } else {
                    barColor = new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), barColor.getAlpha() - ALPHA_STEP);
                }
            } else {
                fadeOut = true;
            }
        }
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        super.setSceneManager(sceneManager);
        getSceneManager().getParent().setTitle("Loading...");
    }
}
