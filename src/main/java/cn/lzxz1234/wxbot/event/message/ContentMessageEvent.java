package cn.lzxz1234.wxbot.event.message;

import cn.lzxz1234.wxbot.vo.Content;

public abstract class ContentMessageEvent extends MessageEvent {

    private Content content;

    public ContentMessageEvent() {
        
    }
    
    public ContentMessageEvent(String uuid) {
        
        super(uuid);
    }
    
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
    
}
