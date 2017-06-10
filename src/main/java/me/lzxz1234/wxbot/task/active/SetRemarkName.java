package me.lzxz1234.wxbot.task.active;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.active.SetRemarkNameEvent;
import me.lzxz1234.wxbot.task.EventListener;

public class SetRemarkName extends EventListener<SetRemarkNameEvent> {
    
    @Override
    public Event handleEnvent(SetRemarkNameEvent e, WXHttpClientContext context)
            throws Exception {
        
        String remarkName = e.getRemarkName();
        String uid = e.getUid();
        String uuid = e.getUuid();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxoplog")
                .addParameter("lang", "zh_CN")
                .addParameter("pass_ticket", context.getPassTicket())
                .build();
        JSONObject params = new JSONObject();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("CmdId", 2);
        params.put("RemarkName", remarkName);
        params.put("UserName", uid);
        
        HttpPost post = new HttpPost(uri);
        post.setEntity(new StringEntity(params.toJSONString(), "UTF-8"));
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            log.debug(uuid + " 修改备注名 " + data);
        } finally {
            resp.close();
        }
        return null;
    }
    
}
