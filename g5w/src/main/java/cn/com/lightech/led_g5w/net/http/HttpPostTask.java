package cn.com.lightech.led_g5w.net.http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import cn.com.lightech.led_g5w.net.utils.PostParamUtil;

/**
 * Created by 明 on 2016/3/8.
 */
public class HttpPostTask extends AsyncTask<String, Void, String> {

    final String tag = this.getClass().getName();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        // 取mac
        //String parameterData = "get_MAC=?";
        //指定连接 到 wifi
        //String parameterData = "netmode=2&wifi_conf=lightech,wpawpa2_aes,86936158&dhcpc=1&net_commit=1";
        //恢复出厂设置
        //String parameterData = "default=1&reboot=1";
        // 重启wifi
        //String parameterData="reboot=1";
        String url = params[0];
        String parameterData = params[1];

        Log.i(tag ,"request params : "+ parameterData);
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            URL localURL = new URL(url);

            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            httpURLConnection.setRequestProperty("WifiDialogContent-Length",
                    String.valueOf(parameterData.length()));

            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);


            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(parameterData);
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is "
                                + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            Log.i(tag,"post response:" + resultBuffer.toString());

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {

            }

        }

        return resultBuffer.toString();
    }


}
