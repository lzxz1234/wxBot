package cn.lzxz1234.wxbot.task.active;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.lzxz1234.wxbot.context.WXHttpClientContext;
import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.event.active.SendImgMsgByUidEvent;
import cn.lzxz1234.wxbot.event.active.SendImgMsgByUidEvent.Type;
import cn.lzxz1234.wxbot.task.EventListener;

public class SendImgMsgByUid extends EventListener<SendImgMsgByUidEvent> {

    @Override
    protected Event handleEnvent(SendImgMsgByUidEvent e,
            WXHttpClientContext context) throws Exception {

        String mid = this.uploadMedia(context, e);
        String msgId = String.valueOf(System.currentTimeMillis()) + RandomStringUtils.randomNumeric(4);
        
        JSONObject params = new JSONObject();
        JSONObject msg = new JSONObject();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("Msg", msg);
        msg.put("MediaId", mid);
        msg.put("FromUserName", context.getMyAccount().getUserName());
        msg.put("ToUserName", e.getUid());
        msg.put("LocalID", msgId);
        msg.put("ClientMsgId", msgId);
        URI uri = null;
        if(e.getType() == Type.JPG) {
            uri = new URIBuilder(context.getBaseUri() + "/webwxsendmsgimg?fun=async&f=json").build();
            msg.put("Type", 3);
        } else if(e.getType() == Type.GIF) {
            msg.put("Type", 47);
            msg.put("EmojiFlag", 2);
            uri = new URIBuilder(context.getBaseUri() + "/webwxsendemoticon?fun=sys").build();
        } else {
            throw new RuntimeException("不支持的图片类型：" + e.getType());
        }
        HttpPost post = new HttpPost(uri);
        post.setEntity(new StringEntity(params.toJSONString(), "UTF-8"));
        post.setHeader("content-type", "application/json; charset=UTF-8");
        
        CloseableHttpResponse resp = null;
        try {
            resp = this.execute(post, context);
            IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            log.debug(e.getUuid() + " 发送消息 " + e.getType() + " 返回");
        } finally {
            if(resp != null) resp.close();
        }
        return null;
    }

    private String uploadMedia(WXHttpClientContext context, SendImgMsgByUidEvent e) throws Exception {
        
        URI[] uris = new URI[] {
                new URIBuilder("https://file." + context.getBaseHost() + "/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json").build(), 
                new URIBuilder("https://file2." + context.getBaseHost() + "/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json").build()
        };
        String fileName = System.currentTimeMillis() + "." + e.getType().name().toLowerCase();
        
        JSONObject mediaRequest = new JSONObject();
        mediaRequest.put("BaseRequest", context.getBaseRequest());
        mediaRequest.put("ClientMediaId", System.currentTimeMillis() / 1000);
        mediaRequest.put("TotalLen", e.getFile().length);
        mediaRequest.put("StartPos", 0);
        mediaRequest.put("DataLen", e.getFile().length);
        mediaRequest.put("MediaType", 4);
        
        HttpEntity entity = MultipartEntityBuilder.create()
                .addTextBody("id", "WU_FILE_0")
                .addTextBody("name", fileName)
                .addTextBody("type", e.getType().getMimeType())
                .addTextBody("lastModifiedDate", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()) + " GMT+0800 (CST)")
                .addTextBody("size", "" + e.getFile().length)
                .addTextBody("mediatype", "pic")
                .addTextBody("uploadmediarequest", mediaRequest.toJSONString())
                .addTextBody("webwx_data_ticket", context.getCookieValue("webwx_data_ticket"))
                .addTextBody("pass_ticket", context.getPassTicket())
                .addBinaryBody("filename", e.getFile(), ContentType.create(e.getType().getMimeType()), fileName)
                .build();
        CloseableHttpResponse resp = null;
        for(URI uri : uris)
            try {
                HttpPost post = new HttpPost(uri);
                post.setEntity(entity);
                resp = this.execute(post, context);
                JSONObject result = JSON.parseObject(IOUtils.toString(resp.getEntity().getContent(), "UTF-8"));
                if(result.getJSONObject("BaseResponse").getIntValue("Ret") == 0)
                    return result.getString("MediaId");
            } finally {
                if(resp != null) resp.close();
            }
        throw new RuntimeException("上传文件失败");
    }
    
}
