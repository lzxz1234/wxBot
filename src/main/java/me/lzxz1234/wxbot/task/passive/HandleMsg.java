package me.lzxz1234.wxbot.task.passive;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.BatchEvent;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.message.ContactMessageEvent;
import me.lzxz1234.wxbot.event.message.ContentMessageEvent;
import me.lzxz1234.wxbot.event.message.FileHelperMessageEvent;
import me.lzxz1234.wxbot.event.message.GroupMessageEvent;
import me.lzxz1234.wxbot.event.message.MessageEvent;
import me.lzxz1234.wxbot.event.message.PublicMessageEvent;
import me.lzxz1234.wxbot.event.message.SelfMessageEvent;
import me.lzxz1234.wxbot.event.message.SpecialMessageEvent;
import me.lzxz1234.wxbot.event.message.UserAddEvent;
import me.lzxz1234.wxbot.event.system.HandleMsgEvent;
import me.lzxz1234.wxbot.task.EventListener;
import me.lzxz1234.wxbot.utils.HtmlUtils;
import me.lzxz1234.wxbot.vo.Content;
import me.lzxz1234.wxbot.vo.Info;
import me.lzxz1234.wxbot.vo.RecommendInfo;
import me.lzxz1234.wxbot.vo.ShareData;
import me.lzxz1234.wxbot.vo.User;

public class HandleMsg extends EventListener<HandleMsgEvent> {

    @Override
    public Event handleEnvent(HandleMsgEvent e, WXHttpClientContext context)
            throws Exception {
        
        List<Event> result = new ArrayList<Event>();
        JSONArray msgList = e.getRawMsg().getJSONArray("AddMsgList");
        for(int i = 0; i < msgList.size(); i ++) {
            MessageEvent event;
            JSONObject msg = msgList.getJSONObject(i);
            String fromUid = msg.getString("FromUserName");
            String content = HtmlUtils.htmlUnescape(msg.getString("Content"));
            
            if(msg.getIntValue("MsgType") == 37) { // 好友请求
                
                event = new UserAddEvent(context.getUuid());
                ((UserAddEvent)event).setRecommendInfo(msg.getObject("RecommendInfo", RecommendInfo.class));
            } else if(fromUid.equals(context.getMyAccount().getUserName())) { // Self
                
                event = new SelfMessageEvent(context.getUuid());
                event.getUser().setName("self");
            } else if(msg.getString("ToUserName").equals("filehelper")) { // File Helper
                
                event = new FileHelperMessageEvent(context.getUuid());
                ((FileHelperMessageEvent)event).setData(content.replace("<br/>", "\n"));
                event.getUser().setName("file_helper");
            } else if(fromUid.startsWith("@@")) { // Group
                
                event = new GroupMessageEvent(context.getUuid());
                int spIndex = content.indexOf("<br/>");
                String uid = content.substring(0, spIndex);
                content = content.substring(spIndex);;
                content = content.replace("<br/>", "");
                uid = uid.substring(0, uid.length() - 1);
                String name = this.getContactPreferName(this.getContactName(context, uid));
                if(StringUtils.isEmpty(name))
                    name = this.getGroupMemberPreferName(this.getGroupMemberName(context, msg.getString("FromUserName"), uid));
                if(StringUtils.isEmpty(name))
                    name = "unknown";
                ((GroupMessageEvent)event).setAtUser(new User(uid, name));
                event.getUser().setName(this.getContactPreferName(this.getContactName(context, fromUid)));
            } else if(this.isContact(context, fromUid)) { // Contact
                
                event = new ContactMessageEvent(context.getUuid());
                event.getUser().setName(this.getContactPreferName(this.getContactName(context, fromUid)));
            } else if(this.isPublic(context, fromUid)) { // Public
                
                event = new PublicMessageEvent(context.getUuid());
                event.getUser().setName(this.getContactPreferName(this.getContactName(context, fromUid)));
            } else if(this.isSpecial(context, fromUid)) { // Special
                
                event = new SpecialMessageEvent(context.getUuid());
                event.getUser().setName(this.getContactPreferName(this.getContactName(context, fromUid)));
            } else {
                log.warn("[UnknownMessage] " + e.getRawMsg().toJSONString());
                continue;
            }
            if(StringUtils.isEmpty(event.getUser().getName())) event.getUser().setName("unknown");
            event.getUser().setName(HtmlUtils.htmlUnescape(event.getUser().getName()));
            event.getUser().setId(fromUid);
            event.setMsgId(msg.getString("MsgId"));
            event.setToUserId(msg.getString("ToUserName"));
            
            if(event instanceof ContentMessageEvent) 
                ((ContentMessageEvent)event).setContent(this.extractMsgContent(context, msg));
            result.add(event);
        }
        return new BatchEvent(context.getUuid(), result.toArray(new Event[0]));
    }

