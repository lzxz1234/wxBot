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
import me.lzxz1234.wxbot.event.BatchEvent;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.GetContactEvent;
import me.lzxz1234.wxbot.event.InitEvent;
import me.lzxz1234.wxbot.event.ProcMsgEvent;
import me.lzxz1234.wxbot.event.StatusNotifyEvent;
import me.lzxz1234.wxbot.task.EventListener;
import me.lzxz1234.wxbot.vo.Account;
import me.lzxz1234.wxbot.vo.SyncKey;

public class Init extends EventListener<InitEvent> {

    @Override
    public Event handleEnvent(InitEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxinit")
                .addParameter("r", String.valueOf(System.currentTimeMillis() / 1000.0))
                .addParameter("lang", "zh_CN")
                .addParameter("pass_ticket", context.getPassTicket()).build();
        HttpPost post = new HttpPost(uri);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", context.getBaseRequest());
        post.setEntity(new StringEntity(JSON.toJSONString(params)));
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            JSONObject dict = JSON.parseObject(data);
            context.setMyAccount(dict.getObject("User", Account.class));
            context.setSyncKey(dict.getObject("SyncKey", SyncKey.class));
            
            if(dict.getJSONObject("BaseResponse").getIntValue("Ret") == 0) {
                
                context.setStatus("inited");
                log.debug(uuid + " 初始化成功");
                return new BatchEvent(uuid, 
                        new StatusNotifyEvent(uuid), 
                        new GetContactEvent(uuid), 
                        new ProcMsgEvent(uuid));
            } else {
                context.setStatus("loginout");
                log.error(uuid + " 初始化失败：\r\n" + data);
            }
        } finally {
            resp.close();
        }
        return null;
    }

}
