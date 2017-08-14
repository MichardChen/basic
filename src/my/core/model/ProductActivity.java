package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_product_activity", pk = "id")
public class ProductActivity extends Model<ProductActivity> {
		
		public static final ProductActivity dao = new ProductActivity();
		
		public ProductActivity queryProductActivity(int pid){
			return ProductActivity.dao.findFirst("select * from mk_product_activity where product_id=?",pid);
		}
}
