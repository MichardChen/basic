package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_gettea_record", pk = "id")
public class GetTeaRecord extends Model<GetTeaRecord> {
	
	public static final GetTeaRecord dao = new GetTeaRecord();

	public Page<GetTeaRecord> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_gettea_record where 1=1 order by create_time desc";
		String select="select * ";
		return GetTeaRecord.dao.paginate(page, size, select, sql);
	}
	
	public Page<GetTeaRecord> queryByPageParams(int page,int size,String time1,String time2,String mobile,String status){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(mobile)){
			if(StringUtil.isNoneBlank(time1)){
				strBuf.append(" and a.create_time>='"+time1+" 00:00:00'");
			}
			if(StringUtil.isNoneBlank(time2)){
				strBuf.append(" and a.create_time<='"+time2+" 23:59:59'");
			}
				
			strBuf.append(" and b.mobile='"+mobile+"'");
			
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.status='"+status+"'");
			}
			
			String sql=" from t_gettea_record a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return GetTeaRecord.dao.paginate(page, size, select, sql,param.toArray());
		}else{
			if(StringUtil.isNoneBlank(time1)){
				strBuf.append(" and a.create_time>='"+time1+" 00:00:00'");
			}
			if(StringUtil.isNoneBlank(time2)){
				strBuf.append(" and a.create_time<='"+time2+" 23:59:59'");
			}
				
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.status='"+status+"'");
			}
			
			String sql=" from t_gettea_record a where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return GetTeaRecord.dao.paginate(page, size, select, sql,param.toArray());
		}
	}
	
	public GetTeaRecord queryById(int id){
		return GetTeaRecord.dao.findFirst("select * from t_gettea_record where id = ? order by create_time desc",id);
	}
	
	public List<GetTeaRecord> queryRecords(int pageSize,int pageNum,int memberId,String date){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(date)){
			return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" and create_time like '%"+date+"%' order by create_time desc limit "+fromRow+","+pageSize);
		}else{
			return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" order by create_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public List<GetTeaRecord> queryRecordByTime(int pageSize,int pageNum,int memberId,String date,String date2){
		int fromRow = pageSize*(pageNum-1);
		return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" and create_time>='"+date+"' and create_time<='"+date2+"' and invoice_status='340003' order by create_time asc limit "+fromRow+","+pageSize);
	}
	
	public List<GetTeaRecord> queryRecordByTime2(int pageSize,int pageNum,int memberId,String date,String date2){
		int fromRow = pageSize*(pageNum-1);
		return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" and create_time>='"+date+"' and create_time<='"+date2+"' and invoice_status in('340001','340004') order by create_time asc limit "+fromRow+","+pageSize);
	}
	
	public boolean updateInfo(GetTeaRecord tea){
		return new GetTeaRecord().setAttrs(tea).update();
	}
	
	public boolean saveInfo(GetTeaRecord tea){
		return new GetTeaRecord().setAttrs(tea).save();
	}
	
	public boolean del(int id){
		return GetTeaRecord.dao.deleteById(id);
	}
	
	public int updateMsg(int id,String expressName,String expressNo,String mark,String status){
		
		return Db.update("update t_gettea_record set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"',express_company='"+expressName+"',express_no='"+expressNo+"',mark='"+mark+"' where id="+id);
	}
	
	public List<Record> queryWarehouseTeaGetNum(int warehouseId){
		String sql = "SELECT tea_id as teaId,size_type_cd as size,quality as quality "+
					 " from t_gettea_record where warehouse_id="+warehouseId+" and status in('280001','280003','280004')";
		List<Record> models = Db.find(sql);
		return models;
	}
	
	public int updateInvoice(int id,String invoiceStatus){
		return Db.update("update t_gettea_record set invoice_status='"+invoiceStatus+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
}
