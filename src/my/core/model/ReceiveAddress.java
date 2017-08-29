package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_receive_address", pk = "id")
public class ReceiveAddress extends Model<ReceiveAddress> {
	
	public static final ReceiveAddress dao = new ReceiveAddress();

	public Page<ReceiveAddress> queryByPage(int page,int size,int memberId,String status){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and member_id=?");
		strBuf.append(" and status=?");
		param.add(memberId);
		param.add(status);
		String sql=" from t_receive_address where 1=1"+strBuf+" order by default_flg desc,create_time desc";
		String select="select * ";
		return ReceiveAddress.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public ReceiveAddress queryById(int id,String status){
		return ReceiveAddress.dao.findFirst("select * from t_receive_address where id = ? and status='"+status+"'",id);
	}
	
	public boolean updateInfo(ReceiveAddress address){
		return new ReceiveAddress().setAttrs(address).update();
	}
	
	public boolean saveInfo(ReceiveAddress address){
		return new ReceiveAddress().setAttrs(address).save();
	}
	
	public boolean del(int id){
		return ReceiveAddress.dao.deleteById(id);
	}
}
