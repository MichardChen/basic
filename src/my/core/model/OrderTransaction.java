package my.core.model;


import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_order_transaction", pk = "id")
public class OrderTransaction extends Model<OrderTransaction> {
			
	public static final OrderTransaction dao = new OrderTransaction();
	
	public int saveTransaction(int orderId,String status,String mark){
		OrderTransaction st = new OrderTransaction().set("order_id", orderId).set("status", status).set("mark", mark).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp());
		boolean isSave = st.save();
		return st.getInt("id");
	}
}
