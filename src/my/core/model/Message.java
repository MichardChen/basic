package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.StringUtil;

@TableBind(table = "t_message", pk = "id")
public class Message extends Model<Message> {
		
		public static final Message dao = new Message();
		
		public Message queryMessageById(int id){
			return Message.dao.findFirst("select * from t_message where id=?",id);
		}
		
		public boolean saveInfo(Message data){
			return new Message().setAttrs(data).save();
		}
		
		public List<Message> queryMessages(int userId,String typeCd){
			if(StringUtil.isBlank(typeCd)){
				return Message.dao.find("select * from t_message where user_id=? order by update_time desc",userId);
			}
			return Message.dao.find("select * from t_message where user_id=? and message_type_cd=? order by update_time desc",userId,typeCd);
		}
}
