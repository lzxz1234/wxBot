package me.lzxz1234.wxbot.task.passive;

import com.alibaba.fastjson.JSON;

import me.lzxz1234.wxbot.WXUtils.EventHandler;
import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.BatchEvent;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.task.EventListener;

public class Batch extends EventListener<BatchEvent> {

    @Override
    public Event handleEnvent(BatchEvent e, WXHttpClientContext context)
            throws Exception {
        
        if(e.getRealEvents() != null)
            for(Event each : JSON.parseArray(e.getRealEvents(), Event.class))
                new EventHandler<Event>(each).run();
        return null;
    }

}
