package my.core.model;

import java.sql.Timestamp;
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

	public Page<News> queryNewsListByPage(int page,int size,String title,String type,String hot,String fromDate,String toDate){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(title)){
			strBuf.append("and news_title like '%"+title+"%'");
		}
		if(StringUtil.isNoneBlank(type)){
			strBuf.append("and news_type_cd='"+type+"'");
		}
		if(StringUtil.isNoneBlank(hot)){
			int hots = StringUtil.toInteger(hot);
			strBuf.append("and hot_flg="+hots);
		}
		if(StringUtil.isNoneBlank(fromDate)){
			strBuf.append("and create_time>='"+fromDate+" 00:00:00'");
		}
		if(StringUtil.isNoneBlank(toDate)){
			strBuf.append("and create_time<='"+toDate+" 23:59:59'");
		}
		
		sql=" from t_news where 1=1 "+strBuf.toString()+" order by update_time desc,top_flg desc,flg desc,create_time desc";
		return News.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<News> queryByPage(int page,int size){
		String sql=" from t_news where flg=1 order by top_flg desc,hot_flg desc,flg desc,create_time desc,update_time desc";
		String select="select * ";
		return News.dao.paginate(page, size, select, sql);
	}
	
	public Page<News> queryByAdminPage(int page,int size){
		String sql=" from t_news order by update_time desc,top_flg desc,flg desc,create_time desc,update_time desc";
		String select="select * ";
		return News.dao.paginate(page, size, select, sql);
	}
	
	public Page<News> queryNews(int page,int size){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add("1");
		String sql=" from t_news where 1=1"+strBuf+" order by update_time desc,top_flg desc,flg desc,hot_flg desc,create_time desc";
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
	
	public int updateNewsStatus(int id,int flg,int userId){
		Db.update("update t_news set update_user_id="+userId+",flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		News news =News.dao.findFirst("select * from t_news where id = ?",id);
		if(news != null){
			return news.getInt("flg");
		}else{
			return 0;
		}
	}
	
	public int saveTop(int id,int top){
		return Db.update("update t_news set top_flg="+top+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public int saveNews(String newsLogo,String newsTitle,String newsTypeCd,int hotFlg,int createUser,int flg,String content,String url,int updateUserId){
		News news = new News().set("news_logo", newsLogo).set("news_title", newsTitle).set("content_url", url).set("news_type_cd", newsTypeCd).set("hot_flg",hotFlg).set("create_user",createUser).set("flg",flg).set("content",content).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("update_user_id", updateUserId);
		boolean isSave = news.save();
		return news.getInt("id");
	}
	
	public News queryMaxNews(){
		return News.dao.findFirst("select * from t_news order by top_flg desc,create_time desc");
	}
}
