package my.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import ch.qos.logback.core.status.Status;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		
		 System.out.println(getMonthFullDay(2017,2,1));
		 JSONObject jsonObject = JSONObject.fromObject("{id:1}");
		 
		 System.out.println(jsonObject.getString("id"));

	}

	/*public static int getDayOfMonth(){
		   Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
		   int day=aCalendar.getActualMaximum(Calendar.DATE);
		   return day;
		}

		//java获取当前月每天的日期
		public static List getDayListOfMonth() {
		    List<String> list = new ArrayList();
		    Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
		    int year = aCalendar.get(Calendar.YEAR);//年份
		    int month = aCalendar.get(Calendar.MONTH);//月份
		    int day = aCalendar.getActualMaximum(Calendar.DATE);
		    for (int i = 1; i <= day; i++) {
		        String aDate = String.valueOf(year)+"/"+month+i;
		        list.add(aDate);
		    }
		    return list;
		}*/
	
	/**
     * 某一年某个月的每一天
     */
    public static List<String> getMonthFullDay(int year,int month,int day){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> fullDayList = new ArrayList<String>();
        if(day <= 0 ) day = 1;
        Calendar cal = Calendar.getInstance();// 获得当前日期对象
        cal.clear();// 清除信息
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);// 1月从0开始
        cal.set(Calendar.DAY_OF_MONTH, day);// 设置为1号,当前日期既为本月第一天
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int j = 0; j <= (count-1);) {
            if(sdf.format(cal.getTime()).equals(getLastDay(year, month)))
                break;
            cal.add(Calendar.DAY_OF_MONTH, j == 0 ? +0 : +1);
            j++;
            fullDayList.add(sdf.format(cal.getTime()));
        }
        return fullDayList;
    }
    
    public static String getLastDay(int year,int month){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return sdf.format(cal.getTime());
    }
}
