package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class Info implements Serializable {

    private static final long serialVersionUID = -1265616329879588176L;
    private String type;
    private String value;
    
    public Info() {
    }

    public Info(String type, String value) {
        
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
