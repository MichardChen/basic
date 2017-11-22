package my.core.model;

import java.math.BigDecimal;
import java.util.List;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_warehouse_tea_member", pk = "id")
public class WarehouseTeaMember extends Model<WarehouseTeaMember> {
	
	public static final WarehouseTeaMember dao = new WarehouseTeaMember();

	
	public Page<WarehouseTeaMember> queryByPage(int page,int size){
			
		String sql=" from t_warehouse_tea_member where 1=1 order by create_time desc";
		String select="select * ";
		return WarehouseTeaMember.dao.paginate(page, size, select, sql);
	}
	
	public List<WarehouseTeaMember> queryAllHouse(){
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member order by update_time desc");
	}
	
	public List<WarehouseTeaMember> queryWareHouseList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public WarehouseTeaMember queryById(int id){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where id = ?",id);
	}
	
	public WarehouseTeaMember queryByUserInfo(int teaId,int memberId,int houseId,String userTypeCd){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where tea_id = ? and member_id=? and warehouse_id=? and member_type_cd=?",teaId,memberId,houseId,userTypeCd);
	}
	
	public WarehouseTeaMember queryPlatTeaInfo(int teaId,int memberId,String userTypeCd){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where tea_id = ? and member_id=? and member_type_cd=?",teaId,memberId,userTypeCd);
	}
	
	
	public WarehouseTeaMember queryWarehouseTeaMember(int id,String memberTypeCd){
		return WarehouseTeaMember.dao.findFirst("select * from t_warehouse_tea_member where tea_id = ? and member_type_cd=?",id,memberTypeCd);
	}
	
	public boolean updateInfo(WarehouseTeaMember data){
		return new WarehouseTeaMember().setAttrs(data).update();
	}
	
	public boolean saveInfo(WarehouseTeaMember data){
		return new WarehouseTeaMember().setAttrs(data).save();
	}
	
	public int saveWarehouseTeaMember(WarehouseTeaMember data){
		WarehouseTeaMember wtm = new WarehouseTeaMember().setAttrs(data);
		wtm.save();
		return wtm.getInt("id");
	}
	
	public boolean del(int id){
		return WarehouseTeaMember.dao.deleteById(id);
	}

	/*public List<WarehouseTeaMember> queryTeaByIdList(int teaId,String size,String priceFlg,int wareHouseId,int quality,int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		String orderby = " order by create_time desc";
		String sql = "";
		if(StringUtil.equals(priceFlg, "0")){
			//从低到高
			if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
				orderby = orderby +",piece_price asc";
			}else{
				orderby = orderby +",item_price asc";
			}
			sql = " and piece_status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}else{
			//从高到低
			if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
				orderby = orderby +",item_price desc";
			}else{
				orderby = orderby +",piece_price desc";
			}
			sql = " and item_status='"+Constants.TEA_STATUS.ON_SALE+"'";
		}
		
		if(quality == 0){
			//从低到高
			orderby = orderby +",stock asc";
		}else{
			//从高到低
			orderby = orderby +",stock desc";
		}
		
		if(wareHouseId != 0){
			sql = sql + " and warehouse_id="+wareHouseId;
		}
		
		sql = sql + " and tea_id="+teaId;
		
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member where 1=1 "+sql+orderby+" limit "+fromRow+","+pageSize);
	}*/

