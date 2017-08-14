package my.core.model;

import java.math.BigDecimal;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_card_record", pk = "id")
public class CardRecord extends Model<CardRecord> {
		
	public static final CardRecord dao = new CardRecord();
	
	public int saveRecord(String cardNo,BigDecimal fee,String typeCd,String mark){
		CardRecord cr = new CardRecord().set("card_no", cardNo).set("fee", fee).set("type_cd", typeCd).set("mark", mark).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp());
		boolean isSave = cr.save();
		return cr.getInt("id");
	}
}