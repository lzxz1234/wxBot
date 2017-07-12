package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class AccountInfo implements Serializable {

    private static final long serialVersionUID = 6223770689593948753L;
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
