package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
/**
 * Store information about the walking groups.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group extends IdItemBase{

    private String groupDescription;
    private User leader;
    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private List<Group> memberUsers = new ArrayList<>();
    private boolean hasFullData;


    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public List<Double> getRouteLatArray() {
        return routeLatArray;
    }

    public void setRouteLatArray(List<Double> routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public List<Double> getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLngArray(List<Double> routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public List<Group> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<Group> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public boolean isHasFullData() {
        return hasFullData;
    }

    public void setHasFullData(boolean hasFullData) {
        this.hasFullData = hasFullData;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupDescription='" + groupDescription + '\'' +
                ", leader=" + leader +
                ", routeLatArray=" + routeLatArray +
                ", routeLngArray=" + routeLngArray +
                ", memberUsers=" + memberUsers +
                ", hasFullData=" + hasFullData +
                '}';
    }
}
