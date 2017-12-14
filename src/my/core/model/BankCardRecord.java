package my.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
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
	
	public Page<BankCardRecord> queryByPageParams(int page,int size,String time,String status,String mobile){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(time)){
			strBuf.append(" and a.create_time >=? and a.create_time <=?");
			param.add(DateUtil.formatStringForTimestamp(time+" 00:00:00"));
			param.add(DateUtil.formatStringForTimestamp(time+" 23:59:59"));
		}
		if(StringUtil.isNoneBlank(status)){
			strBuf.append(" and a.status =?");
			param.add(status);
		}
		if(StringUtil.isNoneBlank(mobile)){
			strBuf.append(" and b.mobile =?");
			param.add(mobile);
		}
			
		String sql=" from t_bankcard_record a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
		String select="select a.* ";
		return BankCardRecord.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public List<BankCardRecord> exportData(String time,String status,String mobile){
		
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(time)){
			String time1 = time+" 00:00:00";
			String time2 = time+" 23:59:59";
			strBuf.append(" and a.create_time >='"+time1+"' and a.create_time <='"+time2+"'");
		}
		if(StringUtil.isNoneBlank(status)){
			strBuf.append(" and a.status='"+status+"'");
		}
		if(StringUtil.isNoneBlank(mobile)){
			strBuf.append(" and b.mobile='"+mobile+"'");
		}
			
		String sql=" from t_bankcard_record a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
		String select="select a.* ";
		return BankCardRecord.dao.find(select+sql);
	}
	
	public BankCardRecord queryById(int id){
		return BankCardRecord.dao.findFirst("select * from t_bankcard_record where id = ? order by create_time desc",id);
	}
	
	public List<BankCardRecord> queryRecords(int pageSize,int pageNum,int memberId,String manuTypeCd,String date){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(date)){
			return BankCardRecord.dao.find("select * from t_bankcard_record where member_id ="+memberId+" and type_cd='"+manuTypeCd+"' and create_time like '%"+date+"%' order by create_time desc limit "+fromRow+","+pageSize);
		}else{
			return BankCardRecord.dao.find("select * from t_bankcard_record where member_id ="+memberId+" and type_cd='"+manuTypeCd+"' order by create_time desc limit "+fromRow+","+pageSize);
		}
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
	
	public int updateStoreStatus(int id,String status){
		return Db.update("update t_bankcard_record set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public BigDecimal sumApplying(int memberId,String type,String status){
		return Db.queryBigDecimal("select sum(moneys) from t_bankcard_record where member_id="+memberId+" and status='"+status+"' and type_cd='"+type+"'");
	}
}
