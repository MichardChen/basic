package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "mk_products", pk = "id")
public class Products extends Model<Products> {
	
	public static final Products dao = new Products();
	
	public List<Products> queryProductLists(String content,int pageSize,int pageNum,String type,int store){
		int fromRow = pageSize*(pageNum-1);
		if (StringUtil.equals(type, Constants.PRODUCT_TYPE.ALL)) {
			if(StringUtil.isNoneBlank(content)){
				return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.effective_mark=1 and a.product_name like '%"+content+"%' order by a.product_salecount desc limit "+fromRow+","+pageSize);
			}else{
				return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.effective_mark=1 order by a.product_salecount desc limit "+fromRow+","+pageSize);		
			}
		}else{
			if(StringUtil.isNoneBlank(content)){
				return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.effective_mark=1 and a.product_type_cd='"+type+"' and a.product_name like '%"+content+"%' order by a.product_salecount desc limit "+fromRow+","+pageSize);
			}else{
				return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.effective_mark=1 and a.product_type_cd='"+type+"' order by a.product_salecount desc limit "+fromRow+","+pageSize);		
			}
		}
	}
	public List<Products> queryProductLists2(String content,int pageSize,int pageNum,String type){
		int fromRow = pageSize*(pageNum-1);
		if (StringUtil.equals(type, Constants.PRODUCT_TYPE.ALL)) {
			if(StringUtil.isNoneBlank(content)){
				return Products.dao.find("select * from mk_products where product_name like '%"+content+"%' and effective_mark=1 order by product_salecount desc limit "+fromRow+","+pageSize);
			}else{
				return Products.dao.find("select * from mk_products where  effective_mark=1 order by product_salecount desc limit "+fromRow+","+pageSize);		
			}
		}else{
			if(StringUtil.isNoneBlank(content)){
				return Products.dao.find("select * from mk_products where product_type_cd='"+type+"' and effective_mark=1 and product_name like '%"+content+"%' order by product_salecount desc limit "+fromRow+","+pageSize);
			}else{
				return Products.dao.find("select * from mk_products where product_type_cd='"+type+"' and effective_mark=1 order by product_salecount desc limit "+fromRow+","+pageSize);		
			}
		}
	}
	//洗剪吹烫染护理
	public List<Products> queryBarberProductLists(String content,int pageSize,int pageNum,String type,int store){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(content)){
			return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.product_type_cd='"+type+"' and a.effective_mark=1 and a.product_name like '%"+content+"%' order by a.product_salecount desc limit "+fromRow+","+pageSize);
		}else{
			return Products.dao.find("select a.* from mk_products a left join mk_product_store b on a.id=b.product_id where b.store_id="+store+" and a.product_type_cd='"+type+"' and a.effective_mark=1 order by a.product_salecount desc limit "+fromRow+","+pageSize);		
		}
	}
	public List<Products> queryBarberProductLists2(String content,int pageSize,int pageNum,String type){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(content)){
			return Products.dao.find("select * from mk_products  where product_type_cd='"+type+"' and product_name like '%"+content+"%' and effective_mark=1  order by product_salecount desc limit "+fromRow+","+pageSize);
		}else{
			return Products.dao.find("select * from mk_products  where product_type_cd='"+type+"' and effective_mark=1 order by product_salecount desc limit "+fromRow+","+pageSize);		
		}
	}
	public void addZan(int productId){
		 Db.update("update mk_products set product_zan=product_zan+1,update_time='"+DateUtil.getNowTimestamp()+"' where id="+productId);
	}
	
	public List<Products> queryProductListByStore(String content,int storeId){
		if(StringUtil.isNoneBlank(content)){
			return Products.dao.find("select mk_products.* from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" and  mk_products.product_name like '%"+content+"%' order by mk_products.product_salecount desc");
		}else{
			return Products.dao.find("select mk_products.* from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" order by mk_products.product_salecount desc");		
		}
	}
	
	public List<Products> queryProductListByStore(String content,int storeId,int pageNum,int pageSize){
		int fromRow = (pageNum-1)*pageSize;
		if(StringUtil.isNoneBlank(content)){
			return Products.dao.find("select mk_products.* from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" and  mk_products.product_name like '%"+content+"%' order by mk_products.product_salecount desc limit "+fromRow+","+pageSize);
		}else{
			return Products.dao.find("select mk_products.* from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" order by mk_products.product_salecount desc limit "+fromRow+","+pageSize);		
		}
	}
	
	public Long queryProductListByStoreCount(String content,int storeId){
		if(StringUtil.isNoneBlank(content)){
			return Db.queryLong(("select count(1) from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" and  mk_products.product_name like '%"+content+"%' order by mk_products.create_time desc"));
		}else{
			return Db.queryLong(("select count(1) from mk_products inner join mk_product_store on mk_products.id=mk_product_store.product_id where mk_product_store.store_id="+storeId+" order by mk_products.create_time desc"));		
		}
	}
	
	public int updateSalesCount(int productId){
		return Db.update("update mk_products set product_salecount=product_salecount+1 where id="+productId);
	}
}