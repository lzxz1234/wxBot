package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class RecommendInfo implements Serializable {

    private static final long serialVersionUID = -5672879075657010310L;
    @JSONField(name="NickName") private String nickName;
    @JSONField(name="Province") private String province;
    @JSONField(name="City") private String city;
    @JSONField(name="Scene") private int scene;
    @JSONField(name="QQNum") private String qq;
    @JSONField(name="Content") private String content;
    @JSONField(name="Alias") private String alias;
    @JSONField(name="UserName") private String userName;
    @JSONField(name="Ticket") private String ticket;
    @JSONField(name="Sex") private int sex;
    
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getTicket() {
        return ticket;
    }
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getGender() {
        return new String[] {"unknown", "male", "female"} [this.sex];
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getScene() {
        return scene;
    }
    public void setScene(int scene) {
        this.scene = scene;
    }
    public String getQq() {
        return qq;
    }
    public void setQq(String qq) {
        this.qq = qq;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    
}
