package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_store", pk = "id")
public class Store extends Model<Store> {
	
	public static final Store dao = new Store();

}
