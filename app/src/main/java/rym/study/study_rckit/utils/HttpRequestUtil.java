package rym.study.study_rckit.utils;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import rym.study.study_rckit.common.Const;

public class HttpRequestUtil extends Thread {

    private static final String TAG = "HttpRequestUtil";

    public static final String METHOD_POST = "POST";

    private static final String APPKEY = "RC-App-Key";
    private static final String NONCE = "RC-Nonce";
    private static final String TIMESTAMP = "RC-Timestamp";
    private static final String SIGNATURE = "RC-Signature";

    private Handler mHandler;
    private HttpURLConnection mConn;
    private String mContent;

    public HttpRequestUtil(Handler handler, String method, String urlStr, String content) throws IOException {
        Log.d(TAG, "HttpRequestUtil method = " + method + ", urlStr = " + urlStr + ", content = " + content);
        mHandler = handler;
        mContent = content;

        // create HttpURLConnection
        URL url = new URL(urlStr);
        mConn = (HttpURLConnection) url.openConnection();
        mConn.setUseCaches(false);
        mConn.setDoInput(true);
        mConn.setDoOutput(true);
        mConn.setRequestMethod(method);
        mConn.setInstanceFollowRedirects(true);
        mConn.setConnectTimeout(30000);
        mConn.setReadTimeout(30000);

        // prepare header
        String nonce = String.valueOf(Math.random() * 1000000);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = hexSHA1(Const.APP_SECRET + nonce + timestamp);
        mConn.setRequestProperty(APPKEY, Const.APP_KEY);
        mConn.setRequestProperty(NONCE, nonce);
        mConn.setRequestProperty(TIMESTAMP, timestamp);
        mConn.setRequestProperty(SIGNATURE, sign);
        mConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @Override
    public void run() {
        try {
            writeContent();
            byte[] data = sendRequest();
            String jsonStr = new String(data);

            // send result message
            Message msg = Message.obtain();
            msg.obj = jsonStr;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeContent() throws IOException {
        DataOutputStream out = new DataOutputStream(mConn.getOutputStream());
        out.writeBytes(mContent);
        out.flush();
        out.close();
    }

    private byte[] sendRequest() throws IOException {
        InputStream in = mConn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        in.close();
        return data;
    }

    private String hexSHA1(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            return String.valueOf(HexUtil.encodeHex(digest));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
