package cn.lzxz1234.wxbot.event.message;

import cn.lzxz1234.wxbot.vo.User;

public class GroupMessageEvent extends ContentMessageEvent {
    
    private User atUser;
    
    public GroupMessageEvent() {
        
    }
    
    public GroupMessageEvent(String uuid) {
        
        super(uuid);
    }

    public User getAtUser() {
        return atUser;
    }

    public void setAtUser(User atUser) {
        this.atUser = atUser;
    }

}
