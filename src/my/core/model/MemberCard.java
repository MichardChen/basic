package my.core.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_member_card", pk = "id")
public class MemberCard extends Model<MemberCard> {
			
	public static final MemberCard dao = new MemberCard();
	
	public List<MemberCard> queryMemberCard(int id,Date date){
		return MemberCard.dao.find("select * from mk_member_card where member_id=? and moneys > 0 and expire_time>=?",id,date);
	}
	
	public MemberCard queryCard(int userId,int cardId){
		return MemberCard.dao.findFirst("select * from mk_member_card where member_id=? and card_id=?",userId,cardId);
	}
	
	public MemberCard queryCardByNo(int userId,String cardNo){
		return MemberCard.dao.findFirst("select * from mk_member_card where member_id=? and card_no=?",userId,cardNo);
	}
	
	//更新卡券余额
	public int updateMoneys(int userId,String cardNo,BigDecimal moneys){
		return Db.update("update mk_member_card set moneys="+moneys+",update_time='"+DateUtil.getNowTimestamp()+"' where member_id="+userId+" and card_no='"+cardNo+"'");
	}
}
