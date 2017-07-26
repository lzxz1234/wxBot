package cn.lzxz1234.wxbot.ioc;

import cn.lzxz1234.wxbot.utils.Lang;

public class SimpleBeanFactory implements BeanFactory {

    @Override
    public <T> T getBean(Class<T> clazz) {
        
        return Lang.newInstance(clazz);
    }

}
