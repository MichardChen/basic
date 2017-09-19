package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_sale_order", pk = "id")
public class SaleOrder extends Model<SaleOrder> {
	
	public static final SaleOrder dao = new SaleOrder();

	public SaleOrder queryById(int id){
		return SaleOrder.dao.findFirst("select * from t_sale_order where id = ?",id);
	}
	
	public SaleOrder queryByOrderNo(String orderNo){
		return SaleOrder.dao.findFirst("select * from t_sale_order where order_no = ?",orderNo);
	}
	
	public boolean updateInfo(SaleOrder order){
		return new SaleOrder().setAttrs(order).update();
	}
	
	public boolean saveInfo(SaleOrder order){
		return new SaleOrder().setAttrs(order).save();
	}
	
	public boolean del(int id){
		return SaleOrder.dao.deleteById(id);
	}
}
