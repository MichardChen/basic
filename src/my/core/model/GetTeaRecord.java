package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_gettea_record", pk = "id")
public class GetTeaRecord extends Model<GetTeaRecord> {
	
	public static final GetTeaRecord dao = new GetTeaRecord();

	public Page<GetTeaRecord> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_gettea_record where 1=1 order by create_time desc";
		String select="select * ";
		return GetTeaRecord.dao.paginate(page, size, select, sql);
	}
	
	public Page<GetTeaRecord> queryByPageParams(int page,int size,String time1,String time2){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isNoneBlank(time1)){
			strBuf.append(" and create_time>='"+time1+" 00:00:00'");
		}
		if(StringUtil.isNoneBlank(time2)){
			strBuf.append(" and create_time<='"+time2+" 23:59:59'");
		}
			
		String sql=" from t_gettea_record where 1=1 "+strBuf+" order by create_time desc";
		String select="select * ";
		return GetTeaRecord.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public GetTeaRecord queryById(int id){
		return GetTeaRecord.dao.findFirst("select * from t_gettea_record where id = ? order by create_time desc",id);
	}
	
	public List<GetTeaRecord> queryRecords(int pageSize,int pageNum,int memberId,String date){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(date)){
			return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" and create_time like '%"+date+"%' order by create_time desc limit "+fromRow+","+pageSize);
		}else{
			return GetTeaRecord.dao.find("select * from t_gettea_record where member_id ="+memberId+" order by create_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public boolean updateInfo(GetTeaRecord tea){
		return new GetTeaRecord().setAttrs(tea).update();
	}
	
	public boolean saveInfo(GetTeaRecord tea){
		return new GetTeaRecord().setAttrs(tea).save();
	}
	
	public boolean del(int id){
		return GetTeaRecord.dao.deleteById(id);
	}
	
}
