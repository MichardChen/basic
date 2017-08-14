package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_order_comment", pk = "id")
public class OrderComment extends Model<OrderComment> {
		
	public static final OrderComment dao = new OrderComment();
	
	public int saveComment(int orderId,String comment){
		OrderComment ct = new OrderComment().set("order_id", orderId).set("comment", comment).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("valid", 0);
		boolean isSave = ct.save();
		return ct.getInt("id");
	}
	
	public List<OrderComment> queryCommentList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return OrderComment.dao.find("select * from mk_order_comment order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public Long queryComentListCount(){
		return Db.queryLong("select count(*) from mk_order_comment");
	}
	
	public int updateCommentStatus(int commentId,int valid){
		return Db.update("update mk_order_comment set valid="+valid+" where id="+commentId);
	}
		
	public OrderComment queryById(int orderId,int valid){
		return OrderComment.dao.findFirst("select * from mk_order_comment where order_id="+orderId+" and valid="+valid+" limit 1");
	}
}
