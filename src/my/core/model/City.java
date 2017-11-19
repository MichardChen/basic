package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "location_city", pk = "id")
public class City extends Model<City> {
		
		public static final City dao = new City();
		
		public City queryCity(int id){
			if(id == 0){
				return null;
			}
			return City.dao.findFirst("select * from location_city where id=?",id);
		}
		
		public List<City> queryAllCityByPid(int pid){
			return City.dao.find("select * from location_city where pid="+pid);
		}
}
