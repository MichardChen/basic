package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_product_store", pk = "id")
public class ProductStore extends Model<ProductStore> {
			
	public static final ProductStore dao = new ProductStore();
			
	public ProductStore queryProductStore(int productId){
		return ProductStore.dao.findFirst("select * from mk_product_store where product_id=?",productId);
	}
}
