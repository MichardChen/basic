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

	public boolean updateInfo(CashJournal data){
		return new CashJournal().setAttrs(data).update();
	}
	
	public boolean saveInfo(CashJournal data){
		return new CashJournal().setAttrs(data).save();
	}
	
	public int updateStatus(String outTradeNo,String status){
		return Db.update("update t_cash_journal set fee_status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where cash_journal_no='"+outTradeNo+"'");
	}
	
	public Page<CashJournal> queryByPage(int page,int size){
			
		String sql=" from t_cash_journal where 1=1 order by create_time desc";
		String select="select * ";
		return CashJournal.dao.paginate(page, size, select, sql);
	}
	
	public Page<CashJournal> queryByPageParams(int page,int size,String piType,String status,String time){
		
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
			
		String sql=" from t_cash_journal where 1=1 "+strBuf+" order by create_time desc";
		String select="select * ";
		return CashJournal.dao.paginate(page, size, select, sql);
	}
}
