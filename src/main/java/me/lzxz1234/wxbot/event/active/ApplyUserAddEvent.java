package me.lzxz1234.wxbot.event.active;

import me.lzxz1234.wxbot.event.Event;
import me.lzxz1234.wxbot.vo.RecommendInfo;

public class ApplyUserAddEvent extends Event {

    private RecommendInfo recommendInfo;

    public ApplyUserAddEvent() {
        
    }
    public ApplyUserAddEvent(String uuid, RecommendInfo recommendInfo) {
        
        super(uuid);
        this.recommendInfo = recommendInfo;
    }

    public RecommendInfo getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(RecommendInfo recommendInfo) {
        this.recommendInfo = recommendInfo;
    }
    
    
}
