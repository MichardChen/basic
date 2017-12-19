package my.core.model;

import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_cash_journal", pk = "id")
public class CashJournal extends Model<CashJournal> {
	
	public static final CashJournal dao = new CashJournal();

	
	public CashJournal queryById(int id) {
		return CashJournal.dao.findFirst("select * from t_cash_journal where id = ?", id);
	}
	
	public boolean updateInfo(CashJournal data){
		return new CashJournal().setAttrs(data).update();
	}
	
	public boolean saveInfo(CashJournal data){
		return new CashJournal().setAttrs(data).save();
	}
	
	public int updateStatus(String outTradeNo,String status,String tradeNo){
		return Db.update("update t_cash_journal set fee_status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"',trade_no='"+tradeNo+"' where cash_journal_no='"+outTradeNo+"'");
	}
	
	public String queryCurrentCashNo(){
		String date = DateUtil.getDateTimeNO();
		String sql="select cash_journal_no from t_cash_journal where occur_date='"+date+"' order by cash_journal_no desc limit 1";
		String nowCahsNo = Db.queryStr(sql);
		if(StringUtil.isNoneBlank(nowCahsNo)){
			return StringUtil.toString(new Long(nowCahsNo)+1);
		}else{
			return date+"000001";
		}
	}
	
	public List<CashJournal> queryRecords(int pageSize,int pageNum,int memberId,String date){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(date)){
			return CashJournal.dao.find("select * from t_cash_journal where member_id ="+memberId+" and create_time like '%"+date+"%' and fee_status='300002' order by create_time desc limit "+fromRow+","+pageSize);
		}else{
			return CashJournal.dao.find("select * from t_cash_journal where member_id ="+memberId+" and fee_status='300002' order by create_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public Page<CashJournal> queryByPage(int page,int size){
			
		String sql=" from t_cash_journal where 1=1 order by create_time desc,cash_journal_no desc";
		String select="select * ";
		return CashJournal.dao.paginate(page, size, select, sql);
	}
	
	public Page<CashJournal> queryByPageParams(int page,int size,String piType,String status,String time,String mobile,String name){
		
		if(StringUtil.isBlank(mobile)){
			List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(piType)){
				strBuf.append(" and pi_type='"+piType+"'");
			}
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and fee_status='"+status+"'");
			}
			if(StringUtil.isNoneBlank(time)){
				strBuf.append(" and occur_date='"+time+"'");
			}
				
			String sql=" from t_cash_journal where 1=1 "+strBuf+" order by create_time desc,cash_journal_no desc";
			String select="select * ";
			return CashJournal.dao.paginate(page, size, select, sql);
		}else{
			List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(piType)){
				strBuf.append(" and a.pi_type='"+piType+"'");
			}
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.fee_status='"+status+"'");
			}
			if(StringUtil.isNoneBlank(time)){
				strBuf.append(" and a.occur_date='"+time+"'");
			}
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append(" and b.mobile='"+mobile+"'");	
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append(" and b.name='"+name+"'");	
			}
			
			String sql=" from t_cash_journal a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return CashJournal.dao.paginate(page, size, select, sql);
		}
	}
	
	public List<CashJournal> exportData(String piType,String status,String time,String mobile,String name){
		
		if(StringUtil.isBlank(mobile)){
			List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(piType)){
				strBuf.append(" and pi_type='"+piType+"'");
			}
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and fee_status='"+status+"'");
			}
			if(StringUtil.isNoneBlank(time)){
				strBuf.append(" and occur_date='"+time+"'");
			}
				
			String sql=" from t_cash_journal where 1=1 "+strBuf+" order by create_time desc,cash_journal_no desc";
			String select="select * ";
			return CashJournal.dao.find(select+sql);
		}else{
			List<Object> param=new ArrayList<Object>();
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(piType)){
				strBuf.append(" and a.pi_type='"+piType+"'");
			}
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.fee_status='"+status+"'");
			}
			if(StringUtil.isNoneBlank(time)){
				strBuf.append(" and a.occur_date='"+time+"'");
			}
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append(" and b.mobile='"+mobile+"'");	
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append(" and b.name='"+name+"'");	
			}
			
			String sql=" from t_cash_journal a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return CashJournal.dao.find(select+sql);
		}
	}
}
