package my.core.model;

import java.math.BigDecimal;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_warehouse_tea_member_item", pk = "id")
public class WarehouseTeaMemberItem extends Model<WarehouseTeaMemberItem> {
	
	public static final WarehouseTeaMemberItem dao = new WarehouseTeaMemberItem();

	
	public WarehouseTeaMemberItem queryById(int id){
		return WarehouseTeaMemberItem.dao.findFirst("select * from t_warehouse_tea_member_item where warehouse_tea_member_id = ?",id);
	}
	
	public WarehouseTeaMemberItem queryByKeyId(int id){
		return WarehouseTeaMemberItem.dao.findFirst("select * from t_warehouse_tea_member_item where id = ?",id);
	}
	
	public List<WarehouseTeaMemberItem> queryTeaByIdList(int teaId
														,String size
														,String priceFlg
														,int wareHouseId
														,int quality
														,int pageSize
														,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		String orderby = " order by create_time desc";
		String sql = "";
		if(StringUtil.equals(priceFlg, "0")){
			//从低到高
			sql = sql +" and size_type_cd ='150001'";
			orderby = orderby +",price asc";
			sql = " and status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}else{
			//从高到低
			sql = sql +" and size_type_cd ='150002'";
			orderby = orderby +",price desc";
			sql = " and status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}
		
		if(quality == 0){
			//从低到高
			orderby = orderby +",quality asc";
		}else{
			//从高到低
			orderby = orderby +",quality desc";
		}
		
		if(wareHouseId != 0){
			sql = sql + " and b.warehouse_id="+wareHouseId;
		}
		
		sql = sql + " and b.tea_id="+teaId;
		
		return WarehouseTeaMemberItem.dao.find("select a.* from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where 1=1 "+sql+orderby+" limit "+fromRow+","+pageSize);
	}
	
	public BigDecimal queryOnSaleTeaCount(int memberId,int houserId,int teaId,String typeCd){
		return Db.queryBigDecimal("select sum(b.quality) from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b where a.member_id="+memberId+" and a.warehouse_id="+houserId+" and tea_id="+teaId+" and member_type_cd='010002' and b.status='160001' and size_type_cd='"+typeCd+"'");
	}
	
	public WarehouseTeaMemberItem queryTeaOnPlatform(String memberTypeCd,int teaId){
		return WarehouseTeaMemberItem.dao.findFirst("select b.* from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b on a.id=b.warehouse_tea_member_id where a.tea_id = ? and a.member_type_cd=?",teaId,memberTypeCd);
	}

	public List<WarehouseTeaMemberItem> queryWantSaleTeaList(String memberTypeCd,int userId,int pageSize,int pageNum){
		int fromRow = (pageNum-1)*pageSize;
		return WarehouseTeaMemberItem.dao.find("select b.* from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b on a.id=b.warehouse_tea_member_id where a.member_id = ? and a.member_type_cd=? order by b.create_time desc limit ?,? ",userId,memberTypeCd,fromRow,pageSize);
	}
	
	public int cutTeaQuality(int quality,int id){
		return Db.update("update t_warehouse_tea_member_item set quality=quality-"+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where warehouse_tea_member_id="+id);
	}
	
	public Page<WarehouseTeaMemberItem> queryByPage(int page,int size){
		String sql=" from t_warehouse_tea_member_item where 1=1 order by create_time desc";
		String select="select * ";
		return WarehouseTeaMemberItem.dao.paginate(page, size, select, sql);
	}
	
	public Page<WarehouseTeaMemberItem> queryByPageParams(int page,int size,String date){
		
		StringBuffer strBuf=new StringBuffer();
		
		if(StringUtil.isNoneBlank(date)){
			strBuf.append(" and create_time like '%"+date+"%'");
		}
			
		String sql=" from t_warehouse_tea_member_item where 1=1 "+strBuf+" order by create_time desc";
		String select="select * ";
		return WarehouseTeaMemberItem.dao.paginate(page, size, select, sql);
	}
		
	public boolean updateInfo(WarehouseTeaMemberItem data){
		return new WarehouseTeaMemberItem().setAttrs(data).update();
	}
	
	public boolean saveInfo(WarehouseTeaMemberItem data){
		return new WarehouseTeaMemberItem().setAttrs(data).save();
	}
	
	public int updateTeaInfo(int wtmId,BigDecimal price,String status,int quality){
		return Db.update("update t_warehouse_tea_member_item set price="+price+",status='"+status+"',quality="+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where warehouse_tea_member_id="+wtmId);
	}
}
