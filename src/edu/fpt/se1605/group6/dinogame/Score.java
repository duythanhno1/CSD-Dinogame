package edu.fpt.se1605.group6.dinogame;

public class Score {
    private final int score;
    private final String name;

    public Score(String name, int score) {
        this.score = score;
        this.name = name;
    }

    public static Score parse(String nextLine) {
        String[] split = nextLine.split(": ");
        return new Score(split[0], Integer.parseInt(split[1]));
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + ": " + getScore();
    }
}
