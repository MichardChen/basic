package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_news_detail", pk = "id")
public class NewsDetail extends Model<NewsDetail> {
	
	public static final NewsDetail dao = new NewsDetail();

}
