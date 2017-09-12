package my.core.model;

import java.math.BigDecimal;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.google.common.collect.Ordering;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_order_item", pk = "id")
public class OrderItem extends Model<OrderItem> {

	public static final OrderItem dao = new OrderItem();
	
	public OrderItem queryById(int id){
		return OrderItem.dao.findFirst("select * from t_order_item where id = ?",id);
	}
	
	public boolean updateInfo(OrderItem data){
		return new OrderItem().setAttrs(data).update();
	}
	
	public boolean saveInfo(OrderItem data){
		return new OrderItem().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return OrderItem.dao.deleteById(id);
	}
	
	public BigDecimal sumOrderQuality(int orderId){
		BigDecimal sum = Db.queryBigDecimal("select sum(quality) from t_order_item where order_id="+orderId);
		if(sum == null){
			return new BigDecimal("0");
		}
		return sum;
	}
	
	public List<OrderItem> queryPriceAnalysis(int teaId,String time1,String time2){
		return OrderItem.dao.find("SELECT AVG(item_amount) as amount,DATE_FORMAT(create_time,'%Y-%m-%d') as date from  t_order_item  GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') where create_time>='"+time1+"' and create_time<='"+time2+"' and tea_id="+teaId+" order by create_time desc");
	}
}
