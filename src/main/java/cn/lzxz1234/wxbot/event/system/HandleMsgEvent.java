package cn.lzxz1234.wxbot.event.system;

import com.alibaba.fastjson.JSONObject;

import cn.lzxz1234.wxbot.event.Event;

public class HandleMsgEvent extends Event {

    private JSONObject rawMsg;
    
    public HandleMsgEvent() {
        
    }
    
    public HandleMsgEvent(String uuid, JSONObject rawMsg) {
        
        super(uuid);
        this.rawMsg = rawMsg;
    }

    public JSONObject getRawMsg() {
        return rawMsg;
    }

    public void setRawMsg(JSONObject rawMsg) {
        this.rawMsg = rawMsg;
    }
}
