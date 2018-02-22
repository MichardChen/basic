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
														,int pageNum
														,int memberId){
		
		int fromRow = pageSize*(pageNum-1);
		//String orderby = " order by a.create_time asc";
		String orderby = " order by ";
		String sql = " and a.size_type_cd ='"+size+"'";
		if(StringUtil.equals(priceFlg, "0")){
			//从低到高
			//sql = sql +" and a.size_type_cd ='150001'";
			orderby = orderby +" a.price asc";
			sql = sql+" and a.status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}else{
			//从高到低
			//sql = sql +" and a.size_type_cd ='150002'";
			orderby = orderby +" a.price desc";
			sql = sql+" and a.status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}
		
		if(quality == 0){
			//从低到高
			orderby = orderby +",a.quality asc";
		}else{
			//从高到低
			orderby = orderby +",a.quality desc";
		}
		
		if(wareHouseId != 0){
			sql = sql + " and b.warehouse_id="+wareHouseId;
		}
		
		
		sql = sql + " and b.tea_id="+teaId+" and a.quality is not null and a.quality!=0";
		
		return WarehouseTeaMemberItem.dao.find("select a.* from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where 1=1 and b.member_id != "+memberId+" "+sql+orderby+" limit "+fromRow+","+pageSize);
	}
	
	public BigDecimal queryOnSaleTeaCount(int memberId,int houserId,int teaId,String typeCd){
		return Db.queryBigDecimal("select sum(b.quality) from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b on b.warehouse_tea_member_id=a.id where a.member_id="+memberId+" and a.warehouse_id="+houserId+" and a.tea_id="+teaId+" and member_type_cd='010001' and b.status='160001' and size_type_cd='"+typeCd+"'");
	}
	
	public WarehouseTeaMemberItem queryTeaOnPlatform(String memberTypeCd,int teaId){
		return WarehouseTeaMemberItem.dao.findFirst("select b.* from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b on a.id=b.warehouse_tea_member_id where a.tea_id = ? and a.member_type_cd=?",teaId,memberTypeCd);
	}

	public List<WarehouseTeaMemberItem> queryWantSaleTeaList(String memberTypeCd,int userId,int pageSize,int pageNum){
		int fromRow = (pageNum-1)*pageSize;
		return WarehouseTeaMemberItem.dao.find("select b.* from t_warehouse_tea_member a inner join t_warehouse_tea_member_item b on a.id=b.warehouse_tea_member_id where a.member_id = ? and a.member_type_cd=? order by b.create_time desc limit ?,? ",userId,memberTypeCd,fromRow,pageSize);
	}
	
	public int cutTeaQuality(int quality,int id){
		return Db.update("update t_warehouse_tea_member_item set quality=quality-"+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public Page<WarehouseTeaMemberItem> queryByPage(int page,int size,String status){
		String andStr = " and status='"+status+"'";
		String sql=" from t_warehouse_tea_member_item where 1=1 "+andStr+" order by create_time desc";
		String select="select * ";
		return WarehouseTeaMemberItem.dao.paginate(page, size, select, sql);
	}
	
	public Page<WarehouseTeaMemberItem> queryByPageParams(int page,int size,String date,int saleUserId,String saleUserTypeCd,String status){
		
		if((StringUtil.isBlank(saleUserTypeCd))&&(saleUserId == 0)){
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(date)){
				strBuf.append(" and create_time like '%"+date+"%'");
			}
			
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and status='"+status+"'");
			}
				
			String sql=" from t_warehouse_tea_member_item where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return WarehouseTeaMemberItem.dao.paginate(page, size, select, sql);
		}else{
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(date)){
				strBuf.append(" and a.create_time like '%"+date+"%'");
			}
			
			if(StringUtil.isNoneBlank(saleUserTypeCd)){
				strBuf.append(" and b.member_type_cd='"+saleUserTypeCd+"'");
			}
			if(saleUserId != 0){
				strBuf.append(" and b.member_id="+saleUserId);
			}
			
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.status='"+status+"'");
			}
				
			String sql=" from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return WarehouseTeaMemberItem.dao.paginate(page, size, select, sql);
		}
	}
	
	public List<WarehouseTeaMemberItem> exportData(String date,int saleUserId,String saleUserTypeCd,String status){
		
		if((StringUtil.isBlank(saleUserTypeCd))&&(saleUserId == 0)){
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(date)){
				strBuf.append(" and create_time like '%"+date+"%'");
			}
			
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and status='"+status+"'");
			}
				
			String sql=" from t_warehouse_tea_member_item where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return WarehouseTeaMemberItem.dao.find(select+sql);
		}else{
			StringBuffer strBuf=new StringBuffer();
			if(StringUtil.isNoneBlank(date)){
				strBuf.append(" and a.create_time like '%"+date+"%'");
			}
			
			if(StringUtil.isNoneBlank(saleUserTypeCd)){
				strBuf.append(" and b.member_type_cd='"+saleUserTypeCd+"'");
			}
			if(saleUserId != 0){
				strBuf.append(" and b.member_id="+saleUserId);
			}
			
			if(StringUtil.isNoneBlank(status)){
				strBuf.append(" and a.status='"+status+"'");
			}
				
			String sql=" from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
			String select="select a.* ";
			return WarehouseTeaMemberItem.dao.find(select+sql);
		}
	}
		
	public boolean updateInfo(WarehouseTeaMemberItem data){
		return new WarehouseTeaMemberItem().setAttrs(data).update();
	}
	
	public boolean saveInfo(WarehouseTeaMemberItem data){
		return new WarehouseTeaMemberItem().setAttrs(data).save();
	}
	
	
	public int saveItemInfo(WarehouseTeaMemberItem data){
		WarehouseTeaMemberItem t = new WarehouseTeaMemberItem().setAttrs(data);
		t.save();
		return t.getInt("id");
	}
	
	public int updateTeaInfo(int wtmId,BigDecimal price,String status,int quality){
		return Db.update("update t_warehouse_tea_member_item set price="+price+",status='"+status+"',quality="+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where warehouse_tea_member_id="+wtmId);
	}
	
	public int updateWtmItemStatus(int wtmId,String status){
		return Db.update("update t_warehouse_tea_member_item set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+wtmId);
	}
	
	public List<WarehouseTeaMemberItem> queryBuyTeaList(int pageSize,int pageNum,String name,int userId){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isNoneBlank(name)){
			return WarehouseTeaMemberItem.dao.find("select * from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id inner join t_tea c on b.tea_id=c.id where c.tea_title like '%"+name+"%' and a.status='160001' order by a.create_time desc limit "+fromRow+","+pageSize);
		}else{
			return WarehouseTeaMemberItem.dao.find("select * from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where b.member_id != "+userId+" and a.status='160001' order by a.create_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	
	public int updateResetOrderStatus(int id,String status,int cancleQuality){
		return Db.update("update t_warehouse_tea_member_item set quality=0,status='"+status+"',cancle_quality="+cancleQuality+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public int updateOnlyStatus(int id,String status){
		return Db.update("update t_warehouse_tea_member_item set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public BigDecimal queryOnSaleListCount(int wtmId,String sizeType){
		return Db.queryBigDecimal("select sum(quality) from t_warehouse_tea_member_item where warehouse_tea_member_id="+wtmId+" and size_type_cd='"+sizeType+"'");
	}
	
	public List<WarehouseTeaMemberItem> queryMemberWtmItems(int memberId,int pageSize,int pageNum,String status,String memberTypeCd){
		int fromRow = (pageNum-1)*pageSize;
		return WarehouseTeaMemberItem.dao.find("select a.* from t_warehouse_tea_member_item a inner join t_warehouse_tea_member b on a.warehouse_tea_member_id=b.id where b.member_id = ? and a.status=? and b.member_type_cd=? order by a.create_time desc limit ?,?",memberId,status,memberTypeCd,fromRow,pageSize);
	}
}
