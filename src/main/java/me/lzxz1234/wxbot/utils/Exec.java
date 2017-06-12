package me.lzxz1234.wxbot.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Exec {

    private static final ErrorListener listener = new DefaultErrorListener();
    private static final Map<String, Worker> workers = new ConcurrentHashMap<String, Worker>();
    
    /**
     * 提交任务，可以保证相当 threadId 传入的任务按严格时间顺序执行
     * @param threadId
     * @param run
     */
    public static void submit(String threadId, Runnable run) {
        
        if(StringUtils.isEmpty(threadId)) threadId = "";
        if(!workers.containsKey(threadId))
            workers.put(threadId, new Worker(threadId));
        workers.get(threadId).submit(threadId, run);
    }
    
    /**
     * 失败任务监听器
     */
    public static interface ErrorListener {
        public void onEvent(Exception e);
    }
    
    /**
     * 默认失败监听，只记录日志
     */
    private static final class DefaultErrorListener implements ErrorListener {
        
        private Logger log = Logger.getLogger(DefaultErrorListener.class);
        @Override
        public void onEvent(Exception e) {
            log.error("自定义线程池错误", e);
        }
        
    }
    
    private static final class Worker extends Thread {
        
        private static final String GLOBAL_NAME_PREFIX = "Exec-Worker-";
        private final String id;
        private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        
        public Worker(String id) {
            
            this.id = id;
        }
        
        @Override
        public void run() {
            
            while(true) {
                Runnable target = take();
                try {
                    if(target != null) { 
                        target.run();
                    } else {
                        Exec.workers.remove(id);
                        return;
                    }
                } catch (Exception e) {
                    listener.onEvent(e);
                }
            }
        }
        public void submit(String taskName, Runnable run) {
            this.queue.add(run);
            this.setName(GLOBAL_NAME_PREFIX + id + "-" + taskName);
        }
        private Runnable take() {
            
            while(true) {
                try {
                    return queue.poll(300, TimeUnit.SECONDS);
                } catch(InterruptedException e1) {
                    // pass
                }
            }
        }
    }
}
