package cn.lzxz1234.wxbot.task.passive;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import cn.lzxz1234.wxbot.context.WXHttpClientContext;
import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.event.system.LoginEvent;
import cn.lzxz1234.wxbot.event.system.Wait4LoginEvent;
import cn.lzxz1234.wxbot.task.EventListener;
import cn.lzxz1234.wxbot.utils.Lang;


public class Wait4Login extends EventListener<Wait4LoginEvent> {

    @Override
    public Event handleEnvent(Wait4LoginEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        URI uri = new URIBuilder("https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login")
                .addParameter("tip", e.getTip())
                .addParameter("uuid", uuid)
                .addParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();

        HttpGet get = new HttpGet(uri);
        CloseableHttpResponse resp = this.execute(get, context);
        try {
            
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            Pattern pattern = Pattern.compile("window.code=(\\d+);");
            Matcher matcher = pattern.matcher(data);
            if(matcher.find()) {
                String code = matcher.group(1);
                if("201".equals(code)) {
                    log.debug(uuid + " 已扫码，待确认");
                    context.setStatus("wait4confirm");
                    return new Wait4LoginEvent("0", uuid, e.getRetry());
                } else if("200".equals(code)) {
                    log.debug(uuid + " 登录成功");
                    context.setStatus("confirmed");
                    pattern = Pattern.compile("window.redirect_uri=\"(\\S+?)\";");
                    matcher = pattern.matcher(data);
                    if(matcher.find()) {
                        String redirectURI = matcher.group(1) + "&fun=new";
                        context.setBaseUri(redirectURI.substring(0, redirectURI.lastIndexOf("/")));
                        String tempHost = context.getBaseUri().substring(8);
                        context.setBaseHost(tempHost.substring(0, tempHost.indexOf("/")));
                        log.debug(uuid + " 重定向到登录地址：" + redirectURI);
                        return new LoginEvent(uuid, redirectURI);
                    } else {
                        context.setStatus("loginout");
                    }
                } else if(e.getRetry() > 0) {
                    log.debug(uuid + " 登录失败待重试，状态码：" + code);
                    Lang.sleep(1000);
                    return new Wait4LoginEvent("1", uuid, e.getRetry() - 1);
                } else {
                    log.debug(uuid + " 登录失败");
                    context.setStatus("timeout");
                }
            }
        } finally {
            resp.close();
        }
        return null;
    }

}
