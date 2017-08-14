package my.core.model;


import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "mk_product_zan", pk = "id")
public class ProductsZan extends Model<ProductsZan>{

	public static final ProductsZan dao = new ProductsZan();
	
	public boolean addZan(int userId,int productId){
		return new ProductsZan().set("user_id", userId).set("product_id", productId).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).save();
	}
	
	public ProductsZan queryIsZaned(int userId,int productId){
		return ProductsZan.dao.findFirst("select * from mk_product_zan where user_Id=? and product_id=?",userId,productId);
	}
}
