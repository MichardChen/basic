package my.core.model;

import java.util.List;

import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@TableBind(table = "t_sale_order", pk = "id")
public class SaleOrder extends Model<SaleOrder> {
	
	public static final SaleOrder dao = new SaleOrder();

	public SaleOrder queryById(int id){
		return SaleOrder.dao.findFirst("select * from t_sale_order where id = ?",id);
	}
	
	public SaleOrder queryByOrderNo(String orderNo){
		return SaleOrder.dao.findFirst("select * from t_sale_order where order_no = ?",orderNo);
	}
	
	public List<SaleOrder> queryMemberOrders(int memberId,int pageSize,int pageNum,String status){
		int fromRow = (pageNum-1)*pageSize;
		return SaleOrder.dao.find("select a.* from t_sale_order a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where b.member_id = ? and a.status=? order by a.create_time desc limit ?,?",memberId,status,fromRow,pageSize);
	}
	
	public List<SaleOrder> queryMemberSaleOrders(int memberId,int pageSize,int pageNum,String date){
		int fromRow = (pageNum-1)*pageSize;
		return SaleOrder.dao.find("select a.* from t_sale_order a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where b.member_id = ? and a.create_time like '%"+date+"%' order by a.create_time desc limit ?,?",memberId,fromRow,pageSize);
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
	
	public Page<SaleOrder> queryByPageParams(int page,int size,String date){
		
		StringBuffer strBuf=new StringBuffer();
		
		if(StringUtil.isNoneBlank(date)){
			strBuf.append(" and create_time like '%"+date+"%'");
		}
			
		String sql=" from t_sale_order where 1=1 "+strBuf+" order by create_time desc";
		String select="select * ";
		return SaleOrder.dao.paginate(page, size, select, sql);
	}
	
	public Page<SaleOrder> queryByPage(int page,int size){
		String sql=" from t_sale_order where 1=1 order by create_time desc";
		String select="select * ";
		return SaleOrder.dao.paginate(page, size, select, sql);
	}

	public List<Record> queryPriceTrendAvg(String date,int teaId){
		String sql = "SELECT AVG(a.price) as price,SUM(a.quality) as quality,DATE_FORMAT(a.create_time,'%Y-%m-%d') as createTime from t_sale_order a "
					+"LEFT  JOIN t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id "
				    +"LEFT JOIN t_tea c on b.tea_id=c.id "
					+"WHERE a.create_time like '%"+date+"%' AND c.id="+teaId+" "
					+"GROUP BY DATE_FORMAT(a.create_time,'%Y-%m-%d') "
					+"ORDER BY DATE_FORMAT(a.create_time, '%Y-%m-%d') DESC";
		
		List<Record> models = Db.find(sql);
		return models;
	}
}
