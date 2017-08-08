package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "s_role", pk = "role_id")
public class Role extends Model<Role> {
	public static final Role dao = new Role();
}
