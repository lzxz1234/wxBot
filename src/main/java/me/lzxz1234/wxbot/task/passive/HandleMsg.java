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
import me.lzxz1234.wxbot.event.HandleMsgAllEvent;
import me.lzxz1234.wxbot.event.HandleMsgEvent;
import me.lzxz1234.wxbot.task.EventListener;
import me.lzxz1234.wxbot.utils.HtmlUtils;
import me.lzxz1234.wxbot.vo.Content;
import me.lzxz1234.wxbot.vo.Info;
import me.lzxz1234.wxbot.vo.Message;
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
            JSONObject msg = msgList.getJSONObject(i);
            String fromUid = msg.getString("FromUserName");
            
            User user = new User();
            Message realMsg = new Message();
            user.setId(fromUid);
            if(msg.getIntValue("MsgType") == 51 && msg.getIntValue("StatusNotifyCode") == 4) { // init message
                
                realMsg.setMsgTypeId(0);
                user.setName("system");
            } else if(msg.getIntValue("MsgType") == 37) { // 好友请求
                
                realMsg.setMsgTypeId(37);
            } else if(fromUid.equals(context.getMyAccount().getUserName())) { // Self
                
                realMsg.setMsgTypeId(1);
                user.setName("self");
            } else if(msg.getString("ToUserName").equals("filehelper")) { // File Helper
                
                realMsg.setMsgTypeId(2);
                user.setName("file_helper");
            } else if(fromUid.startsWith("@@")) { // Group
                
                realMsg.setMsgTypeId(3);
                user.setName(this.getContactPreferName(this.getContactName(context, user.getId())));
            } else if(this.isContact(context, fromUid)) { // Contact
                
                realMsg.setMsgTypeId(4);
                user.setName(this.getContactPreferName(this.getContactName(context, user.getId())));
            } else if(this.isPublic(context, fromUid)) { // Public
                
                realMsg.setMsgTypeId(5);
                user.setName(this.getContactPreferName(this.getContactName(context, user.getId())));
            } else if(this.isSpecial(context, fromUid)) { // Special
                
                realMsg.setMsgTypeId(6);
                user.setName(this.getContactPreferName(this.getContactName(context, user.getId())));
            } else {
                realMsg.setMsgTypeId(99);
                user.setName("unknown");
            }
            if(StringUtils.isEmpty(user.getName())) user.setName("unknown");
            user.setName(HtmlUtils.htmlUnescape(user.getName()));
            
            realMsg.setMsgId(msg.getString("MsgId"));
            realMsg.setToUserId(msg.getString("ToUserName"));
            realMsg.setUser(user);
            realMsg.setContent(this.extractMsgContent(context, realMsg.getMsgTypeId(), msg));
            
            result.add(new HandleMsgAllEvent(e.getUuid(), realMsg));
        }
        return new BatchEvent(context.getUuid(), result.toArray(new Event[0]));
    }

    private Content extractMsgContent(WXHttpClientContext context, int msgTypeId, JSONObject msg) throws Exception {
        
        String uuid = context.getUuid();
        Content result = new Content();
        int mType = msg.getIntValue("MsgType");
        String msgId = msg.getString("MsgId");
        String content = HtmlUtils.htmlUnescape(msg.getString("Content"));
        if(msgTypeId == 0) {
            result.setType(11);
            result.setData("");
            return result;
        } else if(msgTypeId == 2) { // File Helper
            result.setType(0);
            result.setData(content.replace("<br/>", "\n"));
            return result;
        } else if(msgTypeId == 3) { // 群聊
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
            result.setUser(new User(uid, name));
        }
        
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
                if(msgTypeId == 3 || (msgTypeId == 1 && msg.getString("ToUserName").startsWith("@@")) ) { // Group Text Message
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
        } else if(mType == 37) { // 好友确认消息
            result.setType(37);
            result.setRecommendInfo(msg.getObject("RecommendInfo", RecommendInfo.class));
            log.debug(uuid + " [UserAdd] " + result.getRecommendInfo().getNickName());
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
