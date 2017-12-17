package my.core.model;

import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_codemst", pk = "id")
public class CodeMst extends Model<CodeMst> {
		
	public static final CodeMst dao = new CodeMst();
	
	public CodeMst queryCodestByCode(String code){
		return CodeMst.dao.findFirst("select * from t_codemst where code=?",code);
	}
	
	public CodeMst queryCodestByName(String name){
		return CodeMst.dao.findFirst("select * from t_codemst where name=?",name);
	}
	
	public List<CodeMst> queryCodestByPcode(String code){
		return CodeMst.dao.find("select * from t_codemst where pcode=?",code);
	}
	
	public Page<CodeMst> queryLogByPage(int page,int size,String name,String pcode){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(name)){
			strBuf.append("and name like '%"+name+"%'");
		}
		
		if(StringUtil.isNoneBlank(pcode)){
			strBuf.append("and pcode like '%"+pcode+"%'");
		}
		
		sql=" from t_codemst where 1=1 "+strBuf.toString()+" order by create_time desc";
		return CodeMst.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<CodeMst> queryByPage(int page,int size){

		String sql=" from t_codemst where 1=1 order by create_time desc";
		String select="select * ";
		return CodeMst.dao.paginate(page, size, select, sql);
	}
}
