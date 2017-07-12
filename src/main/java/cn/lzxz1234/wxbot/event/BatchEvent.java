package cn.lzxz1234.wxbot.event;

import java.util.Arrays;
import java.util.List;

public class BatchEvent extends Event {

    private List<Event> realEvents;
    public BatchEvent() {
        
    }
    
    public BatchEvent(String uuid, Event... realEvents) {
        
        super(uuid);
        this.realEvents = Arrays.asList(realEvents);
    }

    public List<Event> getRealEvents() {
        return realEvents;
    }

    public void setRealEvents(List<Event> realEvents) {
        this.realEvents = realEvents;
    }

    
}
