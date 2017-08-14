package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_comment_items", pk = "id")
public class CommentItems extends Model<CommentItems> {
		
	public static final CommentItems dao = new CommentItems();
	
	public int saveCommentItems(int commentId,String itemCode){
		CommentItems ct = new CommentItems().set("comment_id", commentId).set("item_code", itemCode).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp());
		boolean isSave = ct.save();
		return ct.getInt("id");
	}
	
	public CommentItems queryById(int id){
		return CommentItems.dao.findFirst("select * from mk_comment_items where id=?",id);
	}
}
