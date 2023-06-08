package edu.fpt.se1605.group6.dinogame.background;

import edu.fpt.se1605.group6.library.image.ImageUtilities;
import edu.fpt.se1605.group6.library.image.InfiniteImageScroll;

import java.awt.*;

public class Sky extends InfiniteImageScroll {

    public Sky(Image sky) {
        super(ImageUtilities.toBufferedImage(sky), 0, -1, 1);
        resume();
    }

}
