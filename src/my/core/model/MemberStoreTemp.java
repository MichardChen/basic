package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_member_store_temp", pk = "id")
public class MemberStoreTemp extends Model<MemberStoreTemp> {
	
	public static final MemberStoreTemp dao = new MemberStoreTemp();

	public MemberStoreTemp queryById(int id){
		return MemberStoreTemp.dao.findFirst("select * from t_member_store_temp where id = ?",id);
	}
	
	public MemberStoreTemp queryByMobile(String mobile){
		return MemberStoreTemp.dao.findFirst("select * from t_member_store_temp where mobile = ?",mobile);
	}
	
	public boolean updateInfo(MemberStoreTemp data){
		return new MemberStoreTemp().setAttrs(data).update();
	}
	
	public boolean saveInfo(MemberStoreTemp data){
		return new MemberStoreTemp().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return MemberStoreTemp.dao.deleteById(id);
	}

}
