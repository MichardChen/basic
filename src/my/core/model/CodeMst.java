package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_codemst", pk = "id")
public class CodeMst extends Model<CodeMst> {
		
	public static final CodeMst dao = new CodeMst();
	
	public CodeMst queryCodestByCode(String code){
		return CodeMst.dao.findFirst("select * from mk_codemst where code=?",code);
	}
	
	public CodeMst queryCodestByName(String name){
		return CodeMst.dao.findFirst("select * from mk_codemst where name=?",name);
	}
	
	public List<CodeMst> queryCodestByPcode(String code){
		return CodeMst.dao.find("select * from mk_codemst where pcode=?",code);
	}
}
