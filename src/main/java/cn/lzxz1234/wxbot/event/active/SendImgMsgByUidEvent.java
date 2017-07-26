package cn.lzxz1234.wxbot.event.active;

import cn.lzxz1234.wxbot.event.Event;

public class SendImgMsgByUidEvent extends Event {

    private String uid;
    private Type type;
    private byte[] file;
    
    public SendImgMsgByUidEvent() {
        
    }
    public SendImgMsgByUidEvent(String uuid, String dst, byte[] file, Type type) {
        super(uuid);
        this.uid = dst;
        this.file = file;
        this.type = type;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String dst) {
        this.uid = dst;
    }
    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
    
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        
        JPG("image/jpeg"), GIF("image/gif"); 
        
        private String mimeType;
        Type(String mimeType) {
            this.mimeType = mimeType;
        }
        public String getMimeType() {
            
            return this.mimeType;
        }
    }
    
}
