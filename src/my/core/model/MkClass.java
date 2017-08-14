package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_class", pk = "id")
public class MkClass extends Model<MkClass> {
		
	public static final MkClass dao = new MkClass();
		
	public List<MkClass> queryClassList(int pageNum,int pageSize){
		int fromRow = pageSize*(pageNum-1);
		return MkClass.dao.find("select * from mk_class order by create_time desc limit "+fromRow+","+pageSize);
	}
}