package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_document", pk = "id")
public class Document extends Model<Document> {
	
	public static final Document dao = new Document();

	public Page<Document> queryByPage(int page,int size){
	/*	List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(" and flg=?");
		param.add(flg);*/
		
		String sql=" from t_document where 1=1 order by create_time desc";
		String select="select * ";
		return Document.dao.paginate(page, size, select, sql);
	}
	
	public Document queryById(int id){
		return Document.dao.findFirst("select * from t_document where id = ?",id);
	}
	
	public boolean updateInfo(Document tea){
		return new Document().setAttrs(tea).update();
	}
	
	public boolean saveInfo(Document tea){
		return new Document().setAttrs(tea).save();
	}
	
	public boolean del(int id){
		return Document.dao.deleteById(id);
	}
	
	public int updateDocumentStatus(int id,int flg){
		Db.update("update t_document set flg="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		Document tea = Document.dao.findFirst("select * from t_document where id = ?",id);
		if(tea != null){
			return tea.getInt("flg");
		}else{
			return 0;
		}
	}
}
