package cn.lzxz1234.wxbot.context;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import cn.lzxz1234.wxbot.vo.Account;
import cn.lzxz1234.wxbot.vo.BaseRequest;
import cn.lzxz1234.wxbot.vo.Pair;
import cn.lzxz1234.wxbot.vo.SyncKey;

public class WXHttpClientContext extends BasicCookieStore implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final ThreadLocal<CloseableHttpClient> tl = new ThreadLocal<CloseableHttpClient>() {
        @Override
        protected CloseableHttpClient initialValue() {
            return HttpClients.custom()
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(10, false))
                    .build();
        }
    };
    
    private String status;
    private String uuid;
    private String baseHost;
    private String syncHost;
    private String baseUri;
    private String passTicket;
    private JSONObject encryChatRoomIds;
    private SyncKey syncKey;
    private BaseRequest baseRequest = new BaseRequest();
    private Account myAccount = new Account();
    private Date loginTime;
    private Date updateTime;
    private Map<String, Object> otherInfo;
    
    public CloseableHttpResponse execute(HttpUriRequest request) 
            throws Exception {
        
        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(this);
        return tl.get().execute(request, context);
    }
    
    public void close() throws Exception {
        
        tl.get().close();
        tl.remove();
    }
    
    @Override
    protected void finalize() throws Throwable {
        
        this.close();
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }
    public void setOtherInfo(Map<String, Object> otherInfo) {
        this.otherInfo = otherInfo;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @JSONField(serialize=false)
    public String getSyncKeyString() {
        
        StringBuilder syncKey = new StringBuilder();
        for(Pair each : this.syncKey.getList()) {
            syncKey.append(each.getKey());
            syncKey.append("_");
            syncKey.append(each.getVal());
            syncKey.append("|");
        }
        if(syncKey.length() > 0) syncKey.deleteCharAt(syncKey.length() - 1);
        return syncKey.toString();
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public JSONObject getEncryChatRoomIds() {
        return encryChatRoomIds;
    }
    public void setEncryChatRoomIds(JSONObject encryChatRoomIds) {
        this.encryChatRoomIds = encryChatRoomIds;
    }
    public SyncKey getSyncKey() {
        return syncKey;
    }
    public void setSyncKey(SyncKey syncKey) {
        this.syncKey = syncKey;
    }
    public String getSyncHost() {
        return syncHost;
    }
    public void setSyncHost(String syncHost) {
        this.syncHost = syncHost;
    }
    public String getBaseHost() {
        return baseHost;
    }
    public void setBaseHost(String baseHost) {
        this.baseHost = baseHost;
    }
    public String getBaseUri() {
        return baseUri;
    }
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
    public String getPassTicket() {
        return passTicket;
    }
    public void setPassTicket(String passTicket) {
        this.passTicket = passTicket;
    }
    public BaseRequest getBaseRequest() {
        return baseRequest;
    }
    public void setBaseRequest(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }
    public Account getMyAccount() {
        return myAccount;
    }
    public void setMyAccount(Account myCount) {
        this.myAccount = myCount;
    }
    public synchronized List<Cookie> getCookies() {
        
        return super.getCookies();
    }
    public Date getLoginTime() {
        return loginTime;
    }
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public void setCookies(List<JSONObject> cookies) {

        if(cookies != null)
            for(JSONObject cookie : cookies) {

                BasicClientCookie e = new BasicClientCookie(cookie.getString("name"), cookie.getString("value"));
                e.setExpiryDate(new Date(System.currentTimeMillis() + 7 * 24 * 3600000));
                e.setDomain(cookie.getString("domain"));
                e.setPath(cookie.getString("path"));
                e.setSecure(cookie.getBooleanValue("secure"));
                e.setVersion(cookie.getIntValue("version"));
                e.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
                this.addCookie(e);
            }
    }
    public String toString() {
        
        return JSON.toJSONString(this);
    }
}
