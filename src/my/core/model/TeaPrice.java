package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_tea_price", pk = "id")
public class TeaPrice extends Model<TeaPrice> {
	
	public static final TeaPrice dao = new TeaPrice();
	
	public TeaPrice queryById(int id){
		return TeaPrice.dao.findFirst("select * from t_tea_price where id = ?",id);
	}
	
	public TeaPrice queryByTeaId(int teaId){
		return TeaPrice.dao.findFirst("select * from t_tea_price where tea_id = ? order by expire_time desc",teaId);
	}
	
	public boolean updateInfo(TeaPrice data){
		return new TeaPrice().setAttrs(data).update();
	}
	
	public boolean saveInfo(TeaPrice data){
		return new TeaPrice().setAttrs(data).save();
	}

}
