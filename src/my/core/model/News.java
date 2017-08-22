package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_news", pk = "id")
public class News extends Model<News> {
	
	public static final News dao = new News();

	public Page<News> queryByPage(int page,int size){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add("1");
		String sql=" from t_news where 1=1"+strBuf+" order by create_time desc";
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
}
