package my.app.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import my.core.model.WxSubmitModel;
import my.pvcloud.util.DateUtil;


public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args){
	
		/*List<String> d = DateUtil.getMonthFullDayByNum(5);
		for(String dd : d){
			System.out.println(dd);
		}*/
		try {
		JSONObject postJson = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("address", "pages/index/index");
		data.put("tag", "生活");
		data.put("first_class", "文娱");
		data.put("second_class", "资讯");
		data.put("first_id", 1);
		data.put("second_id", 2);
		data.put("title", "首页");
		WxSubmitModel model = new WxSubmitModel();
		model.setAddress("pages/index/index");
		model.setTag("生活");
		model.setFirst_class("文娱");
		model.setSecond_class("资讯");
		model.setFirst_id(1);
		model.setSecond_id(2);
		model.setTitle("首页");
		List<WxSubmitModel> models = new ArrayList<>();
		models.add(model);
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(data);
		
			postJson.put("item_list", jsonArray.toString());
			System.out.println("json:"+postJson.toString());
			//postJson.put("item_list", "[{\"address\":\"pages/index/index\",\"tag\":\"生活\",\"first_class\": \"文娱\",\"second_class\": \"资讯\",\"first_id\":1,\"second_id\":2,\"title\": \"首页\"}]");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}