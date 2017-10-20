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

@TableBind(table = "t_pay_record", pk = "id")
public class PayRecord extends Model<PayRecord> {
	
	public static final PayRecord dao = new PayRecord();

	
	public PayRecord queryById(int id){
		return PayRecord.dao.findFirst("select * from t_pay_record where id = ?",id);
	}
	
	public PayRecord queryByOutTradeNo(String outTradeNo){
		return PayRecord.dao.findFirst("select * from t_pay_record where out_trade_no = ?",outTradeNo);
	}
	
	
	public boolean updateInfo(PayRecord data){
		return new PayRecord().setAttrs(data).update();
	}
	
	public boolean saveInfo(PayRecord data){
		return new PayRecord().setAttrs(data).save();
	}
	
	public int updatePay(String outTradeNo,String status,String tradeNo){
		return Db.update("update t_pay_record set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"',trade_no='"+tradeNo+"' where out_trade_no="+outTradeNo);
	}
	
	public Page<PayRecord> queryByPage(int page,int size){
		/*	List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			strBuf.append(" and flg=?");
			param.add(flg);*/
			
			String sql=" from t_pay_record where 1=1 order by create_time desc";
			String select="select * ";
			return PayRecord.dao.paginate(page, size, select, sql);
		}
		
		public Page<PayRecord> queryByPageParams(int page,int size,String time,String mobile){
			
			List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(time)){
				strBuf.append(" and a.create_time >=? and a.create_time <=?");
				param.add(DateUtil.formatStringForTimestamp(time+" 00:00:00"));
				param.add(DateUtil.formatStringForTimestamp(time+" 23:59:59"));
			}
			
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append(" and b.mobile =?");
				param.add(mobile);
			}
				
			String sql=" from t_pay_record a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return PayRecord.dao.paginate(page, size, select, sql,param.toArray());
		}
		
		public List<PayRecord> queryRecords(int pageSize,int pageNum,int memberId,String date){
			int fromRow = pageSize*(pageNum-1);
			if(StringUtil.isNoneBlank(date)){
				return PayRecord.dao.find("select * from t_pay_record where member_id ="+memberId+" and status != '220001' and create_time like '%"+date+"%' order by create_time desc limit "+fromRow+","+pageSize);
			}else{
				return PayRecord.dao.find("select * from t_pay_record where member_id ="+memberId+" and status != '220001' order by create_time desc limit "+fromRow+","+pageSize);
			}
		}
}
