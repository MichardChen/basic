package my.app.test;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Date;

import org.json.JSONException;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.GDMapUtil;
import my.pvcloud.util.GeoUtil;
import my.pvcloud.util.LngLat;
import my.pvcloud.util.StringUtil;

public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		/*double[] data = GeoUtil.getRectangle(Double.valueOf("116.427265"), Double.valueOf("39.914271"), new Long("10000"));
		System.out.println(data[0]);
		System.out.println(data[1]);
		System.out.println(data[2]);
		System.out.println(data[3]);
		System.out.println(GeoUtil.getDistanceOfMeter(Double.valueOf("118.203477"), Double.valueOf("24.501857"), Double.valueOf("118.1944171"), Double.valueOf("24.491515")));
	*/
	
		/*Timestamp timestamp = DateUtil.getNowTimestamp();
		System.out.println(DateUtil.formatYMD(timestamp));
		Date date = new Date();
		System.out.println(DateUtil.formatDateYMD(date));*/
		
		Double myNumber=Double.valueOf("1000000");

		  Double test=0.3434;
		  //getInstance() 
		  //返回当前缺省语言环境的缺省数值格式。
		  String myString = NumberFormat.getInstance().format(myNumber);
		  System.out.println(myString);
		  //getCurrencyInstance()返回当前缺省语言环境的通用格式
		  myString = NumberFormat.getCurrencyInstance().format(myNumber); 
		  System.out.println(myString);
		  
		  String s = StringUtil.getIdCode();
		  System.out.println(s);
	}
}