package my.core.wxpay;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import my.core.model.CashJournal;
import my.pvcloud.util.PropertiesUtil;
import my.pvcloud.util.StringUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
        //String outTradeNo = CashJournal.dao.queryCurrentCashNo();
        String outTradeNo = StringUtil.getOrderNo();
        String reqBody = "<xml>"
        				+"<body>掌上茶宝-充值</body>"
        				+"<trade_type>APP</trade_type>"
        				+"<mch_id>"+wx_mch_id+"</mch_id>"
        				+"<sign_type>HMAC-SHA256</sign_type>"
        				+"<nonce_str>"+nonStr+"</nonce_str>"
        				+"<fee_type>CNY</fee_type>"
        				+"<device_info>WEB</device_info>"
        				+"<out_trade_no>"+outTradeNo+"</out_trade_no>"
        				+"<total_fee>1</total_fee>"
        				+"<appid>"+wx_appid+"</appid>"
        				+"<notify_url>"+wx_notify_url+"</notify_url>"
        				+"<sign>78F24E555374B988277D18633BF2D4CA23A6EAF06FEE0CF1E50EA4EADEEC41A3</sign>"
        				+"<spbill_create_ip>123.12.12.123</spbill_create_ip>"
        				+"</xml>";
        URL httpUrl = new URL(wx_unifiedorder);
        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
        httpURLConnection.setRequestProperty("Host", "api.mch.weixin.qq.com");
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

}
