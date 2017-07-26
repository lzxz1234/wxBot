package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class Name implements Serializable {

    private static final long serialVersionUID = 8814633135972755042L;
    
    public static Name SYSTEM = new Name("system");
    public static Name SELF = new Name("self");
    public static Name FILE_HELPER = new Name("file_helper");
    public static Name UNKNOWN = new Name("unknown");
    
    private String remarkName;
    private String nickName;
    private String displayName;
    
    public Name() {}
    
    public Name(String name) {
        
        this.setDisplayName(name);
        this.setNickName(name);
        this.setRemarkName(name);
    }
    
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
