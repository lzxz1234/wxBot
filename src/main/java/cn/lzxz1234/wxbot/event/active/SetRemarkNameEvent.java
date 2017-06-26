package cn.lzxz1234.wxbot.event.active;

import cn.lzxz1234.wxbot.event.Event;

public class SetRemarkNameEvent extends Event {

    private String uid;
    private String remarkName;
    
    public SetRemarkNameEvent() {
        
    }
    public SetRemarkNameEvent(String uuid, String uid, String remarkName) {
        
        super(uuid);
        this.uid = uid;
        this.remarkName = remarkName;
    }
    
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getRemarkName() {
        return remarkName;
    }
    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }
    
}
