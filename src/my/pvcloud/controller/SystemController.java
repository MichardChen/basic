package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.FeedBack;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.SystemVersionControl;
import my.pvcloud.model.FeedBackModel;
import my.pvcloud.model.SystemModel;
import my.pvcloud.service.FeedBackService;
import my.pvcloud.service.SystemService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/systemInfo", path = "/pvcloud")
public class SystemController extends Controller {

	SystemService service = Enhancer.enhance(SystemService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		Page<SystemVersionControl> list = service.queryByPage(page, size);
		ArrayList<SystemModel> models = new ArrayList<>();
		SystemModel model = null;
		for(SystemVersionControl system : list.getList()){
			model = new SystemModel();
			model.setId(system.getInt("id"));
			model.setData1(system.getStr("data1"));
			model.setData2(system.getStr("data2"));
			model.setCreateTime(StringUtil.toString(system.getTimestamp("create_time")));
			model.setMark(system.getStr("mark"));
			CodeMst type = CodeMst.dao.queryCodestByCode(system.getStr("version_type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("system.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByPage(){
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
		Page<SystemVersionControl> list = service.queryByPage(page, size);
		ArrayList<SystemModel> models = new ArrayList<>();
		SystemModel model = null;
		for(SystemVersionControl system : list.getList()){
			model = new SystemModel();
			model.setId(system.getInt("id"));
			model.setData1(system.getStr("data1"));
			model.setData2(system.getStr("data2"));
			model.setCreateTime(StringUtil.toString(system.getTimestamp("create_time")));
			model.setMark(system.getStr("mark"));
			CodeMst type = CodeMst.dao.queryCodestByCode(system.getStr("version_type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("system.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		SystemVersionControl model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
		render("systemAlter.jsp");
	}
	
	/**
	 * 更新
	 */
	public void saveSystem(){
		try{
			int id = getParaToInt("id");
			String data1 = StringUtil.checkCode(getPara("data1"));
			String data2 = StringUtil.checkCode(getPara("data2"));
			String mark = StringUtil.checkCode(getPara("mark"));
			
			SystemVersionControl svc = new SystemVersionControl();
			svc.set("id", id);
			svc.set("data1", data1);
			svc.set("data2", data2);
			svc.set("mark", mark);
			svc.set("update_time", DateUtil.getNowTimestamp());
			boolean ret = SystemVersionControl.dao.updateInfo(svc);
			if(ret){
				SystemVersionControl svcs = SystemVersionControl.dao.querySystemVersionControlById(id);
				if(svcs != null){
					CodeMst vcType = CodeMst.dao.queryCodestByCode(svcs.getStr("version_type_cd"));
					if(vcType != null){
						Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "保存系统配置:"+vcType.getStr("name"));
					}
				}
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
