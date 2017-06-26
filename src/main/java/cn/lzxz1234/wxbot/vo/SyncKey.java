package cn.lzxz1234.wxbot.vo;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class SyncKey {

    @JSONField(name="List") private List<Pair> list;
    @JSONField(name="Count") private int count;
    
    public List<Pair> getList() {
        return list;
    }
    public void setList(List<Pair> list) {
        this.list = list;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
