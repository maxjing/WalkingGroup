package ca.cmpt276.walkinggroup.dataobjects;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * a customize data type to store LatLng, Description, and group ID for walking group
 * usually used as list
 */

public class GroupInfo {
    private LatLng latLng;
    private String Des;
    private Long ID;


    public GroupInfo() {}
    public GroupInfo(LatLng latLng, String des, Long ID) {
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


}
