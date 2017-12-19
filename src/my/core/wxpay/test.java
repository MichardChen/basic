package my.core.wxpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import my.pvcloud.util.PropertiesUtil;
import my.pvcloud.util.StringUtil;

public class test {

    public static void main(String[] args) throws Exception {

        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        String wx_appid = propertiesUtil.getProperties("wx_appid");
        String wx_mch_id = propertiesUtil.getProperties("wx_mch_id");
        String wx_key = propertiesUtil.getProperties("wx_key");
        String wx_unifiedorder = propertiesUtil.getProperties("wx_unifiedorder");
        String wx_notify_url = propertiesUtil.getProperties("wx_notify_url");
        String nonStr = WXPayUtil.generateNonceStr();
        String UTF8 = "UTF-8";
        String outTradeNo = StringUtil.getOrderNo();
        String stringA="appid="+wx_appid
        			  +"&body=掌上茶宝-充值"
        			  +"&mch_id="+wx_mch_id
        			  +"&nonce_str="+nonStr
        			  +"&notify_url="+wx_notify_url
        			  +"&out_trade_no="+outTradeNo
        			  +"&spbill_create_ip=120.41.149.248"
        			  +"&total_fee=1000"
        			  +"&trade_type=APP";
        String md5StringA = WXPayUtil.MD5(stringA+"&key="+wx_key);
        String reqBody = "<xml>"
        				+"<appid>"+wx_appid+"</appid>"
        				+"<body>掌上茶宝-充值</body>"
        				+"<mch_id>"+wx_mch_id+"</mch_id>"
        				+"<nonce_str>"+nonStr+"</nonce_str>"
        				+"<notify_url>"+wx_notify_url+"</notify_url>"
        				+"<out_trade_no>"+outTradeNo+"</out_trade_no>"
        				+"<spbill_create_ip>120.41.149.248</spbill_create_ip>"
        				+"<total_fee>1000</total_fee>"
        				+"<trade_type>APP</trade_type>"
        				+"<sign>"+md5StringA+"</sign>"
        				+"</xml>";
        URL httpUrl = new URL(wx_unifiedorder);
        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
       // httpURLConnection.setRequestProperty("Host", "api.mch.weixin.qq.com");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(10*1000);
        httpURLConnection.setReadTimeout(10*1000);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(reqBody.getBytes(UTF8));

        //获取内容
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
        final StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        String resp = stringBuffer.toString();
        if (stringBuffer!=null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream!=null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream!=null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(resp);

    }
    
    public static void getSign(Map<String, String> map){
        String[] keys = map.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuffer reqStr = new StringBuffer();
        for(String key : keys){
           System.out.println(key);
        }
     }

}
