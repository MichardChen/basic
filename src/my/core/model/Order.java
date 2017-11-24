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
import com.jfinal.plugin.activerecord.Record;

@TableBind(table = "t_order", pk = "id")
public class Order extends Model<Order> {
	
	public static final Order dao = new Order();

	public Page<Order> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_order where 1=1 order by create_time desc";
		String select="select * ";
		return Order.dao.paginate(page, size, select, sql);
	}
	
	public Page<Order> queryByPageParams(int page,int size,String title){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		
		if(StringUtil.isNoneBlank(title)){
			strBuf.append(" and create_time>=?");
			Timestamp createTime = DateUtil.formatStringForTimestamp(title+" 00:00:00");
			param.add(createTime);
		}
			
			
			String sql=" from t_order where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return Order.dao.paginate(page, size, select, sql,param.toArray());
		}
	
	public Order queryById(int id){
		return Order.dao.findFirst("select * from t_order where id = ?",id);
	}
	
	public boolean updateInfo(Order order){
		return new Order().setAttrs(order).update();
	}
	
	public boolean saveInfo(Order order){
		return new Order().setAttrs(order).save();
	}
	
	public Order addInfo(Order order){
		Order o = new Order().setAttrs(order);
		o.save();
		return o;
	}
	
	public boolean del(int id){
		return Order.dao.deleteById(id);
	}
	
	public List<Order> queryBuyNewTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		if(StringUtil.isNoneBlank(date)){
			return Order.dao.find("select * from t_order where member_id="+userId+" and create_time like '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
		}else{
			return Order.dao.find("select * from t_order where member_id="+userId+" order by update_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public List<Order> querySaleTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		if(StringUtil.isNoneBlank(date)){
			return Order.dao.find("select a.* from t_order a inner join t_order_item b on a.id=b.order_id where b.sale_user_id="+userId+" and b.sale_user_type !='010002' and a.create_time like '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
		}else{
			return Order.dao.find("select a.* from t_order a inner join t_order_item b on a.id=b.order_id where b.sale_user_id="+userId+" and b.sale_user_type !='010002' order by update_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public List<Order> queryOrderByTime(String date,String orderStatus,int teaId){
		return Order.dao.find("select * from t_order where create_time like '%"+date+"%' and order_status='"+orderStatus+"'");
	}
	
	//成交走势，详细数据
	public List<Record> queryBargainTrendAvg(String date1,String date2,int teaId,String size){
		String d1 = date1+" 00:00:00";
		String d2 = date2+" 23:59:59";
		String sql = "SELECT "+
				"AVG(a.item_amount) as allAmount,SUM(a.quality) as quality,a.create_time,DATE_FORMAT(a.create_time, '%Y-%m-%d') as createTime "+
				"FROM t_order_item a "+
				"LEFT JOIN t_warehouse_tea_member_item b ON a.wtm_item_id = b.id "+
				"LEFT JOIN t_warehouse_tea_member c on b.warehouse_tea_member_id=c.id "+
				"LEFT JOIN t_tea d ON d.id = c.tea_id "+
				"WHERE a.create_time>='"+d1+"' AND a.create_time<='"+d2+"' AND d.id="+teaId+" AND c.member_type_cd!='010002'"+" AND b.size_type_cd='"+size+"' "+
				"GROUP BY "+
				"DATE_FORMAT(a.create_time, '%Y-%m-%d') "+
				"ORDER BY "+
				"DATE_FORMAT(a.create_time, '%Y-%m-%d') DESC";
		
		List<Record> models = Db.find(sql);
		return models;
	}
	
	public List<Record> queryBargainTrendAvgByDate(String date1,int teaId,String size){
		String sql = "SELECT "+
				"AVG(a.item_amount) as allAmount,SUM(a.quality) as quality,a.create_time,DATE_FORMAT(a.create_time, '%Y-%m-%d') as createTime "+
				"FROM t_order_item a "+
				"LEFT JOIN t_warehouse_tea_member_item b ON a.wtm_item_id = b.id "+
				"LEFT JOIN t_warehouse_tea_member c on b.warehouse_tea_member_id=c.id "+
				"LEFT JOIN t_tea d ON d.id = c.tea_id "+
				"WHERE a.create_time like '%"+date1+"%' AND d.id="+teaId+" AND c.member_type_cd!='010002'"+" AND b.size_type_cd='"+size+"' "+
				"GROUP BY "+
				"DATE_FORMAT(a.create_time, '%Y-%m-%d') "+
				"ORDER BY "+
				"DATE_FORMAT(a.create_time, '%Y-%m-%d') DESC";
		
		List<Record> models = Db.find(sql);
		return models;
	}
	
	//成交总量和成交总额
	public List<Record> queryBargainSum(String date1,String date2,int teaId,String size){
		String d1 = date1+" 00:00:00";
		String d2 = date2+" 23:59:59";
		String sql = "SELECT "+
					 "SUM(b.quality) as quality,SUM(a.pay_amount) as amount "+
					 "FROM t_order a LEFT JOIN t_order_item b on a.id=b.order_id "+
					 "LEFT JOIN t_warehouse_tea_member_item d on b.wtm_item_id=d.id "+
					 "LEFT JOIN t_warehouse_tea_member e on d.warehouse_tea_member_id=e.id "+
					 "WHERE a.create_time>='"+d1+"' AND a.create_time<='"+d2+"' AND e.member_type_cd!='010002'"+" AND d.size_type_cd='"+size+"' "+ 
					 "AND a.order_status = '140003' "+
					 "AND e.tea_id="+teaId;
		
		List<Record> models = Db.find(sql);
		return models;
	}
}
