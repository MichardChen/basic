package my.app.test;

import my.pvcloud.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;

public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		/*String sJson = "[{'teaId':10,'quality':20},{'teaId':10,'quality':20},{'teaId':10,'quality':20}]";
		JSONArray jsonArray = new JSONArray(sJson);
		int iSize = jsonArray.length();
		System.out.println("Size:" + iSize);
		for (int i = 0; i < iSize; i++) {
		JSONObject jsonObj = jsonArray.getJSONObject(i);
		System.out.println("[" + i + "]teaId=" + jsonObj.get("teaId"));
		System.out.println("[" + i + "]quality=" + jsonObj.get("quality"));
		System.out.println();
				
	}*/
	
		String string = "[1,23,45]";
		PropertiesUtil pUtil = PropertiesUtil.getInstance();
		System.out.print(pUtil.getProperties("wx_secret"));
	}
}