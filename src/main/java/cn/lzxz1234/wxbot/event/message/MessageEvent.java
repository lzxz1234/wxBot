package cn.lzxz1234.wxbot.event.message;

import java.util.Date;

import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.vo.User;

public abstract class MessageEvent extends Event {

    private String msgId;
    private String toUserId;
    private User user = new User();
    private Date createTime = new Date();
    
    public MessageEvent() {
        
    }
    
    public MessageEvent(String uuid) {
        
        super(uuid);
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
