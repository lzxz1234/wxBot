package me.lzxz1234.wxbot.task.passive;

import org.apache.commons.lang.RandomStringUtils;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.system.NewEvent;
import me.lzxz1234.wxbot.event.system.Wait4LoginEvent;
import me.lzxz1234.wxbot.task.EventListener;


public class New extends EventListener<NewEvent> {

    @Override
    public Event handleEnvent(NewEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        context.setStatus("wait4login");
        context.setOtherInfo(e.getOtherInfo());
        context.getBaseRequest().setDeviceId("e" + RandomStringUtils.randomNumeric(15));
        log.debug("启动微信进程：" + uuid + "，待扫码");
        return new Wait4LoginEvent("1", uuid, 10);
    }

}
