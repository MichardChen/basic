package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_message", pk = "id")
public class Message extends Model<Message> {
		
	public static final Message dao = new Message();
		
	public List<Message> queryMessage(int pageNum,int pageSize,String toUserTypeCd,int userId){
		int fromRow = pageSize*(pageNum-1);
		return Message.dao.find("select * from mk_message where to_user_id="+userId+" and to_user_type_cd='"+toUserTypeCd+"' order by create_time limit "+fromRow+","+pageSize);
	}
}
