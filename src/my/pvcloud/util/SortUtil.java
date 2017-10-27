package my.pvcloud.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alipay.api.domain.Data;
import com.sun.org.apache.regexp.internal.recompile;

import my.core.vo.DataListVO;

public class SortUtil {

	public static List<DataListVO> queryAsc(List<DataListVO> data){
		
		/*List<DataListVO> list = new ArrayList<>();
		for(int k=0;k<data.size()-1;k++){
			for(int j=0;j<data.size()-k-1;j++){
				if(data.get(k).getKey().compareTo(data.get(j).getKey())>0){
					//时间大于
					DataListVO temp = data.get(j);
					data.add(j, data.get(j+1));
					data.add(j+1,temp);
				}
			}
		}
		
		for(DataListVO vo : data){
			System.out.println(vo.getKey());
		}
		return data;*/
		   Collections.sort(data);
	        for(DataListVO u : data){
	            System.out.println(u.getKey());
	        }
	        return data;
	}
}
