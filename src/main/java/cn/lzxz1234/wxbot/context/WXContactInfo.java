package cn.lzxz1234.wxbot.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import cn.lzxz1234.wxbot.vo.GroupMember;
import cn.lzxz1234.wxbot.vo.Member;
import cn.lzxz1234.wxbot.vo.NormalMember;

public class WXContactInfo implements Serializable {

    private static final long serialVersionUID = 2488496462459062123L;

    private static final Set<String> SPECIAL_USERS = new HashSet<String>(Arrays.asList(
            "newsapp", "fmessage", "filehelper", "weibo", "qqmail",
            "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle",
            "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp",
            "blogapp", "facebookapp", "masssendapp", "meishiapp",
            "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder",
            "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c",
            "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11",
            "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages"));
    
    private String uuid;
    private List<JSONObject> memberList;
    private JSONObject groupMembers;
    
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public List<JSONObject> getMemberList() {
        return memberList;
    }
    public JSONObject getGroupMembers() {
        return groupMembers;
    }
    public void setGroupMembers(JSONObject groupMembers) {
        this.groupMembers = groupMembers;
    }
    public void setMemberList(List<JSONObject> memberList) {
        this.memberList = memberList;
    }
    
    @JSONField(serialize=false)
    public List<JSONObject> getPublicList() {
        
        List<JSONObject> result = new ArrayList<JSONObject>();
        if(memberList != null)
            for(JSONObject contact : this.memberList)
                if((contact.getIntValue("VerifyFlag") & 8) != 0) // 公众号
                    result.add(contact);
        return result;
    }
    @JSONField(serialize=false)
    public List<JSONObject> getSpecialList() {
        
        List<JSONObject> result = new ArrayList<JSONObject>();
        if(memberList != null)
            for(JSONObject contact : this.memberList)
                if(SPECIAL_USERS.contains(contact.getString("UserName"))) // 特殊账户
                    result.add(contact);
        return result;
    }
    @JSONField(serialize=false)
    public List<JSONObject> getGroupList() {
        
        List<JSONObject> result = new ArrayList<JSONObject>();
        if(memberList != null)
            for(JSONObject contact : this.memberList)
                if(contact.getString("UserName").indexOf("@@") != -1) // 群聊
                    result.add(contact);
        return result;
    }
    @JSONField(serialize=false)
    public List<JSONObject> getContactList() {
        
        List<JSONObject> result = new ArrayList<JSONObject>();
        if(memberList != null)
            for(JSONObject contact : this.memberList)
                if((contact.getIntValue("VerifyFlag") & 8) == 0
                && !SPECIAL_USERS.contains(contact.getString("UserName"))
                && contact.getString("UserName").indexOf("@@") == -1)
                    result.add(contact);
        return result;
    }
    @JSONField(serialize=false)
    public NormalMember getNormalMember() {
        
        NormalMember result = new NormalMember();
        for(JSONObject contact : this.getMemberList()) {
            String userName = contact.getString("UserName");
            if((contact.getIntValue("VerifyFlag") & 8) != 0) { // 公众号
                result.put(userName, new Member("public", contact));
            } else if(SPECIAL_USERS.contains(userName)) { // 特殊账户
                result.put(userName, new Member("special", contact));
            } else if(userName.indexOf("@@") != -1) { // 群聊
                result.put(userName, new Member("group", contact));
            } else {
                result.put(userName, new Member("contact", contact));
            }
        }
        return result;
    }
    @JSONField(serialize=false)
    public GroupMember getGroupMember() {
        
        GroupMember result = new GroupMember();
        for(String gid : groupMembers.keySet()) {
            JSONArray members = groupMembers.getJSONArray(gid);
            for(int i = 0; i < members.size(); i ++) {
                JSONObject member = members.getJSONObject(i);
                result.put(member.getString("UserName"), new Member("group_member", member, gid));
            }
        }
        return result;
    }
}
