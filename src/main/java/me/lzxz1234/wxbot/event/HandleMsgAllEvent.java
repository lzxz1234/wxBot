package me.lzxz1234.wxbot.event;

import me.lzxz1234.wxbot.vo.Message;

public class HandleMsgAllEvent extends Event {

    private Message realMsg;

    public HandleMsgAllEvent() {
        
    }
    public HandleMsgAllEvent(String uuid, Message realMsg) {
        
        super(uuid);
        this.realMsg = realMsg;
    }
    public Message getRealMsg() {
        return realMsg;
    }

    public void setRealMsg(Message realMsg) {
        this.realMsg = realMsg;
    }
    
}
