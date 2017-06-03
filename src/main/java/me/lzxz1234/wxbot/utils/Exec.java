package me.lzxz1234.wxbot.utils;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Exec {

    private static final int SIZE = 400;
    private static final Worker[] threads = new Worker[SIZE + 1];
    private static final ErrorListener listener;
    
    static {
        
        listener = new DefaultErrorListener();
        for(int i = 0; i <= SIZE; i ++) {
            threads[i] = new Worker(i);
            threads[i].start();
        }
    }
    
    /**
     * 提交任务，可以保证相当 hashKey 传入的任务按严格时间顺序执行
     * @param hashKey
     * @param run
     */
    public static void submit(String hashKey, Runnable run) {
        
        if(StringUtils.isEmpty(hashKey)) 
            threads[SIZE].submit(hashKey, run);
        else
            threads[(int)(Math.abs(hashKey.hashCode()) % SIZE)].submit(hashKey, run);
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
        private final String MY_NAME_PREFIX;
        private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        
        public Worker(int id) {
            
            this.MY_NAME_PREFIX = GLOBAL_NAME_PREFIX + id + "-";
        }
        
        @Override
        public void run() {
            
            while(true) {
                Runnable target = take();
                try {
                    if(target != null) target.run();
                } catch (Exception e) {
                    listener.onEvent(e);
                }
            }
        }
        public void submit(String taskName, Runnable run) {
            this.queue.add(run);
            this.setName(this.MY_NAME_PREFIX + taskName);
        }
        private Runnable take() {
            while(true) {
                try {
                    return queue.take();
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }
}
