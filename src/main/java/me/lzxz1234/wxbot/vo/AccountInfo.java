package me.lzxz1234.wxbot.vo;

public class AccountInfo {

    private GroupMember groupMember = new GroupMember();
    private NormalMember normalMember = new NormalMember();
    
    public GroupMember getGroupMember() {
        return groupMember;
    }
    public void setGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }
    public NormalMember getNormalMember() {
        return normalMember;
    }
    public void setNormalMember(NormalMember normalMember) {
        this.normalMember = normalMember;
    }
    
}
