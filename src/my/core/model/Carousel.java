package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_carousel", pk = "id")
public class Carousel extends Model<Carousel> {
	
	public static final Carousel dao = new Carousel();

	public List<Carousel> queryCarouselList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return Carousel.dao.find("select * from t_carousel order by update_time desc limit "+fromRow+","+pageSize);
	}
}
