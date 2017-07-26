package cn.lzxz1234.wxbot.vo;

import java.io.Serializable;

public class Content implements Serializable {

    private static final long serialVersionUID = -7968713548292626071L;
    /**
     * 0 文本
     * 1 坐标
     * 3 图片
     * 4 音频
     */
    private int type;
    private String data;
    private String desc;
    private Object detail;
    
    /**
     * type 为 7 时有效
     */
    private ShareData shareData;
    /**
     * type 为37 和 42时有效
     */
    private RecommendInfo recommendInfo;
    
    public RecommendInfo getRecommendInfo() {
        return recommendInfo;
    }
    public void setRecommendInfo(RecommendInfo recommendInfo) {
        this.recommendInfo = recommendInfo;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Object getDetail() {
        return detail;
    }
    public void setDetail(Object detail) {
        this.detail = detail;
    }
    public ShareData getShareData() {
        return shareData;
    }
    public void setShareData(ShareData shareData) {
        this.shareData = shareData;
    }
    
}