	public List<WarehouseTeaMember> queryPersonProperty(int memberId,int pageSize,int pageNum,String userTypeCd){
		int fromRow = pageSize*(pageNum-1);
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member where member_id="+memberId+" and member_type_cd='"+userTypeCd+"' and stock!=0 and stock is not null order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public List<Integer> queryPersonTeaId(int memberId,int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return Db.query("SELECT tea_id from t_warehouse_tea_member  where member_id="+memberId+" and member_type_cd='010001'  GROUP BY tea_id order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public BigDecimal queryTeaStock(int memberId,int teaId,String memberTypeCd){
		BigDecimal sum = Db.queryBigDecimal("select SUM(stock) from t_warehouse_tea_member where member_id="+memberId+" and member_type_cd='"+memberTypeCd+"' and tea_id="+teaId);
		if(sum == null){
			return new BigDecimal("0");
		}else{
			return sum;
		}
	}
	
	public List<WarehouseTeaMember> queryPersonWarehouseTea(int memberId,String memberTypeCd,int teaId){
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member where member_id="+memberId+" and tea_id="+teaId+" and member_type_cd='"+memberTypeCd+"' order by create_time desc");
	}
	
	public List<WarehouseTeaMember> querysaleTeaWarehouseTea(int memberId,int teaId,String memberTypeCd){
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member where member_id="+memberId+" and tea_id="+teaId+" and member_type_cd='"+memberTypeCd+"' order by create_time desc");
	}
	
	public boolean updateStock(int id,int stock){
		int ret = Db.update("update t_warehouse_tea_member set stock=stock+"+stock+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		if(ret != 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean cutStock(int id,int stock){
		int ret = Db.update("update t_warehouse_tea_member set stock=stock-"+stock+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		if(ret != 0){
			return true;
		}else{
			return false;
		}
	}
	
	public int addTeaQuality(int quality,int warehouseId,int teaId,int memberId){
		return Db.update("update t_warehouse_tea_member set origin_stock=origin_stock+"+quality+",stock=stock+"+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where warehouse_id="+warehouseId+" and tea_id="+teaId+" and member_id="+memberId);
	}
	
	public int cutTeaQuality(int quality,int warehouseId,int teaId,int memberId){
		return Db.update("update t_warehouse_tea_member set stock=stock-"+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where warehouse_id="+warehouseId+" and tea_id="+teaId+" and member_id="+memberId);
	}
	
	public List<WarehouseTeaMember> queryWantSaleTeaList(String memberTypeCd,int userId,int pageSize,int pageNum){
		int fromRow = (pageNum-1)*pageSize;
		return WarehouseTeaMember.dao.find("select * from t_warehouse_tea_member where member_id = ? and member_type_cd=? order by create_time desc limit ?,?",userId,memberTypeCd,fromRow,pageSize);
	}
	
	public BigDecimal queryWarehouseTeaMemberListCount(int wareHouseId){
		return Db.queryBigDecimal("select sum(stock) from t_warehouse_tea_member where warehouse_id="+wareHouseId);
	}
	
	public Page<WarehouseTeaMember> queryByPageParams(int page
											,int size
											,String date
											,String mobile
											,String saleUserTypeCd
											,String tea){
	
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isBlank(tea)){
			
			if(StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
				//用户
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				
				if(StringUtil.isNoneBlank(mobile)){
					strBuf.append(" and b.mobile like '%"+mobile+"%'");
				}
				strBuf.append(" and a.member_type_cd like '%"+Constants.USER_TYPE.USER_TYPE_CLIENT+"%'");
				String sql=" from t_warehouse_tea_member a inner join t_member b on a.member_id=b.id where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}else if(StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.PLATFORM_USER)){
				//平台
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				
				if(StringUtil.isNoneBlank(mobile)){
					strBuf.append(" and b.mobile like '%"+mobile+"%'");
				}
				strBuf.append(" and a.member_type_cd like '%"+Constants.USER_TYPE.PLATFORM_USER+"%'");
				String sql=" from t_warehouse_tea_member a inner join s_user b on a.member_id=b.user_id where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}else{
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				
				String sql=" from t_warehouse_tea_member a where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}
		}else{
			
			//茶叶不为空
			if(StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
				//用户
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				
				if(StringUtil.isNoneBlank(mobile)){
					strBuf.append(" and b.mobile like '%"+mobile+"%'");
				}
				strBuf.append(" and c.tea_title like '%"+tea+"%'");
				strBuf.append(" and a.member_type_cd like '%"+Constants.USER_TYPE.USER_TYPE_CLIENT+"%'");
				String sql=" from t_warehouse_tea_member a inner join t_member b on a.member_id=b.id inner join t_tea c on a.tea_id=c.id where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}else if(StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.PLATFORM_USER)){
				//平台
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				
				if(StringUtil.isNoneBlank(mobile)){
					strBuf.append(" and b.mobile like '%"+mobile+"%'");
				}
				strBuf.append(" and c.tea_title like '%"+tea+"%'");
				strBuf.append(" and a.member_type_cd like '%"+Constants.USER_TYPE.PLATFORM_USER+"%'");
				String sql=" from t_warehouse_tea_member a inner join s_user b on a.member_id=b.user_id inner join t_tea c on a.tea_id=c.id where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}else{
				if(StringUtil.isNoneBlank(date)){
					strBuf.append(" and a.create_time like '%"+date+"%'");
				}
				strBuf.append(" and c.tea_title like '%"+tea+"%'");
				String sql=" from t_warehouse_tea_member a inner join t_tea c on a.tea_id=c.id where 1=1 "+strBuf+" order by a.create_time desc";
				String select="select a.* ";
				return WarehouseTeaMember.dao.paginate(page, size, select, sql);
			}
		}
	}
}
