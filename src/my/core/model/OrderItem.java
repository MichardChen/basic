package my.core.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_order_item", pk = "id")
public class OrderItem extends Model<OrderItem> {

	public static final OrderItem dao = new OrderItem();
	
	public OrderItem queryById(int id){
		return OrderItem.dao.findFirst("select * from t_order_item where id = ?",id);
	}
	
	public Page<OrderItem> queryByPage(int page,int size){
			String sql=" from t_order_item where 1=1 order by create_time desc";
			String select="select * ";
			return OrderItem.dao.paginate(page, size, select, sql);
	}
	
	public Page<OrderItem> queryByPageParams(int page,int size,String date){
		
		StringBuffer strBuf=new StringBuffer();
		
		if(StringUtil.isNoneBlank(date)){
			strBuf.append(" and create_time like '%"+date+"%'");
		}
			
		String sql=" from t_order_item where 1=1 "+strBuf+" order by create_time desc";
		String select="select * ";
		return OrderItem.dao.paginate(page, size, select, sql);
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
	
	public BigDecimal sumOrderAmount(int orderId){
		BigDecimal sum = Db.queryBigDecimal("select sum(item_amount) from t_order_item where order_id="+orderId);
		if(sum == null){
			return new BigDecimal("0");
		}
		return sum;
	}
	
	public List<OrderItemModel> queryPriceAnalysis(int teaId,String time1,String time2){
		return Db.query("SELECT AVG(item_amount) as amount,DATE_FORMAT(create_time,'%Y-%m-%d') as date from  t_order_item  where create_time>='"+time1+"' and create_time<='"+time2+"' and tea_id="+teaId+" GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') order by create_time asc");
	}
	
	public List<OrderItem> queryBuyNewTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		if(StringUtil.isNoneBlank(date)){
			return OrderItem.dao.find("select * from t_order_item where member_id="+userId+" and create_time like '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
		}else{
			return OrderItem.dao.find("select * from t_order_item where member_id="+userId+" order by update_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public List<OrderItem> querySaleTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		if(StringUtil.isNoneBlank(date)){
			return OrderItem.dao.find("select * from t_order_item where sale_user_id="+userId+" and create_time like '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
		}else{
			return OrderItem.dao.find("select * from t_order_item where sale_user_id="+userId+" order by update_time desc limit "+fromRow+","+pageSize);
		}
	}
}
