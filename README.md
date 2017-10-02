## WxBot

Java 版网页微信接口，Web微信协议参考资料：

[littlecodersh/ItChat](https://github.com/littlecodersh/ItChat) 微信个人号接口、微信机器人及命令行微信，Command line talks through Wechat

[Urinx/WeixinBot](https://github.com/Urinx/WeixinBot) 网页版微信API，包含终端版微信及微信机器人

[zixia/wechaty](https://github.com/zixia/wechaty) Wechaty is wechat for bot in Javascript(ES6). It's a Personal Account Robot Framework/Library.

### 调用示例参见 TulingTest.java

    String uuid = WXUtils.genUUID(null); // 开启一个微信会话
    WXUtils.registEventListener(ContactMessageEvent.class, TulingTest.class); // 注册感兴趣的事件
    File temp = File.createTempFile(uuid, ".png");
    FileOutputStream os = new FileOutputStream(temp);
    WXUtils.genQrCode(uuid, os);// 生成二维码扫描登录
    os.close();
    System.out.println(temp.getAbsolutePath());
