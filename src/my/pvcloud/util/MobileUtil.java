package my.pvcloud.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class MobileUtil {

	public static void main(String[] args) {  
	    String val = "18250752999";
	    //加密
	    String result=encryptPhone(val);  
	    System.out.println(result);  
	    //解密
	    String rawdata=decryptPhone(result);  
	    System.out.println(rawdata);  
	}  
	
	private static String[] data = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	  
	/** 
	 * 数字 0 1 2 3 4 5 6 7 8 9 
	 * 字母 b a c g h k o w q p 
	 * @param val 
	 * @return 数字转字母 
	 */  
	public static String getPhoneStr(String val) {  
	    String returnStr = "";  
	    if (val != null && val.length() > 0) {  
	        byte[] obj = val.getBytes();  
	        for (int i = 0; i < obj.length; i++) {  
	            returnStr += getLetter(String.valueOf(val.charAt(i)));  
	        }  
	    }  
	    return returnStr;  
	}  
	   /** 
	    * 字母转数字 
	    * 字母 b a c g h k o w q p 
	    * 数字 0 1 2 3 4 5 6 7 8 9 
	    * @param val 
	    * @return 
	    */  
	public static String getPhoneNum(String val){  
	    String returnStr = "";  
	    if (val != null && val.length() > 0) {  
	        byte[] obj = val.getBytes();  
	        for (int i = 0; i < obj.length; i++) {  
	            returnStr += getMapData().get(String.valueOf(val.charAt(i)));  
	        }  
	    }  
	    return returnStr;  
	}  
	  
	  
	  
	/** 
	 * 通过Map值获取key 
	 * @return 
	 */  
	public static String getLetter(String val) {  
	    for(Entry<String,String> entry:getMapData().entrySet()){  
	        if(val.equals(entry.getValue()))  
	           return String.valueOf(entry.getKey());  
	    }  
	    return val;  
	}  
	  
	/** 
	 * 字母 b a c g h k o w q p 
	    * 数字 0 1 2 3 4 5 6 7 8 9 
	 * @return Map 
	 */  
	public static Map<String,String> getMapData(){  
	    Map<String,String> map = new HashMap<String,String>();  
	        map.put("b","0");  
	        map.put("a","1");  
	        map.put("c","2");  
	        map.put("g","3");  
	        map.put("h","4");  
	        map.put("k","5");  
	        map.put("o","6");  
	        map.put("w","7");  
	        map.put("q","8");  
	        map.put("p","9");  
	    return map;  
	}  
	  

	  
	/** 
	 * ：加密步骤： 
	 * 1、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换 
	 * 2、将全部号码转换为对应的字符 
	 * 3、任意位置插入三个随机数字 
	 * 4、在步骤C之后的字符串前加上”~” 
	 * @return 
	 */  
	public static String encryptPhone(String phoneStr){ 
		Random random = new Random();
		//1.获取头尾3个字符串
		String t1 = data[random.nextInt(26)];
		String t2 = data[random.nextInt(26)];
		String t3 = data[random.nextInt(26)];
		String w1 = data[random.nextInt(26)];
		String w2 = data[random.nextInt(26)];
		String w3 = data[random.nextInt(26)];
		int randomNum = random.nextInt(10);
	    //1.将全部号码转换为对应的字符  
	    phoneStr=getPhoneStr(phoneStr);  
	    return t1+t2+t3+phoneStr+w1+w2+w3+randomNum;  
	}  
	  
	  
	/** 
	 * ：解密步骤： 
	 * 1、去除字符串前”~” 
	 * 2、去除所有数字 
	 * 3、将剩余字母全部转换为对应的数字 
	 * 4、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换 
	 * @return 
	 */  
	public static String decryptPhone(String phoneStr){  
	    if(!isNumber(phoneStr.substring(3,14))){  
	        //1.将剩余字母全部转换为对应的数字  
	        phoneStr=getPhoneNum(phoneStr.substring(3,14));  
	    }  
	    return phoneStr;  
	}  
	  
	        
	  
	   /** 
	    * 正则表达式数字验证 
	    *  
	    * @author crab 
	    * @param str 
	    * @return 
	    */  
	   public static boolean isNumber(String str) {  
	       if (str != null && !str.equals("")) {  
	           java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");  
	           java.util.regex.Matcher match = pattern.matcher(str);  
	           return match.matches();  
	       } else {  
	           return false;  
	       }  
	   }  
	  
	   /** 
	    * 字符串非空非null判断 crab 
	    */  
	   public static boolean isEmpty(String val) {  
	       if (val == null || val.equals("") || val.equalsIgnoreCase("null")) {  
	           return true;  
	       } else {  
	           return false;  
	       }  
	   }  
}
