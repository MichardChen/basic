package my.app.test;

import org.json.JSONException;

import my.pvcloud.util.GDMapUtil;
import my.pvcloud.util.GeoUtil;
import my.pvcloud.util.LngLat;

public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		double[] data = GeoUtil.getRectangle(Double.valueOf("116.427265"), Double.valueOf("39.914271"), new Long("10000"));
		System.out.println(data[0]);
		System.out.println(data[1]);
		System.out.println(data[2]);
		System.out.println(data[3]);
		System.out.println(GeoUtil.getDistanceOfMeter(Double.valueOf("118.203477"), Double.valueOf("24.501857"), Double.valueOf("118.1944171"), Double.valueOf("24.491515")));
	}
}