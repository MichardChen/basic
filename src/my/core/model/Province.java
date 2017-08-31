package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "location_province", pk = "id")
public class Province extends Model<Province> {
		
		public static final Province dao = new Province();
		
		public Province queryProvince(int id){
			if(id == 0){
				return null;
			}
			return Province.dao.findFirst("select * from location_province where id=?",id);
		}
}
