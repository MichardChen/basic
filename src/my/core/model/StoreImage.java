package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_store_image", pk = "id")
public class StoreImage extends Model<StoreImage> {
	
	public static final StoreImage dao = new StoreImage();
	
	public boolean updateInfo(String img,int storeId,int seq){
		int ret = Db.update("update t_store_image set img='"+img+"',update_time='"+DateUtil.getNowTimestamp()+"' where store_id="+storeId+" and seq="+seq);
		if(ret != 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean saveInfo(StoreImage tea){
		return new StoreImage().setAttrs(tea).save();
	}

	public List<StoreImage> queryStoreImages(int storeId){
		return StoreImage.dao.find("select * from t_store_image where store_id=? and flg=1 order by seq asc,create_time asc",storeId);
	}
	
	public StoreImage queryStoreFirstImages(int storeId){
		return StoreImage.dao.findFirst("select * from t_store_image where store_id=? and flg=1 order by create_time asc",storeId);
	}
}
