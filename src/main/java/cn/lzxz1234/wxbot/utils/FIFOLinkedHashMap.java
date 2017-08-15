package cn.lzxz1234.wxbot.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class FIFOLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -2528998181145341846L;
    
    public final int MAX_ENTRIES;
    
    public FIFOLinkedHashMap(int maxEntries) {
        this.MAX_ENTRIES = maxEntries;
    }
    
    protected boolean removeEldestEntry(Map.Entry<K,  V> eldest) {
        return size() > MAX_ENTRIES;
    }

    public synchronized V putIfAbsent(K key, V value) {
        
        V previous = this.get(key);
        if(previous == null)
            this.put(key, value);
        return previous;
    }
    
}
