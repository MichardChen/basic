package my.pvcloud.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "cust_info", pk = "cust_id")
public class CustInfo extends Model<CustInfo> {

	public static final CustInfo dao = new CustInfo();
	 
	public Page<CustInfo> queryByPage(int page,int size,String custInfo,String custValue){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select m.*";
		if(("phoneNum").equals(custInfo) && custValue!=null && !("").equals(custValue)){
			strBuf.append(" and m.phonenum like ?");
			param.add("%"+custValue+"%");
		}else if(("addrName").equals(custInfo) && custValue!=null && !("").equals(custValue)){
			strBuf.append(" and m.addrname like ?");
			param.add("%"+custValue+"%");
		}
		sql=" from cust_info m where 1=1 "+strBuf.toString()+" order by m.register_date,cust_id desc";
		return CustInfo.dao.paginate(page, size, select, sql,param.toArray());
		
	}
	
	public CustInfo queryById(int custId){
		return CustInfo.dao.findFirst("select * from cust_info where cust_id = ?",custId);
	}
	
	public boolean updateInfo(CustInfo custInfo){
		return new CustInfo().setAttrs(custInfo).update();
	}
	
	public CustInfo login(String phoneNum,String password){
		return CustInfo.dao.findFirst("select * from cust_info where phonenum = ? and password = ?",phoneNum,password);
	}
	
	public boolean saveInfo(CustInfo custInfo){
		return new CustInfo().setAttrs(custInfo).save();
	}
	
	public CustInfo queryByPhoneNum(String phoneNum){
		return CustInfo.dao.findFirst("select * from cust_info where phonenum = ?",phoneNum);
	}
	
	public boolean del(int custId){
		return CustInfo.dao.deleteById(custId);
	}
}