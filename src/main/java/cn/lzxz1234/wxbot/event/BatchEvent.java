package cn.lzxz1234.wxbot.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class BatchEvent extends Event {

    private String realEvents;
    public BatchEvent() {
        
    }
    
    public BatchEvent(String uuid, Event... realEvents) {
        
        super(uuid);
        this.realEvents = JSON.toJSONString(realEvents, SerializerFeature.WriteClassName);
    }

    public String getRealEvents() {
        return realEvents;
    }

    public void setRealEvents(String realEvents) {
        this.realEvents = realEvents;
    }
    
}
