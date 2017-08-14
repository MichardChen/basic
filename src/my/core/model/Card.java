package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_card", pk = "id")
public class Card extends Model<Card> {
			
	public static final Card dao = new Card();
			
	public Card queryCard(int id){
		return Card.dao.findFirst("select * from mk_card where id=?",id);
	}
	
	public Card queryCardType(String code){
		return Card.dao.findFirst("select * from mk_card where type_cd=?",code);
	}
	
	public List<Card> queryAllCard(){
		return Card.dao.find("select * from mk_card");
	}
}
