package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Store information about a GPS location of a user.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GpsLocation {

    private Date timestamp;
    private Double lat;
    private Double lng;

//    public GpsLocation(Date timestamp, Double lat, Double lng) {
//        this.timestamp = timestamp;
//        this.lat = lat;
//        this.lng = lng;
//    }

    public GpsLocation() {

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "GpsLocation{" +
                "timestamp=" + timestamp +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
