package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_store", pk = "id")
public class Store extends Model<Store> {
			
	public static final Store dao = new Store();
	
	public List<Store> queryStoreList(){
		return Store.dao.find("select * from mk_store");
	}
}
