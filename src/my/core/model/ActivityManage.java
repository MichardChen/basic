package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_activity_manage", pk = "id")
public class ActivityManage extends Model<ActivityManage> {
		
		public static final ActivityManage dao = new ActivityManage();
		
		public List<ActivityManage> queryActivityManages(int pageNum,int pageSize){
				int fromRow = pageSize*(pageNum-1);
				return ActivityManage.dao.find("select * from mk_activity_manage order by create_time desc limit "+fromRow+","+pageSize);
		}
}
