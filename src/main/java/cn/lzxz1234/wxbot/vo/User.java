package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class User implements Serializable {

    private static final long serialVersionUID = 654357447686704101L;
    private String id;
    private Name name;
    
    public User() {
    }
    public User(String id, Name name) {
        super();
        this.id = id;
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Name getName() {
        return name;
    }
    public void setName(Name name) {
        this.name = name;
    }
    
    public String getContactPreferName() {
        
        if(name == null) return null;
        if(StringUtils.isNotEmpty(name.getRemarkName())) 
            return name.getRemarkName();
        if(StringUtils.isNotEmpty(name.getNickName())) 
            return name.getNickName();
        if(StringUtils.isNotEmpty(name.getDisplayName())) 
            return name.getDisplayName();
        return null;
    }
    
    public String getGroupMemberPreferName() {
        
        if(name == null) return null;
        if(StringUtils.isNotEmpty(name.getRemarkName())) 
            return name.getRemarkName();
        if(StringUtils.isNotEmpty(name.getDisplayName())) 
            return name.getDisplayName();
        if(StringUtils.isNotEmpty(name.getNickName())) 
            return name.getNickName();
        return null;
    }
    
}
