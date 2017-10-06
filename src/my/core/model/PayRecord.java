package my.core.model;

import java.math.BigDecimal;

import my.pvcloud.util.DateUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

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
}
