package me.lzxz1234.wxbot.task.active;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.system.SendMsgByUidEvent;
import me.lzxz1234.wxbot.task.EventListener;

public class SendMsgByUid extends EventListener<SendMsgByUidEvent> {

    @Override
    public Event handleEnvent(SendMsgByUidEvent e, WXHttpClientContext context)
            throws Exception {
        
        String word = e.getWord();
        String dst = e.getUid();
        String uuid = e.getUuid();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxsendmsg")
                .addParameter("pass_ticket", context.getPassTicket())
                .build();
        String msgId = String.valueOf(System.currentTimeMillis()) + RandomStringUtils.randomNumeric(4);
        JSONObject params = new JSONObject();
        JSONObject msg = new JSONObject();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("Msg", msg);
        msg.put("Type", 1);
        msg.put("Content", word);
        msg.put("FromUserName", context.getMyAccount().getUserName());
        msg.put("ToUserName", dst);
        msg.put("LocalID", msgId);
        msg.put("ClientMsgId", msgId);
        
        HttpPost post = new HttpPost(uri);
        post.setEntity(new StringEntity(params.toJSONString(), "UTF-8"));
        post.setHeader("content-type", "application/json; charset=UTF-8");
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            log.debug(uuid + " 发送消息 " + word + " 成功");
        } finally {
            resp.close();
        }
        return null;
    }

}
