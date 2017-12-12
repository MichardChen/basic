package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_store_evaluate", pk = "id")
public class StoreEvaluate extends Model<StoreEvaluate> {
	
	public static final StoreEvaluate dao = new StoreEvaluate();

	public Page<StoreEvaluate> queryByPage(int page,int size){
		String sql=" from t_store_evaluate where 1=1 order by create_time desc,flg desc";
		String select="select * ";
		return StoreEvaluate.dao.paginate(page, size, select, sql);
	}
	
	public Page<StoreEvaluate> queryByPageParams(int page,int size,String date,String mobile,String flg){
		
		Member member = Member.dao.queryMember(mobile);
		int storeId = 0;
		if(member != null){
			Store store = Store.dao.queryMemberStore(member.getInt("id"));
			if(store != null){
				storeId = store.getInt("id");
			}
		}
		
		if(StringUtil.isNoneBlank(flg)){
			int flgs = StringUtil.toInteger(flg);
			if(storeId == 0){
				if(StringUtil.isNoneBlank(date)){
					String sql=" from t_store_evaluate where flg="+flgs+" and create_time like '%"+date+"%' order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}else{
					String sql=" from t_store_evaluate where flg="+flgs+" order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}
			}else{
				if(StringUtil.isNoneBlank(date)){
					String sql=" from t_store_evaluate where store_id="+storeId+" and flg="+flgs+" and create_time like '%"+date+"%' order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}else{
					String sql=" from t_store_evaluate where store_id="+storeId+" and flg="+flgs+" order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}
			}
		}else{
			if(storeId == 0){
				if(StringUtil.isNoneBlank(date)){
					String sql=" from t_store_evaluate where create_time like '%"+date+"%' order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}else{
					String sql=" from t_store_evaluate order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}
			}else{
				if(StringUtil.isNoneBlank(date)){
					String sql=" from t_store_evaluate where store_id="+storeId+" and create_time like '%"+date+"%' order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}else{
					String sql=" from t_store_evaluate where store_id="+storeId+" order by create_time desc,flg desc";
					String select="select * ";
					return StoreEvaluate.dao.paginate(page, size, select, sql);
				}
			}
		}
	}
	
	public boolean updateInfo(StoreEvaluate tea){
		return new StoreEvaluate().setAttrs(tea).update();
	}
	
	public StoreEvaluate saveInfo(StoreEvaluate tea){
		StoreEvaluate store = new StoreEvaluate().setAttrs(tea);
		store.save();
		return store;
	}
	
	public int saveInfos(StoreEvaluate data){
		StoreEvaluate store = new StoreEvaluate().setAttrs(data);
		store.save();
		return store.getInt("id");
	}
	
	public List<StoreEvaluate> queryStoreEvaluateList(int pageSize,int pageNum,int storeId){
		int fromRow = (pageNum-1)*pageSize;
		return StoreEvaluate.dao.find("select * from t_store_evaluate where flg=1 and store_id="+storeId+" order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public int sumStoreEvaluateNum(int userId,int storeId,String date1,String date2){
		Long sum = Db.queryLong("select count(1) from t_store_evaluate where member_id="+userId+" and store_id="+storeId+" and create_time>='"+date1+"' and create_time<='"+date2+"'");
		if(sum == null){
			return 0;
		}
		return sum.intValue();
	}
	
	public int updateFlg(int id,int flg){
		return Db.update("update t_store_evaluate set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
}
