package my.core.model;

import java.math.BigDecimal;
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
		String sql=" from t_store_evaluate where 1=1 order by create_time desc";
		String select="select * ";
		return StoreEvaluate.dao.paginate(page, size, select, sql);
	}
	
	public Page<StoreEvaluate> queryByPageParams(int page
											    ,int size
											    ,String date
											    ,String mobile
											    ,String flg
											    ,String title1
											    ,String storeName
											    ,String content){
		
		if(StringUtil.isBlank(storeName)){
			Member member = Member.dao.queryMember(mobile);
			int storeId = 0;
			if(member != null){
				Store store = Store.dao.queryMemberStore(member.getInt("id"));
				if(store != null){
					storeId = store.getInt("id");
				}
			}
			
			String sql = " from t_store_evaluate where 1=1 ";
			String select = "select * ";
			if(StringUtil.isNoneBlank(flg)){
				int flgs = StringUtil.toInteger(flg);
				sql = sql + " and flg="+flgs;
			}
			
			if(StringUtil.isNoneBlank(date)){
				date = date+" 00:00:00";
				sql = sql + " and create_time >='"+date+"' ";
			}
			if(StringUtil.isNoneBlank(title1)){
				title1 = title1+" 23:59:59";
				sql = sql + " and create_time <='"+title1+"' ";
			}
			if(StringUtil.isNoneBlank(mobile)){
				sql = sql + " and store_id="+storeId;
			}
			
			if(StringUtil.isNoneBlank(content)){
				sql = sql + " and mark like '%"+content+"%'";
			}
			
			sql = sql +" order by create_time desc";
				
			return StoreEvaluate.dao.paginate(page, size, select, sql);
		}else{
			Member member = Member.dao.queryMember(mobile);
			int storeId = 0;
			if(member != null){
				Store store = Store.dao.queryMemberStore(member.getInt("id"));
				if(store != null){
					storeId = store.getInt("id");
				}
			}
			
			String sql = " from t_store_evaluate a inner join t_store b on a.store_id=b.id where 1=1 ";
			String select = "select a.* ";
			if(StringUtil.isNoneBlank(flg)){
				int flgs = StringUtil.toInteger(flg);
				sql = sql + " and a.flg="+flgs;
			}
			
			if(StringUtil.isNoneBlank(date)){
				date = date+" 00:00:00";
				sql = sql + " and a.create_time >='"+date+"' ";
			}
			if(StringUtil.isNoneBlank(title1)){
				title1 = title1+" 23:59:59";
				sql = sql + " and a.create_time <='"+title1+"' ";
			}
			if(StringUtil.isNoneBlank(mobile)){
				sql = sql + " and a.store_id="+storeId;
			}
			
			if(StringUtil.isNoneBlank(content)){
				sql = sql + " and b.mark like '%"+content+"%'";
			}
			
			sql = sql + " and b.store_name like '%"+storeName+"%'";
			
			sql = sql +" order by a.create_time desc";
				
			return StoreEvaluate.dao.paginate(page, size, select, sql);
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
	
	public int sumStorePoint(int storeId,String date) {
		BigDecimal sum = Db.queryBigDecimal("select sum(service_point) from t_store_evaluate where store_id=" + storeId+" and flg=1");
		if(sum == null){
			return 0;
		}
		return sum.intValue();
	}
}
