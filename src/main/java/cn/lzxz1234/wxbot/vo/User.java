package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 654357447686704101L;
    private String id;
    private String name;
    
    public User() {
    }
    public User(String id, String name) {
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
