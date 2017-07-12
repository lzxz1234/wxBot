package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class Member implements Serializable {

    private static final long serialVersionUID = 7076092920477086118L;
    private String type;
    private JSONObject info;
    private String groupId;
    
    public Member() {
    }
    
    public Member(String type, JSONObject info) {

        this.type = type;
        this.info = info;
    }
    
    public Member(String type, JSONObject info, String groupId) {

        this.type = type;
        this.info = info;
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }
    
}
