package my.app.test;

import org.json.JSONException;

import my.pvcloud.util.GDMapUtil;
import my.pvcloud.util.LngLat;

public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		 LngLat start = new LngLat(118.095104, 24.448375);
	      LngLat end = new LngLat(118.114841, 24.448375);
	      System.err.println(GDMapUtil.calculateLineDistance(start, end));
	}
}