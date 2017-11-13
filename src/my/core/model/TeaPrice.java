package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.StringUtil;

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

	public Page<TeaPrice> queryByPage(int page,int size){
		
		String sql=" from t_tea_price where 1=1 order by create_time desc";
		String select="select * ";
		return TeaPrice.dao.paginate(page, size, select, sql);
	}
	
	public Page<TeaPrice> queryByPageParams(int page,int size,String name){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isBlank(name)){
			String sql=" from t_tea_price where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return TeaPrice.dao.paginate(page, size, select, sql);
		}else{
			String sql=" from t_tea_price a inner join t_tea b on a.tea_id=b.id where 1=1 and b.tea_title like '%"+name+"%' order by a.create_time desc";
			String select="select * ";
			return TeaPrice.dao.paginate(page, size, select, sql);
		}
	}
}
