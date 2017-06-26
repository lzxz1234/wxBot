package cn.lzxz1234.wxbot.vo;

import com.alibaba.fastjson.annotation.JSONField;

public class BaseRequest {

    @JSONField(name="DeviceID") private String deviceId;
    @JSONField(name="Skey") private String skey;
    @JSONField(name="Uin") private String uin;
    @JSONField(name="Sid") private String sid;
    
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getSkey() {
        return skey;
    }
    public void setSkey(String skey) {
        this.skey = skey;
    }
    public String getUin() {
        return uin;
    }
    public void setUin(String uin) {
        this.uin = uin;
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    
}
