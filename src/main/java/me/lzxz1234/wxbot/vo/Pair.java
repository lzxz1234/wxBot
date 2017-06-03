package me.lzxz1234.wxbot.vo;

import com.alibaba.fastjson.annotation.JSONField;

public class Pair {

    @JSONField(name="Key") private String key;
    @JSONField(name="Val")private String val;
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }
    
}
