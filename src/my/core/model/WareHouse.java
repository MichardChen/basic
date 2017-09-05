package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_warehouse", pk = "id")
public class WareHouse extends Model<WareHouse> {
	
	public static final WareHouse dao = new WareHouse();

	
	public Page<WareHouse> queryByPage(int page,int size){
			
		String sql=" from t_warehouse where 1=1 order by create_time desc";
		String select="select * ";
		return WareHouse.dao.paginate(page, size, select, sql);
	}
	
	public List<WareHouse> queryAllHouse(){
		return WareHouse.dao.find("select * from t_warehouse where flg=1 order by update_time desc");
	}
	
	public List<WareHouse> queryWareHouseList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return WareHouse.dao.find("select * from t_warehouse order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public WareHouse queryById(int id){
		return WareHouse.dao.findFirst("select * from t_warehouse where id = ?",id);
	}
	
	public boolean updateInfo(WareHouse data){
		return new WareHouse().setAttrs(data).update();
	}
	
	public boolean saveInfo(WareHouse data){
		return new WareHouse().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return WareHouse.dao.deleteById(id);
	}
	
	public int updateWareHouseStatus(int id,int flg){
		Db.update("update t_warehouse set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		WareHouse carousel = WareHouse.dao.findFirst("select * from t_warehouse where id = ?",id);
		if(carousel != null){
			return carousel.getInt("flg");
		}else{
			return 0;
		}
	}
}
