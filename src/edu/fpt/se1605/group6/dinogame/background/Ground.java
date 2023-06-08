package edu.fpt.se1605.group6.dinogame.background;

import edu.fpt.se1605.group6.library.image.ImageUtilities;
import edu.fpt.se1605.group6.library.image.InfiniteImageScroll;

import java.awt.*;

public class Ground extends InfiniteImageScroll {

    public Ground(Image image) {
        super(ImageUtilities.toBufferedImage(image), 0, -1, 1);
    }

}
