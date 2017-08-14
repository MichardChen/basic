package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "mk_points_record", pk = "id")
public class PointsRecord extends Model<PointsRecord> {
		
	public static final PointsRecord dao = new PointsRecord();
	
	public int saveRecord(String userTypeCd,int userId,String operateTypeCd,int points,String mark){
		PointsRecord record = new PointsRecord().set("user_type_cd", userTypeCd).set("user_id", userId).set("operate_type_cd", operateTypeCd).set("point", points).set("mark", mark).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp());
		boolean isSave = record.save();
		return record.getInt("id");
	}
	
	public Long queryExistRecord(String userTypeCd,int userId,String operateType,String date){
		if(StringUtil.isNoneBlank(date)){
			return Db.queryLong("select count(*) from mk_points_record where user_type_cd='"+userTypeCd+"' and user_id="+userId+" and operate_type_cd='"+operateType+"' and create_time like '%"+date+"%'");
		}else{
			return Db.queryLong("select count(*) from mk_points_record where user_type_cd='"+userTypeCd+"' and user_id="+userId+" and operate_type_cd='"+operateType+"' ");
		}
	}
}
