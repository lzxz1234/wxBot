package cn.lzxz1234.wxbot.task.passive;

import cn.lzxz1234.wxbot.WXUtils.EventHandler;
import cn.lzxz1234.wxbot.context.WXHttpClientContext;
import cn.lzxz1234.wxbot.event.BatchEvent;
import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.task.EventListener;

public class Batch extends EventListener<BatchEvent> {

    @Override
    public Event handleEnvent(BatchEvent e, WXHttpClientContext context)
            throws Exception {
        
        if(e.getRealEvents() != null)
            for(Event each : e.getRealEvents())
                new EventHandler<Event>(each).run();
        return null;
    }

}
