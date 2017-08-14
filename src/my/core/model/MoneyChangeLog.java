package my.core.model;

import java.math.BigDecimal;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_moneychange_log", pk = "id")
public class MoneyChangeLog extends Model<MoneyChangeLog> {
		
		public static final MoneyChangeLog dao = new MoneyChangeLog();
		public int addMoneyChangeLog(String typeCd,BigDecimal money,int memberId,String mark,String orderNo,String cardNo){
			MoneyChangeLog log = new MoneyChangeLog().set("type_cd", typeCd).set("moneys", money).set("member_id", memberId).set("mark", mark).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("order_no", orderNo).set("card_no", cardNo);
			boolean isSave = log.save();
			return log.getInt("id");
		}
}
