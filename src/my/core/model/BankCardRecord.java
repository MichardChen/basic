package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_bankcard_record", pk = "id")
public class BankCardRecord extends Model<BankCardRecord> {
	
	public static final BankCardRecord dao = new BankCardRecord();

	public Page<BankCardRecord> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_bankcard_record where 1=1 order by create_time desc";
		String select="select * ";
		return BankCardRecord.dao.paginate(page, size, select, sql);
	}
	
/*	public Page<GetTeaRecord> queryByPageParams(int page,int size,String title){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(title)){
			strBuf.append(" and store_name=?");
			param.add(title);
		}
			
			String sql=" from t_bankcard_record where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return GetTeaRecord.dao.paginate(page, size, select, sql,param.toArray());
		}*/
	
	public BankCardRecord queryById(int id){
		return BankCardRecord.dao.findFirst("select * from t_bankcard_record where id = ? order by create_time desc",id);
	}
	
	public List<BankCardRecord> queryRecords(int pageSize,int pageNum,int memberId){
		int fromRow = pageSize*(pageNum-1);
		return BankCardRecord.dao.find("select * from t_bankcard_record where member_id ="+memberId+" order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public boolean updateInfo(BankCardRecord data){
		return new BankCardRecord().setAttrs(data).update();
	}
	
	public boolean saveInfo(BankCardRecord data){
		return new BankCardRecord().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return BankCardRecord.dao.deleteById(id);
	}
}
