package my.app.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import ch.qos.logback.core.status.Status;
import my.pvcloud.model.TestModel;
import my.pvcloud.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		
		/*List<TestModel> list = new ArrayList<TestModel>();
		List<BigDecimal> key = new ArrayList<BigDecimal>();
		 TestModel t1 = new TestModel();
		 t1.setId(1);
		 t1.setName("11");
		 t1.setDist(new BigDecimal("99.28"));
		 
		 TestModel t2 = new TestModel();
		 t2.setId(2);
		 t2.setName("199");
		 t2.setDist(new BigDecimal("199.28"));
		 
		 TestModel t3 = new TestModel();
		 t3.setId(3);
		 t3.setName("299");
		 t3.setDist(new BigDecimal("299.28"));
		 
		 list.add(t1);
		 list.add(t2);
		 list.add(t3);
		 
		 key.add(new BigDecimal("199.28"));
		 key.add(new BigDecimal("99.28"));
		 key.add(new BigDecimal("299.28"));
		 
		 Map<BigDecimal, TestModel> map = new HashMap<BigDecimal, TestModel>();
		 map.put(new BigDecimal("199.28"), t2);
		 map.put(new BigDecimal("99.28"), t1);
		 map.put(new BigDecimal("299.28"), t3);
		 
		 Collections.sort(key);
		 for(BigDecimal k  : key){
			 System.out.println(k+"-"+map.get(k).getDist());
		 }*/
		//System.out.println(StringUtil.getStoreKeyCode(""));

	}

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
