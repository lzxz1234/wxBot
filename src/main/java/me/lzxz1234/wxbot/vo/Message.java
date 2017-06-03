package me.lzxz1234.wxbot.vo;

import java.util.Date;

public class Message {

    private int msgTypeId;
    private String msgId;
    private Content content;
    private String toUserId;
    private User user;
    private Date createTime = new Date();
    
    public int getMsgTypeId() {
        return msgTypeId;
    }
    /**
     * @param msgTypeId 
     *      0 -> Init
            1 -> Self
            2 -> FileHelper
            3 -> Group
            4 -> Contact
            5 -> Public
            6 -> Special
            37 -> Friend
            99 -> Unknown
     */
    public void setMsgTypeId(int msgTypeId) {
        this.msgTypeId = msgTypeId;
    }
    public String getMsgId() {
        return msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public Content getContent() {
        return content;
    }
    public void setContent(Content content) {
        this.content = content;
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
