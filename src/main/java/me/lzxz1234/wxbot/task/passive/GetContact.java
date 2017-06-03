package me.lzxz1234.wxbot.task.passive;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.lzxz1234.wxbot.WXUtils;
import me.lzxz1234.wxbot.context.WXContactInfo;
import me.lzxz1234.wxbot.context.WXHttpClientContext;
import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.event.GetContactEvent;
import me.lzxz1234.wxbot.task.EventListener;

public class GetContact extends EventListener<GetContactEvent> {

    @Override
    public Event handleEnvent(GetContactEvent e, WXHttpClientContext context)
            throws Exception {
        
        WXContactInfo contact = new WXContactInfo();
        contact.setUuid(e.getUuid());
        contact.setMemberList(this.getMemberist(context));
        context.setEncryChatRoomIds(new JSONObject());
        contact.setGroupMembers(this.getGroupMembers(context, contact));
        WXUtils.saveContact(contact);
        return null;
    }
    
    private JSONObject getGroupMembers(WXHttpClientContext context, WXContactInfo contact) throws Exception {
        
        JSONObject result = new JSONObject();
        URI uri = new URIBuilder(context.getBaseUri() + "/webwxbatchgetcontact")
                .addParameter("type", "ex")
                .addParameter("r", String.valueOf(System.currentTimeMillis() / 1000))
                .addParameter("pass_ticket", context.getPassTicket())
                .build();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("BaseRequest", context.getBaseRequest());
        params.put("Count", contact.getGroupList().size());
        params.put("List", this.extractGroupInfoList(contact));
        
        HttpPost post = new HttpPost(uri);
        post.setEntity(new StringEntity(JSON.toJSONString(params)));
        CloseableHttpResponse resp = this.execute(post, context);
        try {
            String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            JSONObject dict = JSON.parseObject(data);
            JSONArray groups = dict.getJSONArray("ContactList");
            for(int i = 0; i < groups.size(); i ++) {
                JSONObject group = groups.getJSONObject(i);
                String gid = group.getString("UserName");
                JSONArray members = group.getJSONArray("MemberList");
                result.put(gid, members);
                context.getEncryChatRoomIds().put(gid, group.getString("EncryChatRoomId"));
            }
        } finally {
            resp.close();
        }
        return result;
    }
    private JSONArray extractGroupInfoList(WXContactInfo context) {
        
        JSONArray list = new JSONArray();
        for(JSONObject group : context.getGroupList()) {
            JSONObject ele = new JSONObject();
            ele.put("UserName", group.getString("UserName"));
            ele.put("EncryChatRoomId", "");
            list.add(ele);
        }
        return list;
    }
    /**
     * 分批查询所有好友信息
     * @return
     * @throws Exception
     */
    private List<JSONObject> getMemberist(WXHttpClientContext context) throws Exception {
        
        List<JSONObject> result = new ArrayList<JSONObject>();
        String uuid = context.getUuid();
        List<JSONObject> dicList = new ArrayList<JSONObject>();
        int seq = 0;
        do {
            URI uri = new URIBuilder(context.getBaseUri() + "/webwxgetcontact")
                    .addParameter("seq", String.valueOf(seq))
                    .addParameter("pass_ticket", context.getPassTicket())
                    .addParameter("skey", context.getBaseRequest().getSkey())
                    .addParameter("r", String.valueOf(System.currentTimeMillis() / 1000))
                    .build();
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity("{}"));
            CloseableHttpResponse resp = this.execute(post, context);
            try {
                String data = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
                JSONObject dic = JSON.parseObject(data);
                dicList.add(dic);
                seq = dic.getIntValue("Seq");
                log.debug(uuid + " 获取好友列表，已获取 " + dic.getString("MemberCount"));
            } finally {
                resp.close();
            }
        } while(seq != 0);
        
        for(JSONObject dic : dicList) {
            JSONArray memberList = dic.getJSONArray("MemberList");
            for(int i = 0; i < memberList.size(); i ++)
                result.add(memberList.getJSONObject(i));
        }
        return result;
    }

}
