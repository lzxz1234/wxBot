package cn.lzxz1234.wxbot.event.message;

import cn.lzxz1234.wxbot.vo.RecommendInfo;

public class UserAddEvent extends MessageEvent {

    private RecommendInfo recommendInfo;
    
    public UserAddEvent() {
        
    }
    
    public UserAddEvent(String uuid) {
        
        super(uuid);
    }

    public RecommendInfo getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(RecommendInfo recommendInfo) {
        this.recommendInfo = recommendInfo;
    }
    
}
