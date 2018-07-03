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

    private boolean hasFullData;
    private User leader;

    private List<Group> memberUsers = new ArrayList<>();

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public boolean isHasFullData() {
        return hasFullData;
    }

    public void setHasFullData(boolean hasFullData) {
        this.hasFullData = hasFullData;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public List<Group> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<Group> memberUsers) {
        this.memberUsers = memberUsers;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + getId() +
                "groupDescription='" + groupDescription + '\'' +
                ", hasFullData=" + hasFullData +
                ", leader='" + leader + '\'' +
                ", memberUsers=" + memberUsers +
                '}';
    }
}
