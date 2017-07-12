package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class Name implements Serializable {

    private static final long serialVersionUID = 8814633135972755042L;
    private String remarkName;
    private String nickName;
    private String displayName;
    
    public String getRemarkName() {
        return remarkName;
    }
    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
}
