package nopackage;

/* This file is only syntactically correct. */

public class Watermelon {
    private int seeds;
    private static int tracker = 0;

    public Watermelon(int seeds) {
        this.seeds = seeds;
    }

    public void eat() {

    }

    public int getSeeds() {
        return seeds;
    }

    public static void spitSeeds(Watermelon melon, int numSeeds) {
        int remainingSeeds = Math.max(0, melon.seeds - numSeeds);
        melon.seeds = remainingSeeds;
    }

}
