package my.core.model;

import java.util.List;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_buycart", pk = "id")
public class BuyCart extends Model<BuyCart> {
	
	public static final BuyCart dao = new BuyCart();

	public Page<BuyCart> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_buycart where 1=1 order by create_time desc";
		String select="select * ";
		return BuyCart.dao.paginate(page, size, select, sql);
	}
	
	public BuyCart queryById(int id){
		return BuyCart.dao.findFirst("select * from t_buycart where id = ?",id);
	}
	
	public List<BuyCart> queryBuyCart(int pageSize,int pageNum,int memberId){
		int fromRow = pageSize*(pageNum-1);
		return BuyCart.dao.find("select * from t_buycart where member_id="+memberId+" and status = '140002' order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public Long queryBuycartCount(int memberId){
		return Db.queryLong("select count(*) from t_buycart where member_id="+memberId+" and status='140002'");
	}
	
	public Long queryBuycartExist(int memberId,int wtmItemId){
		return Db.queryLong("select count(*) from t_buycart where member_id="+memberId+" and warehouse_tea_member_item_id="+wtmItemId+" and status='140002'");
	}
	
	public BuyCart queryBuycart(int memberId,int wtmItemId){
		return BuyCart.dao.findFirst("select * from t_buycart where member_id="+memberId+" and warehouse_tea_member_item_id="+wtmItemId+" and status='140002'");
	}
	
	public boolean updateInfo(BuyCart data){
		return new BuyCart().setAttrs(data).update();
	}
	
	public boolean saveInfo(BuyCart data){
		return new BuyCart().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return BuyCart.dao.deleteById(id);
	}
	
	public int updateStatus(String ids,String status){
		return Db.update("update t_buycart set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id in("+ids+")");
	}
	
	public int updateStock(int memberId,int wtmItemId,int quality){
		return Db.update("update t_buycart set quality=quality+"+quality+",update_time='"+DateUtil.getNowTimestamp()+"' where member_id="+memberId+" and warehouse_tea_member_item_id="+wtmItemId+" and status='140002'");
	}
}
