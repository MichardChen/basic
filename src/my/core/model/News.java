package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_news", pk = "id")
public class News extends Model<News> {
	
	public static final News dao = new News();

}
