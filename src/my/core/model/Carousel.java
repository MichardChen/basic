package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_carousel", pk = "id")
public class Carousel extends Model<Carousel> {
	
	public static final Carousel dao = new Carousel();

}
