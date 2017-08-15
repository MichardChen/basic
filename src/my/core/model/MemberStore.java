package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_member_store", pk = "id")
public class MemberStore extends Model<MemberStore> {
	
	public static final MemberStore dao = new MemberStore();
}