    private Content extractMsgContent(WXHttpClientContext context, JSONObject msg) throws Exception {
        
        String uuid = context.getUuid();
        Content result = new Content();
        int mType = msg.getIntValue("MsgType");
        String msgId = msg.getString("MsgId");
        String content = HtmlUtils.htmlUnescape(msg.getString("Content"));
        
        if(mType == 1) {
            if(content.indexOf("http://weixin.qq.com/cgi-bin/redirectforward?args=") != -1) {
                HttpGet get = new HttpGet(content);
                CloseableHttpResponse resp = this.execute(get, context);
                try {
                    String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
                    result.setType(1);
                    result.setData(this.searchContent("title", data, "xml"));
                    result.setDetail(data);
                    log.debug(uuid + " [Location] " + result.getData());
                } finally {
                    resp.close();
                }
            } else {
                result.setType(0);
                if(msg.getString("FromUserName").startsWith("@@") 
                        || (msg.getString("FromUserName").equals(context.getMyAccount().getUserName()) && msg.getString("ToUserName").startsWith("@@")) ) { // Group Text Message
                    Object[] msgInfos = this.procAtInfo(content);
                    result.setData(msgInfos[0].toString());
                    result.setDetail(msgInfos[2]);
                    result.setDesc(msgInfos[1].toString());
                } else {
                    result.setData(content);
                }
                log.debug(uuid + " [Text] " + result.getData());
            }
        } else if(mType == 3) {
            result.setType(3);
            result.setData(this.getMsgImgUrl(context, msgId));
            // TODO 是否需要下载图片
            // msg_content['img'] = self.session.get(msg_content['data']).content.encode('hex')
            log.debug(uuid + " [Image] " + result.getData());
        } else if(mType == 34) {
            result.setType(4);
            result.setData(this.getVoiceUrl(context, msgId));
            // TODO 是否需要下载音频
            // msg_content['voice'] = self.session.get(msg_content['data']).content.encode('hex')
            log.debug(uuid + " [Voice] " + result.getData());
        } else if(mType == 42) { // 共享名片
            result.setType(5);
            result.setRecommendInfo(msg.getObject("RecommendInfo", RecommendInfo.class));
            log.debug(uuid + " [UserAdd] " + result.getRecommendInfo().getNickName());
        } else if(mType == 47) { // 动画表情
            result.setType(6);
            result.setData(this.searchContent("cdnurl", content, "attr"));
            log.debug(uuid + " [Animation] " + result.getData());
        } else if(mType == 49) { // Share
            result.setType(7);
            ShareData shareData = new ShareData();
            if(msg.getIntValue("AppMsgType") == 3) 
                shareData.setType("music");
            else if(msg.getIntValue("AppMsgType") == 5) 
                shareData.setType("link");
            else if(msg.getIntValue("AppMsgType") == 7) 
                shareData.setType("weibo");
            else
                shareData.setType("unknown");
            shareData.setTitle(msg.getString("FileName"));
            shareData.setDesc(this.searchContent("des", content, "xml"));
            shareData.setUrl(msg.getString("Url"));
            shareData.setFrom(this.searchContent("appname", content, "xml"));
            shareData.setContent(msg.getString("Content"));
            result.setShareData(shareData);
            log.debug(uuid + " [Share] " + shareData);
        } else if(mType == 62) { // Video
            result.setType(8);
            result.setData(content);
            log.debug(uuid + " [Share] " + result.getData());
        } else if(mType == 53) {
            result.setType(9);
            result.setData(content);
            log.debug(uuid + " [Video Call] " + result.getData());
        } else if(mType == 10002) {
            result.setType(10);
            result.setData(content);
            log.debug(uuid + " [Redraw] " + result.getData());
        } else if(mType == 10000) { //unknown, maybe red packet, or group invite, or add friend, system notice
            result.setType(12);
            result.setData(msg.getString("Content"));
            log.debug(uuid + " [Unknown] " + result.getData());
        } else if(mType == 43) {
            result.setType(13);
            result.setData(this.getVideoUrl(context, msgId));
            log.debug(uuid + " [Video] " + result.getData());
        } else {
            result.setType(99);
            result.setData(content);
            log.debug(uuid + " [Unknown] " + result.getData());
        }
        return result;
    }
    
    private String getVideoUrl(WXHttpClientContext context, String msgId) throws Exception {
        
        return new URIBuilder(context.getBaseUri() + "/webwxgetvideo")
                .addParameter("msgid", msgId)
                .addParameter("skey", context.getBaseRequest().getSkey())
                .build().toString();
    }
    
    private String getMsgImgUrl(WXHttpClientContext context, String msgId) throws Exception {
        
        return new URIBuilder(context.getBaseUri() + "/webwxgetmsgimg")
                .addParameter("MsgID", msgId)
                .addParameter("skey", context.getBaseRequest().getSkey())
                .build().toString();
    }
    
    private String getVoiceUrl(WXHttpClientContext context, String msgId) throws Exception {
        
        return new URIBuilder(context.getBaseUri() + "/webwxgetvoice")
                .addParameter("msgid", msgId)
                .addParameter("skey", context.getBaseRequest().getSkey())
                .build().toString();
    }
    
    public Object[] procAtInfo(String msg) {
        
        String[] segs = msg.split("\u2005");
        String strMsgAll = "", strMsg = "";
        List<Info> infos = new ArrayList<Info>();
        if(segs.length > 0) {
            for(int i = 0; i < segs.length - 1; i ++) {
                Matcher matcher = Pattern.compile("@.*\u2005").matcher(segs[i]);
                if(matcher.find() ) {
                    String pm = matcher.group();
                    String name = pm.substring(1, pm.length() - 1);
                    String string = segs[i].replace(pm, "");
                    strMsgAll += string + "@" + name + " ";
                    strMsg += string;
                    if(StringUtils.isNotEmpty(string))
                        infos.add(new Info("str", string));
                    infos.add(new Info("at", name));
                } else {
                    infos.add(new Info("str", segs[i]));
                    strMsgAll += segs[i];
                    strMsg += segs[i];
                }
            }
            strMsgAll += segs[segs.length - 1];
            strMsg += segs[segs.length - 1];
            infos.add(new Info("str", segs[segs.length - 1]));
        } else {
            infos.add(new Info("str", segs[segs.length - 1]));
            strMsgAll = msg;
            strMsg = msg;
        }
        return new Object[] {strMsgAll.replace("\u2005", ""), strMsg.replace("\u2005", ""), infos.toArray(new Info[0])};
    }

}
