package cn.lzxz1234.wxbot.event.message;

public class FileHelperMessageEvent extends MessageEvent {

    private String data;
    
    public FileHelperMessageEvent() {
        
    }
    
    public FileHelperMessageEvent(String uuid) {
        
        super(uuid);
    }
    

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
}
