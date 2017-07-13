package cn.lzxz1234.wxbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.lzxz1234.wxbot.context.WXHttpClientContext;
import cn.lzxz1234.wxbot.event.Event;
import cn.lzxz1234.wxbot.event.active.SendMsgByUidEvent;
import cn.lzxz1234.wxbot.event.message.ContactMessageEvent;
import cn.lzxz1234.wxbot.task.EventListener;
import sun.misc.BASE64Encoder;

public class TulingTest extends EventListener<ContactMessageEvent> {

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }
    
    static final String secret = "a0248164d692b143";
    static final String apiKey = "ed3390feb5e64d02bc74bafb79bf4c7a";
    
    @Override
    protected Event handleEnvent(ContactMessageEvent e,
            WXHttpClientContext context) throws Exception {
        
        if(StringUtils.isEmpty(e.getContent().getData())) return null;
        
        long timestamp = System.currentTimeMillis();
        String md5Key = this.getMD5(secret + timestamp + apiKey);
        
        Map<String, String> req = new HashMap<String, String>();
        req.put("key", apiKey);
        req.put("info", e.getContent().getData());
        
        JSONObject post = new JSONObject();
        post.put("key", apiKey);
        post.put("timestamp", timestamp);
        post.put("data", new Aes(md5Key).encrypt(JSON.toJSONString(req)));
        
        JSONObject resp = JSON.parseObject(sendPost(post.toJSONString(), "http://www.tuling123.com/openapi/api"));
        if(resp.getInteger("code") == 100000) 
            WXUtils.submit(new SendMsgByUidEvent(e.getUuid(), e.getUser().getId(), resp.getString("text")));
        else 
            System.out.println(resp);
        return null;
    }
    
    @Test
    public void test() throws Exception {
        
        WXUtils.registEventListener(ContactMessageEvent.class, TulingTest.class);
        
        String uuid = WXUtils.genUUID(null);
        File temp = File.createTempFile(uuid, ".png");
        FileOutputStream os = new FileOutputStream(temp);
        WXUtils.genQrCode(uuid, os);
        os.close();
        System.out.println(temp.getAbsolutePath());
        Thread.sleep(100000000L);
    }
    
    private String getMD5(String sourceStr) throws NoSuchAlgorithmException {
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(sourceStr.getBytes());
        
        byte[] b = md.digest();
        StringBuffer buf = new StringBuffer();
        for (int offset = 0; offset < b.length; offset++) {
            int i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    public static class Aes {
        
        private Key key;
        private IvParameterSpec iv;
        private Cipher cipher;

        public Aes(String strKey) {
            try {
                this.key = new SecretKeySpec(getHash("MD5", strKey), "AES");
                this.iv = new IvParameterSpec(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0 });
                this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        
        public String encrypt(String strContent) {
            try {
                byte[] data = strContent.getBytes("UTF-8");
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                byte[] encryptData = cipher.doFinal(data);
                return new BASE64Encoder().encode(encryptData);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        private static byte[] getHash(String algorithm, String text) {
            try {
                byte[] bytes = text.getBytes("UTF-8");
                final MessageDigest digest = MessageDigest.getInstance(algorithm);
                digest.update(bytes);
                return digest.digest();
            } catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
    
    public String sendPost(String param, String url) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "token");
            conn.setRequestProperty("tag", "htc_new");

            conn.connect();

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(param);

            out.flush();
            out.close();
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
        return result;
    }
    
}
