package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_warehouse_tea_member", pk = "id")
public class WarehouseTeaMember extends Model<WarehouseTeaMember> {
	
	public static final WarehouseTeaMember dao = new WarehouseTeaMember();

	
	public Page<WarehouseTeaMember> queryByPage(int page,int size){
			
		String sql=" from t_warehouse_tea_member where 1=1 order by create_time desc";
		String select="select * ";
		return WarehouseTeaMember.dao.paginate(page, size, select, sql);
	}
	
	public List<WarehouseTeaMember> queryAllHouse(){
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member order by update_time desc");
	}
	
	public List<WarehouseTeaMember> queryWareHouseList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public WarehouseTeaMember queryById(int id){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where id = ?",id);
	}
	
	
	public WarehouseTeaMember queryWarehouseTeaMember(int id,String memberTypeCd){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where tea_id = ? and member_type_cd=?",id,memberTypeCd);
	}
	public boolean updateInfo(WarehouseTeaMember data){
		return new WarehouseTeaMember().setAttrs(data).update();
	}
	
	public boolean saveInfo(WarehouseTeaMember data){
		return new WarehouseTeaMember().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return WarehouseTeaMember.dao.deleteById(id);
	}

}
