package me.lzxz1234.wxbot.event.system;

import me.lzxz1234.wxbot.event.Event;

public class SendMsgByUidEvent extends Event {

    private String uid;
    private String word;
    
    public SendMsgByUidEvent() {
        
    }
    public SendMsgByUidEvent(String uuid, String dst, String word) {
        super(uuid);
        this.uid = dst;
        this.word = word;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String dst) {
        this.uid = dst;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    
}
