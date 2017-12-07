package my.core.model;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_invoice", pk = "id")
public class Invoice extends Model<Invoice> {
	
	public static final Invoice dao = new Invoice();

	public Page<Invoice> queryByPage(int page,int size){
		String sql=" from t_invoice where 1=1 order by create_time desc";
		String select="select * ";
		return Invoice.dao.paginate(page, size, select, sql);
	}
	
	/*public Page<Invoice> queryByPageParams(int page,int size,int memberId){
		
		if(memberId != 0){
			String sql=" from t_store_evaluate a inner join t_store b on a.store_id=b.id where b.member_id="+memberId+" order by create_time desc";
			String select="select * ";
			return Invoice.dao.paginate(page, size, select, sql);
		}else{
			String sql=" from t_store_evaluate order by create_time desc";
			String select="select * ";
			return Invoice.dao.paginate(page, size, select, sql);
		}
	}*/
	
	public boolean updateInfo(Invoice data){
		return new Invoice().setAttrs(data).update();
	}
	
	public Invoice saveInfo(Invoice tea){
		Invoice store = new Invoice().setAttrs(tea);
		store.save();
		return store;
	}
	
	public int saveInfos(Invoice data){
		Invoice store = new Invoice().setAttrs(data);
		store.save();
		return store.getInt("id");
	}
	
	/*public List<Invoice> queryStoreEvaluateList(int pageSize,int pageNum,int storeId){
		int fromRow = (pageNum-1)*pageSize;
		return Invoice.dao.find("select * from t_store_evaluate where flg=1 and store_id="+storeId+" order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public int sumStoreEvaluateNum(int userId,int storeId,String date1,String date2){
		Long sum = Db.queryLong("select count(1) from t_store_evaluate where member_id="+userId+" and store_id="+storeId+" and create_time>='"+date1+"' and create_time<='"+date2+"'");
		if(sum == null){
			return 0;
		}
		return sum.intValue();
	}*/
}
