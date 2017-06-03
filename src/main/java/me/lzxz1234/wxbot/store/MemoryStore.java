package me.lzxz1234.wxbot.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.lzxz1234.wxbot.context.WXContactInfo;
import me.lzxz1234.wxbot.context.WXHttpClientContext;

public class MemoryStore extends Store {

    private Map<String, WXHttpClientContext> contexts = new ConcurrentHashMap<>();
    private Map<String, WXContactInfo> contacts = new ConcurrentHashMap<>();
    
    @Override
    public void saveContext(WXHttpClientContext context) {
        
        contexts.put(context.getUuid(), context);
    }

    @Override
    public WXHttpClientContext getContext(String uuid) {

        return contexts.get(uuid);
    }

    @Override
    public void saveContact(WXContactInfo contact) {
        
        contacts.put(contact.getUuid(), contact);
    }

    @Override
    public WXContactInfo getContact(String uuid) {

        return contacts.get(uuid);
    }

}
