package cn.lzxz1234.wxbot.ioc;

public interface BeanFactory {

    public <T> T getBean(Class<T> clazz);
    
}
