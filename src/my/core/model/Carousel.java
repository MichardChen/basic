package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_carousel", pk = "id")
public class Carousel extends Model<Carousel> {
	
	public static final Carousel dao = new Carousel();

	
	public Page<Carousel> queryByPage(int page,int size){
			
		String sql=" from t_carousel where 1=1 order by create_time desc";
		String select="select * ";
		return Carousel.dao.paginate(page, size, select, sql);
	}
	
	public List<Carousel> queryCarouselList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return Carousel.dao.find("select * from t_carousel order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public Carousel queryById(int id){
		return Carousel.dao.findFirst("select * from t_carousel where id = ?",id);
	}
	
	public boolean updateInfo(Carousel data){
		return new Carousel().setAttrs(data).update();
	}
	
	public boolean saveInfo(Carousel data){
		return new Carousel().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return Carousel.dao.deleteById(id);
	}
	
	public int updateCarouselStatus(int id,int flg){
		Db.update("update t_carousel set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		Carousel carousel = Carousel.dao.findFirst("select * from t_carousel where id = ?",id);
		if(carousel != null){
			return carousel.getInt("flg");
		}else{
			return 0;
		}
	}
}
