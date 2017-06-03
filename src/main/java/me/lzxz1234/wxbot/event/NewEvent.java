package me.lzxz1234.wxbot.event;

import java.util.Map;

public class NewEvent extends Event {

    private Map<String, Object> otherInfo;
    
    public NewEvent() {
        
    }
    
    public NewEvent(String uuid, Map<String, Object> otherInfo) {

        super(uuid);
        this.otherInfo = otherInfo;
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }
    public void setOtherInfo(Map<String, Object> otherInfo) {
        this.otherInfo = otherInfo;
    }
    
}
