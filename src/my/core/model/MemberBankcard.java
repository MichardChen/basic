package my.core.model;

import java.math.BigDecimal;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.sun.swing.internal.plaf.basic.resources.basic;

@TableBind(table = "t_member_bankcard", pk = "id")
public class MemberBankcard extends Model<MemberBankcard> {
	
	public static final MemberBankcard dao = new MemberBankcard();
	
	public MemberBankcard queryById(int id){
		return MemberBankcard.dao.findFirst("select * from t_member_bankcard where id = ?",id);
	}
	
	public MemberBankcard queryByMemberId(int id){
		return MemberBankcard.dao.findFirst("select * from t_member_bankcard where member_id = ?",id);
	}
	
	public boolean updateInfo(MemberBankcard data){
		return new MemberBankcard().setAttrs(data).update();
	}
	
	public boolean saveInfo(MemberBankcard data){
		return new MemberBankcard().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return MemberBankcard.dao.deleteById(id);
	}
}