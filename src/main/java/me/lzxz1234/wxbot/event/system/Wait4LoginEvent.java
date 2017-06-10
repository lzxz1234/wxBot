package me.lzxz1234.wxbot.event.system;

import me.lzxz1234.wxbot.event.Event;

public class Wait4LoginEvent extends Event {

    private String tip;
    private int retry;
    
    public Wait4LoginEvent() {
        
    }
    public Wait4LoginEvent(String tip, String uuid, int retry) {
        super(uuid);
        this.tip = tip;
        this.retry = retry;
    }
    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }
    public int getRetry() {
        return retry;
    }
    public void setRetry(int retry) {
        this.retry = retry;
    }
    
}
