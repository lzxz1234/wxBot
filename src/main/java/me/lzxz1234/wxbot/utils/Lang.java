package me.lzxz1234.wxbot.utils;

@SuppressWarnings("unchecked")
public class Lang {

    public static void sleep(long i) {

        try {
            Thread.sleep(i);
        } catch (Exception e) {
            // pass
        }
    }

    public static <T> T newInstance(Class<?> clazz) {

        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {

            throw new RuntimeException("新建失败", e);
        }
    }
}
