package ca.cmpt276.walkinggroup.app;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

class GroupInfo {
    private LatLng latLng;
    private String Des;
    private Long ID;
    private String meetPlace;

    public GroupInfo() {}
    public GroupInfo(LatLng latLng, String des, Long ID, String meetPlace) {
        this.latLng = latLng;
        Des = des;
        this.ID = ID;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getMeetPlace() {
        return meetPlace;
    }

    public void setMeetPlace(String meetPlace) {
        this.meetPlace = meetPlace;
    }
}
