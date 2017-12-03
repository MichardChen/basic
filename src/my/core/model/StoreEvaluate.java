package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_store_evaluate", pk = "id")
public class StoreEvaluate extends Model<StoreEvaluate> {
	
	public static final StoreEvaluate dao = new StoreEvaluate();

	public Page<StoreEvaluate> queryByPage(int page,int size){
		String sql=" from t_store_evaluate where 1=1 order by create_time desc";
		String select="select * ";
		return StoreEvaluate.dao.paginate(page, size, select, sql);
	}
	
	public Page<StoreEvaluate> queryByPageParams(int page,int size,int memberId){
		
		if(memberId != 0){
			String sql=" from t_store_evaluate a inner join t_store b on a.store_id=b.id where b.member_id="+memberId+" order by create_time desc";
			String select="select * ";
			return StoreEvaluate.dao.paginate(page, size, select, sql);
		}else{
			String sql=" from t_store_evaluate order by create_time desc";
			String select="select * ";
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
}
