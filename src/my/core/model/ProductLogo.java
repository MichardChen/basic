package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_product_logo", pk = "id")
public class ProductLogo extends Model<ProductLogo> {
	
		public static final ProductLogo dao = new ProductLogo();
		
		public List<ProductLogo> queryProductLogoLists(int productId){
			return ProductLogo.dao.find("select * from mk_product_logo where product_id=? order by seq asc",productId);
		}
}
