package me.lzxz1234.wxbot.task.passive;

import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.InitEvent;
import me.lzxz1234.wxbot.event.LoginEvent;
import me.lzxz1234.wxbot.task.EventListener;


public class Login extends EventListener<LoginEvent> {

    @Override
    public Event handleEnvent(LoginEvent e, WXHttpClientContext context)
            throws Exception {
        
        String uuid = e.getUuid();
        HttpGet get = new HttpGet(e.getUrl());
        CloseableHttpResponse resp = this.execute(get, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(new InputSource(new StringReader(data)));
            
            Node root = xmlDoc.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            for(int i = 0; i < childNodes.getLength(); i ++) {
                Node node = childNodes.item(i);
                if(node.getNodeName().equals("skey")) {
                    String skey = node.getChildNodes().item(0).getNodeValue();
                    context.getBaseRequest().setSkey(skey);;
                } else if(node.getNodeName().equals("wxsid")) {
                    String sid = node.getChildNodes().item(0).getNodeValue();
                    context.getBaseRequest().setSid(sid);;
                } else if(node.getNodeName().equals("wxuin")) {
                    String uin = node.getChildNodes().item(0).getNodeValue();
                    context.getBaseRequest().setUin(uin);
                } else if(node.getNodeName().equals("pass_ticket")) {
                    String passTicket = node.getChildNodes().item(0).getNodeValue();
                    context.setPassTicket(passTicket);
                }
            }
            context.setLoginTime(new Date());
            log.debug(uuid + " 登录成功，待初始化");
            return new InitEvent(uuid);
        } finally {
            resp.close();
        }
    }

}
