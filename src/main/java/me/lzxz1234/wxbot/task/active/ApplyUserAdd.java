package me.lzxz1234.wxbot.task.active;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.active.ApplyUserAddEvent;
import me.lzxz1234.wxbot.task.EventListener;
import me.lzxz1234.wxbot.vo.RecommendInfo;

public class ApplyUserAdd extends EventListener<ApplyUserAddEvent> {

    @Override
    public Event handleEnvent(ApplyUserAddEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        RecommendInfo recommendInfo = e.getRecommendInfo();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxverifyuser")
                .addParameter("r", String.valueOf(System.currentTimeMillis() / 1000))
                .addParameter("lang", "zh_CN")
                .build();
        JSONObject params = new JSONObject();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("Opcode", 3);
        params.put("VerifyUserListSize", 1);
        params.put("VerifyUserList", new JSONArray());
        params.getJSONArray("VerifyUserList").add(new JSONObject());
        params.getJSONArray("VerifyUserList").getJSONObject(0).put("Value", recommendInfo.getUserName());
        params.getJSONArray("VerifyUserList").getJSONObject(0).put("VerifyUserTicket", recommendInfo.getTicket());
        params.put("VerifyContent", "");
        params.put("SceneListCount", 1);
        params.put("SceneList", new JSONArray());
        params.getJSONArray("SceneList").add(33);
        params.put("skey", context.getBaseRequest().getSkey());
        
        HttpPost post = new HttpPost(uri);
        post.setHeader("content-type", "application/json; charset=UTF-8");
        post.setEntity(new StringEntity(params.toJSONString(), "UTF-8"));
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            log.debug(uuid + " 通过好友请求 " + data);
        } finally {
            resp.close();
        }
        return null;
    }

}
