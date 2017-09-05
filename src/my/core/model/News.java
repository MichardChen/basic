package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.model.Member;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_news", pk = "id")
public class News extends Model<News> {
	
	public static final News dao = new News();

	public Page<News> queryNewsListByPage(int page,int size,String title){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(title)){
			strBuf.append("and news_title=?");
			param.add(title);
		}
		
		sql=" from t_news where 1=1 "+strBuf.toString()+" order by create_time desc";
		return News.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<News> queryByPage(int page,int size){
		String sql=" from t_news order by create_time desc";
		String select="select * ";
		return News.dao.paginate(page, size, select, sql);
	}
	
	public Page<News> queryNews(int page,int size){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add("1");
		String sql=" from t_news where 1=1"+strBuf+" order by create_time desc,hot_flg desc";
		String select="select * ";
		return News.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public News queryById(int id){
		return News.dao.findFirst("select * from t_news where id = ?",id);
	}
	
	public boolean updateInfo(News news){
		return new News().setAttrs(news).update();
	}
	
	public boolean saveInfo(News news){
		return new News().setAttrs(news).save();
	}
	
	public boolean del(int id){
		return News.dao.deleteById(id);
	}
	
	public int updateNewsStatus(int id,int flg){
		Db.update("update t_news set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		News news =News.dao.findFirst("select * from t_news where id = ?",id);
		if(news != null){
			return news.getInt("flg");
		}else{
			return 0;
		}
	}
	
	public int saveNews(String newsLogo,String newsTitle,String newsTypeCd,int hotFlg,int createUser,int flg,String content,String url){
		News news = new News().set("news_logo", newsLogo).set("news_title", newsTitle).set("content_url", url).set("news_type_cd", newsTypeCd).set("hot_flg",hotFlg).set("create_user",createUser).set("flg",flg).set("content",content).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp());
		boolean isSave = news.save();
		return news.getInt("id");
	}
}
