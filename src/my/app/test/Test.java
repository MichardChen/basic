package my.app.test;

import java.util.List;

import org.json.JSONException;

import my.pvcloud.util.DateUtil;


public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args){
	
		List<String> d = DateUtil.getMonthFullDayByNum(5);
		for(String dd : d){
			System.out.println(dd);
		}
	}
}