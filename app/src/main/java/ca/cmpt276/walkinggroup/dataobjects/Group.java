package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

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
    private List<Group> routeLatArray = new ArrayList<>();
    private List<Group> routeLngArray = new ArrayList<>();
    private List<Group> memberUsers = new ArrayList<>();
    private List<Group> messages = new ArrayList<>();
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

    public List<Group> getRouteLatArray() {
        return routeLatArray;
    }

    public void setRouteLatArray(List<Group> routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public List<Group> getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLngArray(List<Group> routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public List<Group> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<Group> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public List<Group> getMessages() {
        return messages;
    }

    public void setMessages(List<Group> messages) {
        this.messages = messages;
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
                ", messages=" + messages +
                ", hasFullData=" + hasFullData +
                '}';
    }
}
