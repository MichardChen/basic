package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_store_image", pk = "id")
public class StoreImage extends Model<StoreImage> {
	
	public static final StoreImage dao = new StoreImage();
	
	public boolean updateInfo(StoreImage tea){
		return new StoreImage().setAttrs(tea).update();
	}
	
	public boolean saveInfo(StoreImage tea){
		return new StoreImage().setAttrs(tea).save();
	}

	public List<StoreImage> queryStoreImages(int storeId){
		return StoreImage.dao.find("select * from t_store_image where store_id=? and flg=1 order by create_time desc",storeId);
	}
}
