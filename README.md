## WxBot

Java 版网页微信接口，Web微信协议参考资料：

[挖掘微信Web版通信的全过程](http://www.tanhao.me/talk/1466.html/)

[微信协议简单调研笔记](http://www.blogjava.net/yongboy/archive/2015/11/05/410636.html)

[qwx: WeChat Qt frontend 微信Qt前端](https://github.com/xiangzhai/qwx)

### 调用示例参见 TulingTest.java

    String uuid = WXUtils.genUUID(null); // 开启一个微信会话
    WXUtils.registEventListener(ContactMessageEvent.class, TulingTest.class); // 注册感兴趣的事件
    File temp = File.createTempFile(uuid, ".png");
    FileOutputStream os = new FileOutputStream(temp);
    WXUtils.genQrCode(uuid, os);// 生成二维码扫描登录
    os.close();
    System.out.println(temp.getAbsolutePath());
