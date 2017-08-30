package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_store", pk = "id")
public class Store extends Model<Store> {
	
	public static final Store dao = new Store();

	public Page<Store> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_store where 1=1 order by create_time desc";
		String select="select * ";
		return Store.dao.paginate(page, size, select, sql);
	}
	
	public Store queryById(int id){
		return Store.dao.findFirst("select * from t_store where id = ?",id);
	}
	
	public boolean updateInfo(Store tea){
		return new Store().setAttrs(tea).update();
	}
	
	public boolean saveInfo(Store tea){
		return new Store().setAttrs(tea).save();
	}
	
	public boolean del(int id){
		return Store.dao.deleteById(id);
	}
	
	public int updateStoreStatus(int id,int flg){
		Db.update("update t_store set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		Store tea = Store.dao.findFirst("select * from t_store where id = ?",id);
		if(tea != null){
			return tea.getInt("flg");
		}else{
			return 0;
		}
	}
}
