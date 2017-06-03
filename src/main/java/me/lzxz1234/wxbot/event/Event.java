package me.lzxz1234.wxbot.event;

import java.util.Date;

public abstract class Event {

    private String uuid;
    private Date createTime = new Date();

    public Event(String uuid) {

        this.uuid = uuid;
    }
    public Event() {

    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
}
