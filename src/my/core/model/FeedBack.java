package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_feedback", pk = "id")
public class FeedBack extends Model<FeedBack> {
	
	public static final FeedBack dao = new FeedBack();

}
