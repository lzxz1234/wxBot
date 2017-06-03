package me.lzxz1234.wxbot.task.active;


import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.WXUtils;
import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.SendMsgByUidEvent;
import me.lzxz1234.wxbot.event.SendMsgEvent;
import me.lzxz1234.wxbot.task.EventListener;

public class SendMsg extends EventListener<SendMsgEvent> {

    private String getUserId(WXHttpClientContext context, String userName) {
        
        if(StringUtils.isEmpty(userName)) return null;
        for(JSONObject contact : WXUtils.getContact(context.getUuid()).getContactList()) {
            if(contact.containsKey("RemarkName") && contact.getString("RemarkName").equals(userName))
                return contact.getString("RemarkName");
            if(contact.containsKey("NickName") && contact.getString("NickName").equals(userName))
                return contact.getString("NickName");
            if(contact.containsKey("DisplayName") && contact.getString("DisplayName").equals(userName))
                return contact.getString("DisplayName");
        }
        for(JSONObject group : WXUtils.getContact(context.getUuid()).getGroupList()) {
            if(group.containsKey("RemarkName") && group.getString("RemarkName").equals(userName))
                return group.getString("RemarkName");
            if(group.containsKey("NickName") && group.getString("NickName").equals(userName))
                return group.getString("NickName");
            if(group.containsKey("DisplayName") && group.getString("DisplayName").equals(userName))
                return group.getString("DisplayName");
        }
        return null;
    }

    @Override
    public Event handleEnvent(SendMsgEvent e, WXHttpClientContext context)
            throws Exception {
        
        String dst = e.getDst();
        String word = e.getWord();
        String uuid = e.getUuid();
        String uid = this.getUserId(context, dst);
        if(uid != null)
            return new SendMsgByUidEvent(uuid, uid, word);
        else 
            log.error(uuid + " 消息发送失败，用户 " + dst + " 不存在");
        return null;
    }

}
