package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

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
	
	public Page<Document> queryByPageParams(int page,int size,String title){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		
		if(StringUtil.isNoneBlank(title)){
			strBuf.append(" and title=?");
			param.add(title);
		}
			
			
			String sql=" from t_document where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return Document.dao.paginate(page, size, select, sql,param.toArray());
		}
	
	public Document queryById(int id){
		return Document.dao.findFirst("select * from t_document where id = ?",id);
	}
	
	public Document queryByTypeCd(String typeCd){
		return Document.dao.findFirst("select * from t_document where type_cd = ?",typeCd);
	}
	
	public List<Document> queryDocumentListByTypeCd(String typeCd){
		return Document.dao.find("select * from t_document where type_cd = ? and flg=1 order by create_time asc",typeCd);
	}
	
	public boolean updateInfo(Document data){
		return new Document().setAttrs(data).update();
	}
	
	public boolean saveInfo(Document data){
		return new Document().setAttrs(data).save();
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
