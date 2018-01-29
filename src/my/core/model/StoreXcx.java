package my.core.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_store_xcx", pk = "id")
public class StoreXcx extends Model<StoreXcx>{
	
	public static final StoreXcx dao = new StoreXcx();

	public StoreXcx queryById(int id){
		return StoreXcx.dao.findFirst("select * from t_store_xcx where id = ?",id);
	}
	
	public StoreXcx queryByStoreId(int storeId){
		return StoreXcx.dao.findFirst("select * from t_store_xcx where store_id = ?",storeId);
	}
	
	public StoreXcx queryByAppId(String appid){
		return StoreXcx.dao.findFirst("select * from t_store_xcx where appid='"+appid+"'");
	}
	
	public boolean updateInfo(StoreXcx data){
		return new StoreXcx().setAttrs(data).update();
	}
	
	public boolean saveInfo(StoreXcx data){
		return new StoreXcx().setAttrs(data).save();
	}
	
	public Page<StoreXcx> queryListByPage(int page,int size,String appid){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(appid)){
			strBuf.append("and appid like '%"+appid+"%'");
		}
		
		sql=" from t_store_xcx where 1=1 "+strBuf.toString()+" order by create_time desc";
		return StoreXcx.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<StoreXcx> queryByPage(int page,int size){
		String sql=" from t_store_xcx where 1=1 order by create_time desc";
		String select="select * ";
		return StoreXcx.dao.paginate(page, size, select, sql);
	}
	
	public int updateStoreXcx(String appid,String authCode,Timestamp expireTime,String accessToken,String refreshToken,String nickName){
		return Db.update("update t_store_xcx set appname='"+nickName+"',auth_code='"+authCode+"',update_time='"+DateUtil.getNowTimestamp()+"',expire_time='"+expireTime+"',authorizer_access_token='"+accessToken+"',authorizer_refresh_token='"+refreshToken+"' where appid='"+appid+"'");
	}
	
	public int updateStoreXcxRefresh(String appid,Timestamp expireTime,String accessToken,String refreshToken){
		return Db.update("update t_store_xcx set update_time='"+DateUtil.getNowTimestamp()+"',expire_time='"+expireTime+"',authorizer_access_token='"+accessToken+"',authorizer_refresh_token='"+refreshToken+"' where appid='"+appid+"'");
	}
}
