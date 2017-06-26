package cn.lzxz1234.wxbot.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @class HttpUtils
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class HttpUtils {

    private static final int TIME_OUT = 3000;
    
    public static byte[] download(String urlLocation) {
        
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlLocation);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIME_OUT);
            conn.connect();
            
            is = conn.getInputStream();
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException("请求错误！", e);
        } finally {
            IOUtils.closeQuietly(is);
            if(conn != null) conn.disconnect();
        }
    }
    
    public static String get(String urlLocation) {
        
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlLocation);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIME_OUT);
            conn.connect();
            
            is = conn.getInputStream();
            return IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("请求错误！", e);
        } finally {
            IOUtils.closeQuietly(is);
            if(conn != null) conn.disconnect();
        }
    }
    
    public static String post(String urlLocation, String content) {
        
        
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            URL url = new URL(urlLocation);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIME_OUT);
            conn.connect();
            
            os = conn.getOutputStream();
            os.write(content.getBytes("UTF-8"));
            IOUtils.closeQuietly(os);
            
            is = conn.getInputStream();
            return IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("请求错误！", e);
        } finally {
            IOUtils.closeQuietly(is);
            if(conn != null) conn.disconnect();
        }
    }

    public static String post(String urlLocation, byte[] fileContent) {
        
        Map<String, byte[]> files = new HashMap<String, byte[]>();
        files.put("file", fileContent);
        return post(urlLocation, null, null, files);
    }

    public static String post(String url, Map<String, String> headers, Map<String, String> params, Map<String, byte[]> files) {

        try {
            MultipartUtility req = new MultipartUtility(url, "UTF-8");
            if(params != null)
                for(Map.Entry<String, String> each : params.entrySet()) 
                    req.addFormField(each.getKey(), each.getValue());
            if(files != null)
                for(Map.Entry<String, byte[]> each : files.entrySet())
                    req.addFilePart(each.getKey(), "", each.getValue());
            return req.send(headers);
        } catch (Exception e) {
            throw new RuntimeException("请求失败", e);
        }
    }

    public static class MultipartUtility {
        
        private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private String requestURL;
        private String charset;
        private ByteArrayOutputStream outputStream;
        private PrintWriter writer;
     
        public MultipartUtility(String requestURL, String charset) throws Exception {
            
            this.requestURL = requestURL;
            this.charset = charset;
            boundary = "qbsNZKOclzJhn5ZPAsIUihqpaEYPiv3Y";
            outputStream = new ByteArrayOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        }
     
        public void addFormField(String name, String value) {
            
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
            writer.append("Content-Type: multipart/form-data; charset=").append(charset).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: 8bit").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }
     
        public void addFilePart(String fieldName, String fileName, byte[] uploadFile) throws IOException {
            
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"");
            if(StringUtils.isNotEmpty(fileName))
                writer.append("; filename=\"").append(fileName).append("\"");
            writer.append(LINE_FEED);
            writer.append("Content-Type: ").append("application/octet-stream").append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
     
            outputStream.write(uploadFile);
            outputStream.flush();
             
            writer.append(LINE_FEED);
            writer.flush();
        }
     
        public String send(Map<String, String> headers) throws IOException {
     
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
     
            URL url = new URL(requestURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if(headers != null)
                for(Map.Entry<String, String> each : headers.entrySet())
                    httpConn.setRequestProperty(each.getKey(), each.getValue());
            OutputStream os = httpConn.getOutputStream();
            os.write(outputStream.toByteArray());
            os.flush();
            os.close();
            String result = IOUtils.toString(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK ? httpConn.getInputStream() : httpConn.getErrorStream(), "UTF-8");
            httpConn.disconnect();
            return result;
        }
    }
}
