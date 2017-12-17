package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.model.CodeMst;
import my.core.vo.CodeMstVO;
import my.pvcloud.service.CodemstService;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/codemstInfo", path = "/pvcloud")
public class CodemstController extends Controller {

	CodemstService service = Enhancer.enhance(CodemstService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("name");
		Page<CodeMst> list = service.queryByPage(page, size);
		ArrayList<CodeMstVO> models = new ArrayList<>();
		CodeMstVO model = null;
		for(CodeMst data : list.getList()){
			model = new CodeMstVO();
			model.setCode(data.getStr("code"));
			model.setName(data.getStr("name"));
			CodeMst pCodeMst = CodeMst.dao.queryCodestByCode(data.getStr("pcode"));
			if(pCodeMst != null){
				model.setPname(pCodeMst.getStr("name"));
			}
			
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("codemst.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String name=getSessionAttr("name");
		this.setSessionAttr("name",name);
		
		String pcode=getSessionAttr("pcode");
		this.setSessionAttr("pcode",pcode);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
		
        Page<CodeMst> list = service.queryByPageParams(page, size, name,pcode);
		ArrayList<CodeMstVO> models = new ArrayList<>();
		CodeMstVO model = null;
		for(CodeMst data : list.getList()){
			model = new CodeMstVO();
			model.setCode(data.getStr("code"));
			model.setName(data.getStr("name"));
			CodeMst pCodeMst = CodeMst.dao.queryCodestByCode(data.getStr("pcode"));
			if(pCodeMst != null){
				model.setPname(pCodeMst.getStr("name"));
			}
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("codemst.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String name = getSessionAttr("name");
		String pname = getPara("name");
		name = pname;
		
		
		String pcode = getSessionAttr("pcode");
		String ppcode = getPara("pcode");
		pcode = ppcode;
		
		this.setSessionAttr("name",name);
		this.setSessionAttr("pcode", pcode);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<CodeMst> list = service.queryByPageParams(page, size, name,pcode);
			ArrayList<CodeMstVO> models = new ArrayList<>();
			CodeMstVO model = null;
			for(CodeMst data : list.getList()){
				model = new CodeMstVO();
				model.setCode(data.getStr("code"));
				CodeMst pCodeMst = CodeMst.dao.queryCodestByCode(data.getStr("pcode"));
				if(pCodeMst != null){
					model.setPname(pCodeMst.getStr("name"));
				}
				model.setName(data.getStr("name"));
				model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("codemst.jsp");
	}
}
