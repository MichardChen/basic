package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_Warehouse_tea", pk = "id")
public class WarehouseTea extends Model<WarehouseTea> {
	
	public static final WarehouseTea dao = new WarehouseTea();

	public WarehouseTea queryById(int id){
		return WarehouseTea.dao.findFirst("select * from t_Warehouse_tea where id = ?",id);
	}
	
	public boolean updateInfo(WarehouseTea data){
		return new WarehouseTea().setAttrs(data).update();
	}
	
	public boolean saveInfo(WarehouseTea data){
		return new WarehouseTea().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return WarehouseTea.dao.deleteById(id);
	}
}
