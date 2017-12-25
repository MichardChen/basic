package my.core.model;

import java.math.BigDecimal;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_invoice", pk = "id")
public class Invoice extends Model<Invoice> {
	
	public static final Invoice dao = new Invoice();

	public Page<Invoice> queryByPage(int page,int size){
		String sql=" from t_invoice where 1=1 order by create_time desc";
		String select="select * ";
		return Invoice.dao.paginate(page, size, select, sql);
	}
	
	public Invoice queryInvoiceById(int id){
		return Invoice.dao.findFirst("select * from t_invoice where id=?",id);
	}
	
	public Page<Invoice> queryByPageParams(int page,int size,String mobile,String date,String status){
		
		String where = "";
		if(StringUtil.isNoneBlank(date)){
			where = " and a.create_time like '%"+date+"%'";
		}
		if(StringUtil.isNoneBlank(mobile)){
			where = " and b.mobile like '%"+mobile+"%'";
		}
		if(StringUtil.isNoneBlank(status)){
			where = " and a.status='"+status+"'";
		}
		String sql=" from t_invoice a inner join t_member b on a.user_id=b.id where 1=1 "+where+" order by a.create_time desc";
		String select="select a.* ";
		return Invoice.dao.paginate(page, size, select, sql);
	}
	
	public List<Invoice> queryByPageParams(String mobile,String date,String status){
		
		String where = "";
		if(StringUtil.isNoneBlank(date)){
			where = " and a.create_time like '%"+date+"%'";
		}
		if(StringUtil.isNoneBlank(mobile)){
			where = " and b.mobile like '%"+mobile+"%'";
		}
		if(StringUtil.isNoneBlank(status)){
			where = " and a.status='"+status+"'";
		}
		String sql=" from t_invoice a inner join t_member b on a.user_id=b.id where 1=1 "+where+" order by a.create_time desc";
		String select="select a.* ";
		return Invoice.dao.find(select+sql);
	}
	
	public boolean updateInfo(Invoice data){
		return new Invoice().setAttrs(data).update();
	}
	
	public Invoice saveInfo(Invoice tea){
		Invoice store = new Invoice().setAttrs(tea);
		store.save();
		return store;
	}
	
	public int saveInfos(Invoice data){
		Invoice store = new Invoice().setAttrs(data);
		store.save();
		return store.getInt("id");
	}
	
	/*public List<Invoice> queryStoreEvaluateList(int pageSize,int pageNum,int storeId){
		int fromRow = (pageNum-1)*pageSize;
		return Invoice.dao.find("select * from t_store_evaluate where flg=1 and store_id="+storeId+" order by create_time desc limit "+fromRow+","+pageSize);
	}
	
	public int sumStoreEvaluateNum(int userId,int storeId,String date1,String date2){
		Long sum = Db.queryLong("select count(1) from t_store_evaluate where member_id="+userId+" and store_id="+storeId+" and create_time>='"+date1+"' and create_time<='"+date2+"'");
		if(sum == null){
			return 0;
		}
		return sum.intValue();
	}*/
	
	public int updateInvoice(int id
							,String invoiceStatus
							,String expressName
							,String expressNo
							,int updateBy
							,String typeCd
							,String titleTypeCd
							,String title
							,String invoiceNo
							,String taxNo
							,String content
							,BigDecimal moneys
							,String mark
							,String bank
							,String account
							,String mail){
		return Db.update("update t_invoice set status='"+invoiceStatus+"',update_time='"+DateUtil.getNowTimestamp()+"',express_company='"+expressName+"',express_no='"+expressNo+"',update_by="+updateBy+",invoice_type_cd='"+typeCd+"',title_type_cd='"+titleTypeCd+"',title='"+title+"',invoice_no='"+invoiceNo+"',tax_no='"+taxNo+"',content='"+content+"',moneys='"+moneys+"',mark='"+mark+"',bank='"+bank+"',account='"+account+"',mail='"+mail+"' where id="+id);
	}
}
