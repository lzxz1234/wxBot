package cn.lzxz1234.wxbot.store;

import cn.lzxz1234.wxbot.context.WXContactInfo;
import cn.lzxz1234.wxbot.context.WXHttpClientContext;

public abstract class Store {

    public abstract void saveContext(WXHttpClientContext context);
    
    public abstract WXHttpClientContext getContext(String uuid);
    
    public abstract void saveContact(WXContactInfo contact);
    
    public abstract WXContactInfo getContact(String uuid);
    
}
