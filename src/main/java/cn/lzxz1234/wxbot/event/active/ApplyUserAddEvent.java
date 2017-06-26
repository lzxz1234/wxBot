package cn.lzxz1234.wxbot.event.active;

import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.vo.RecommendInfo;

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
