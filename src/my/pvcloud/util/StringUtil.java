package my.pvcloud.util;

import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;



public class StringUtil extends StringUtils {

	public static final String STRING_BLANK = "";
	public static String toString(Double douNum){
		
		if (douNum == null){
			return "";
		}
		return Double.toString(douNum);
	}
	
	public static Integer toInteger(String num){
		if(StringUtil.equals("undefined", num)){
			return 0;
		}
		if (num == null || StringUtil.equals("", num)){
			return 0;
		}
		
		return new Integer(num);
	}
	
	public static String toString(Long longNum){
		
		if (longNum == null){
			return "";
		}
		return Long.toString(longNum);
	}
	

	public static String toString(Integer douNum){
		
		if (douNum == null){
			return "";
		}
		return douNum.toString();
	}
	
	public static String toString(Timestamp time) {
		
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			tsStr = sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tsStr;
	}
	
	public static String toStringFormat(Timestamp time) {
		
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			tsStr = sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tsStr;
	}
	
	public static String toString(int num) {
		
		Integer integer = new Integer(num);
		return integer.toString();
	}
	
	public static String getOrderNo(){
		long l = System.currentTimeMillis();
		//new日期对象
		Date date = new Date(l);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date1 = dateFormat.format(date);
	    //年月日
		Random random = new Random();
		int i = random.nextInt(100000)+100000;
		int units = i%10;
		int tens = (i/10)%10;
		int hundreads = (i/100)%10;
		int thousands =(1/1000)%10;
		return date1+tens+thousands+hundreads;
	}
	
	public static String toString(BigDecimal num){
		if(num == null){
			return "0";
		}
		return num.toString();
	}
	
	/**
	 * Pad left
	 * @param s
	 * @param length
	 * @return
	 */
	public static String padLeft(String s, int length) {
		byte[] bs = new byte[length];
		byte[] ss = s.getBytes();
		Arrays.fill(bs, (byte) (48 & 0xff));
		System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
		return new String(bs);
	}
	
	/**
	 * remove a serial of zero
	 * @param str
	 * @return
	 */
	public static String removeZero(String str){
		
		if(StringUtil.isNotBlank(str)){
			str = str.substring(8);
		}
		
		if(!StringUtil.isNotBlank(str) || str.indexOf("0") == -1){
			return str;
		}else {
			int lastZeroPosition = str.lastIndexOf("0")+1;
			return str.substring(lastZeroPosition, str.length());
		}
		
	}
	
	public static String transformUrl(String str){
		/**
		 * 正则表达式说明
		 * (http://)?? 			表示一次或者多次
		 * (\\w)+				表示单词字符：[a-zA-Z_0-9] 出现至少一次
		 * (\\.\\w+)+		 	表示 .单词字符出现至少一次
		 * ([/\\w\\.\\?=%&-:@#$]*+)*+		表示 /单词字符.?=%&-:@#$出现0到多次的形式的字符串出现0到多次
		 */
		String reg = "((https|http|ftp|rtsp|mms)://)??(\\w)+(\\.\\w+)+([/\\w\\.\\?=%&-:@#$]*+)*+";
		//str = str.replace("http://", "");
		String outStr = str.replaceAll("("+reg+")", "<a href=\"$1\" style=\"color:#3baeda;\">$1</a>");
		return outStr;
	}
	
	public static String getRandomName(){
		// 获得当前时间
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		// 转换为字符串
		String formatDate = format.format(new Date());
		// 随机生成文件编号
		int random = new Random().nextInt(10000);
		return new StringBuffer().append(formatDate).append(random).toString();
	}
	
	public static BigDecimal toBigDecimal(String str){
		if(StringUtil.isBlank(str) || str == null){
			return new BigDecimal("0");
		}else{
			BigDecimal bd = new BigDecimal(str);
			bd.setScale(2,BigDecimal.ROUND_UP);
			return bd;
		}
	}
	
	public static float toFloat(String str){
		if(StringUtil.isBlank(str) || str == null){
			return Float.parseFloat("0");
		}else{
			return Float.parseFloat(str);
		}
	}
}
