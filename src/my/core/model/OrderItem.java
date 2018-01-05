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

	public OrderItem queryById(int id) {
		return OrderItem.dao.findFirst("select * from t_order_item where id = ?", id);
	}

	public OrderItem queryByOrderId(int orderId) {
		return OrderItem.dao.findFirst("select * from t_order_item where order_id = ?", orderId);
	}

	public Page<OrderItem> queryByPage(int page, int size) {
		String sql = " from t_order_item where 1=1 order by create_time desc";
		String select = "select * ";
		return OrderItem.dao.paginate(page, size, select, sql);
	}

	public Page<OrderItem> queryByPageParams(int page, int size, String date, String orderNo, String status,
			String payTime, int userId, String saleUserTypeCd, int buyUserId) {

		StringBuffer strBuf = new StringBuffer();

		if (StringUtil.isNoneBlank(date)) {
			strBuf.append(" and a.create_time like '%" + date + "%'");
		}
		if (StringUtil.isNoneBlank(orderNo)) {
			strBuf.append(" and b.order_no='" + orderNo + "'");
		}
		if (StringUtil.isNoneBlank(status)) {
			strBuf.append(" and b.order_status='" + status + "'");
		}
		if (StringUtil.isNoneBlank(payTime)) {
			strBuf.append(" and b.pay_time like '%" + payTime + "%'");
		}
		if (userId != 0) {
			strBuf.append(" and a.sale_id=" + userId);
			if (StringUtil.isNoneBlank(saleUserTypeCd)) {
				strBuf.append(" and a.sale_user_type='" + saleUserTypeCd + "'");
			}
		}
		if (buyUserId != 0) {
			strBuf.append(" and a.member_id=" + buyUserId);
		}

		String sql = " from t_order_item a inner join t_order b on a.order_id=b.id where 1=1 " + strBuf
				+ " order by b.create_time desc";
		String select = "select a.* ";
		return OrderItem.dao.paginate(page, size, select, sql);
	}

	public List<OrderItem> exportData(String date, String orderNo, String status,String payTime, int userId, String saleUserTypeCd, int buyUserId) {

		StringBuffer strBuf = new StringBuffer();

		if (StringUtil.isNoneBlank(date)) {
			strBuf.append(" and a.create_time like '%" + date + "%'");
		}
		if (StringUtil.isNoneBlank(orderNo)) {
			strBuf.append(" and b.order_no='" + orderNo + "'");
		}
		if (StringUtil.isNoneBlank(status)) {
			strBuf.append(" and b.order_status='" + status + "'");
		}
		if (StringUtil.isNoneBlank(payTime)) {
			strBuf.append(" and b.pay_time like '%" + payTime + "%'");
		}
		if (userId != 0) {
			strBuf.append(" and a.sale_id=" + userId);
			if (StringUtil.isNoneBlank(saleUserTypeCd)) {
				strBuf.append(" and a.sale_user_type='" + saleUserTypeCd + "'");
			}
		}
		if (buyUserId != 0) {
			strBuf.append(" and a.member_id=" + buyUserId);
		}

		String sql = " from t_order_item a inner join t_order b on a.order_id=b.id where 1=1 " + strBuf
				+ " order by b.create_time desc";
		String select = "select a.* ";
		return OrderItem.dao.find(select+sql);
	}

	public boolean updateInfo(OrderItem data) {
		return new OrderItem().setAttrs(data).update();
	}

	public int saveInfo(OrderItem data) {
		OrderItem t = new OrderItem().setAttrs(data);
		t.save();
		return t.getInt("id");
	}

	public boolean del(int id) {
		return OrderItem.dao.deleteById(id);
	}

	public BigDecimal sumOrderQuality(int orderId) {
		BigDecimal sum = Db.queryBigDecimal("select sum(quality) from t_order_item where order_id=" + orderId);
		if (sum == null) {
			return new BigDecimal("0");
		}
		return sum;
	}

	public BigDecimal sumOrderAmount(int orderId) {
		BigDecimal sum = Db.queryBigDecimal("select sum(item_amount) from t_order_item where order_id=" + orderId);
		if (sum == null) {
			return new BigDecimal("0");
		}
		return sum;
	}

	public List<OrderItemModel> queryPriceAnalysis(int teaId, String time1, String time2) {
		return Db
				.query("SELECT AVG(a.item_amount) as amount,DATE_FORMAT(a.create_time,'%Y-%m-%d') as date from  t_order_item a inner join t_warehouse_tea_member_item b on a.wtm_item_id=b.id  inner join t_warehouse_tea_member c on b.warehouse_tea_member_id=c.tea_id   where a.create_time>='"
						+ time1 + "' and a.create_time<='" + time2 + "' and c.tea_id=" + teaId
						+ " GROUP BY DATE_FORMAT(a.create_time,'%Y-%m-%d') order by a.create_time asc");
	}

	public List<OrderItem> queryBuyNewTeaRecord(int pageSize, int pageNum, int userId, String date) {
		int fromRow = (pageNum - 1) * pageSize;
		if (StringUtil.isNoneBlank(date)) {
			return OrderItem.dao
					.find("select * from t_order_item where member_id=" + userId + " and create_time like '%" + date
							+ "%' order by update_time desc limit " + fromRow + "," + pageSize);
		} else {
			return OrderItem.dao.find("select * from t_order_item where member_id=" + userId
					+ " order by update_time desc limit " + fromRow + "," + pageSize);
		}
	}

	public List<OrderItem> querySaleTeaRecord(int pageSize, int pageNum, int userId, String date) {
		int fromRow = (pageNum - 1) * pageSize;
		if (StringUtil.isNoneBlank(date)) {
			return OrderItem.dao.find("select * from t_order_item where sale_id=" + userId + " and create_time like '%"
					+ date + "%' order by update_time desc limit " + fromRow + "," + pageSize);
		} else {
			return OrderItem.dao.find("select * from t_order_item where sale_id=" + userId
					+ " order by update_time desc limit " + fromRow + "," + pageSize);
		}
	}

	public List<OrderItem> queryOrderItemList(int pageSize, int pageNum, int userId, String date) {
		int fromRow = (pageNum - 1) * pageSize;
		return OrderItem.dao.find("select * from t_order_item where member_id=" + userId + " and create_time like '%"
				+ date + "%' and sale_user_type='010002' order by update_time desc limit " + fromRow + "," + pageSize);
	}

	public List<OrderItem> queryAllOrderItemList(int pageSize, int pageNum, String date,int storeId){
		int fromRow = (pageNum - 1) * pageSize;
		if(storeId != 0){
			if(StringUtil.isNoneBlank(date)){
				return OrderItem.dao.find("select a.* from t_order_item a inner join t_member b on a.member_id=b.id where a.create_time like '%" + date
						+ "%' and a.sale_user_type='010002' and b.store_id="+storeId+" order by a.update_time desc limit " + fromRow + "," + pageSize);
			}else{
				return OrderItem.dao.find("select a.* from t_order_item a inner join t_member b on a.member_id=b.id where a.sale_user_type='010002' and b.store_id="+storeId+" order by a.update_time desc limit " + fromRow + "," + pageSize);
			
			}
		}else{
			return null;
		}
	}
	
	public Page<OrderItem> querySellOrderItemList(int pageSize, int pageNum) {
		String select = "select *";
		String sql = " from t_order_item where sale_user_type='010002' order by update_time desc";
		return OrderItem.dao.paginate(pageNum, pageSize, select, sql);
	}
	
	public Page<OrderItem> querySellOrderItemListByParams(int pageSize, int pageNum,String date,String buyMobile,String sellMobile,int sellId) {
		if(StringUtil.isNoneBlank(date)){
			String select = "select a.* ";
			String sql = " from t_order_item a inner join t_member b on a.member_id=b.id where a.create_time like '%" + date
					+ "%' and a.sale_user_type='010002'";
			if(StringUtil.isNoneBlank(buyMobile)){
				sql = sql + " and b.mobile='"+buyMobile+"'";
			}
			
			if(StringUtil.isNoneBlank(sellMobile)&&(sellId != 0)){
				sql = sql + " and b.store_id="+sellId;
			}
			
			sql = sql + " order by update_time desc";
			return  OrderItem.dao.paginate(pageNum, pageSize, select, sql);
		}else{
			String select = "select a.* ";
			String sql = " from t_order_item a inner join t_member b on a.member_id=b.id where a.sale_user_type='010002'";
			if(StringUtil.isNoneBlank(buyMobile)){
				sql = sql + " and b.mobile='"+buyMobile+"'";
			}
			if(StringUtil.isNoneBlank(sellMobile)&&(sellId != 0)){
				sql = sql + " and b.store_id="+sellId;
			}
			sql = sql + " order by update_time desc";
			return  OrderItem.dao.paginate(pageNum, pageSize, select, sql);
		}
	}
	
	public List<OrderItem> exportSellData(String date,String buyMobile,String sellMobile,int sellId) {
		if(StringUtil.isNoneBlank(date)){
			String select = "select a.* ";
			String sql = " from t_order_item a inner join t_member b on a.member_id=b.id where a.create_time like '%" + date
					+ "%' and a.sale_user_type='010002'";
			if(StringUtil.isNoneBlank(buyMobile)){
				sql = sql + " and b.mobile='"+buyMobile+"'";
			}
			
			if(StringUtil.isNoneBlank(sellMobile)&&(sellId != 0)){
				sql = sql + " and b.store_id="+sellId;
			}
			
			sql = sql + " order by update_time desc";
			return  OrderItem.dao.find(select+sql);
		}else{
			String select = "select a.* ";
			String sql = " from t_order_item a inner join t_member b on a.member_id=b.id where a.sale_user_type='010002'";
			if(StringUtil.isNoneBlank(buyMobile)){
				sql = sql + " and b.mobile='"+buyMobile+"'";
			}
			if(StringUtil.isNoneBlank(sellMobile)&&(sellId != 0)){
				sql = sql + " and b.store_id="+sellId;
			}
			sql = sql + " order by update_time desc";
			return  OrderItem.dao.find(select+sql);
		}
	}
}
