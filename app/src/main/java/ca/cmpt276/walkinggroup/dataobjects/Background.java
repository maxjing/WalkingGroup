package ca.cmpt276.walkinggroup.dataobjects;

public class Background {
    private String points;
    private int iconID;

    public Background(String points, int iconID) {
        this.points = points;
        this.iconID = iconID;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
