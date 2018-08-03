package ca.cmpt276.walkinggroup.dataobjects;

/**
 * Store information about the background.
 */

public class Background {
    private int points;
    private int iconID;

    public Background(int points, int iconID) {
        this.points = points;
        this.iconID = iconID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
