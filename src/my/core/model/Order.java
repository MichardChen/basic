package my.core.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

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
	
	public boolean del(int id){
		return Order.dao.deleteById(id);
	}
	
	public List<Order> queryBuyNewTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		return Order.dao.find("select * from t_order where member_id="+userId+" and create_time '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public List<Order> querySaleTeaRecord(int pageSize,int pageNum,int userId,String date){
		int fromRow = (pageNum-1)*pageSize;
		return Order.dao.find("select a.* from t_order a inner join t_order_item b on a.id=b.order_id where b.sale_user_id="+userId+" and b.sale_user_type !='010002' and a.create_time like '%"+date+"%' order by update_time desc limit "+fromRow+","+pageSize);
	}
}
