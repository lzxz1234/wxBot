package cn.lzxz1234.wxbot.event.active;

import cn.lzxz1234.wxbot.event.Event;

public class SendMsgEvent extends Event {

    private String dst;
    private String word;
    
    public SendMsgEvent() {
        
    }
    public SendMsgEvent(String uuid, String dst, String word) {
        
        super(uuid);
        this.dst = dst;
        this.word = word;
    }
    
    public String getDst() {
        return dst;
    }
    public void setDst(String dst) {
        this.dst = dst;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    
}
