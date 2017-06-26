package cn.lzxz1234.wxbot.event.system;

import cn.lzxz1234.wxbot.event.Event;

public class LoginEvent extends Event {

    private String url;
    
    public LoginEvent() {
        
    }
    public LoginEvent(String uuid, String url) {
        
        super(uuid);
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
