package ca.cmpt276.walkinggroup.dataobjects;

public class Title {
    private String title;
    private int point;

    public Title(String title, int point) {
        this.title = title;
        this.point = point;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

}
