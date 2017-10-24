package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

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
	
	public Page<Store> queryByPageParams(int page,int size,String title,String status,int memberId){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(title)){
			strBuf.append(" and store_name=?");
			param.add(title);
		}
		if(StringUtil.isNoneBlank(status)){
			strBuf.append(" and status=?");
			param.add(status);
		}
		if(memberId != 0){
			strBuf.append(" and member_id=?");
			param.add(memberId);
		}
			
			String sql=" from t_store where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return Store.dao.paginate(page, size, select, sql,param.toArray());
		}
	
	public Store queryById(int id){
		return Store.dao.findFirst("select * from t_store where id = ? order by create_time desc",id);
	}
	
	public Store queryMemberStore(int userId){
		return Store.dao.findFirst("select * from t_store where member_id = ?",userId);
	}
	
	public List<Store> queryStoreList(int pageSize
									 ,int pageNum
									 ,String status
									 ,Float maxLongtitude
									 ,Float maxLatitude
									 ,Float minLongtitude
									 ,Float minLatitude){
		int fromRow = pageSize*(pageNum-1);
		return Store.dao.find("select * from t_store where status='"+status+"' and "+minLongtitude+"<=longitude and longitude<="+maxLongtitude+" and "+minLatitude+"<=latitude and latitude<="+maxLatitude+" order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public boolean updateInfo(Store tea){
		return new Store().setAttrs(tea).update();
	}
	
	public Store saveInfo(Store tea){
		Store store = new Store().setAttrs(tea);
		store.save();
		return store;
	}
	
	public int saveInfos(Store tea){
		Store store = new Store().setAttrs(tea);
		store.save();
		return store.getInt("id");
	}
	
	public boolean del(int id){
		return Store.dao.deleteById(id);
	}
	
	public int updateStoreStatus(int id,String status){
		Db.update("update t_store set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		Store tea = Store.dao.findFirst("select * from t_store where id = ?",id);
		if(tea != null){
			return tea.getInt("flg");
		}else{
			return 0;
		}
	}
}
