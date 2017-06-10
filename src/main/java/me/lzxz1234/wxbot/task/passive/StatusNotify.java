package me.lzxz1234.wxbot.task.passive;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.system.StatusNotifyEvent;
import me.lzxz1234.wxbot.task.EventListener;

public class StatusNotify extends EventListener<StatusNotifyEvent> {

    @Override
    public Event handleEnvent(StatusNotifyEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxstatusnotify")
                .addParameter("lang", "zh_CN")
                .addParameter("pass_ticket", context.getPassTicket()).build();
        
        HttpPost post = new HttpPost(uri);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("Code", 3);
        params.put("FromUserName", context.getMyAccount().getUserName());
        params.put("ToUserName", context.getMyAccount().getUserName());
        params.put("ClientMsgId", System.currentTimeMillis() / 1000);
        post.setEntity(new StringEntity(JSON.toJSONString(params)));
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            JSONObject dict = JSON.parseObject(data);
            if(dict.getJSONObject("BaseResponse").getIntValue("Ret") == 0) {
                
                log.debug(uuid + " 状态通知成功");
            } else {
                log.debug(uuid + " 状态通知失败：\r\n" + data);
            }
        } finally {
            resp.close();
        }
        return null;
    }

}
