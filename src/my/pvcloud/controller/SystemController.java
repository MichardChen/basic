package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.CodeMst;
import my.core.model.FeedBack;
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
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
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
	public void queryByConditionByPage(){
		/*try {
			
			String custInfo=getSessionAttr("custInfo");
			String custValue=getSessionAttr("custValue");
				
			Page<News> custInfoList = new Page<News>(null, 0, 0, 0, 0);	
				
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0){
	            page = 1;
	        }
			if(custInfo!=null){
				if(("addrName").equals(custInfo)){
					custInfoList = service.queryByPage(page, size);
				}else if(("phoneNum").equals(custInfo)){
					custInfoList = service.queryByPage(page, size);
				}else{
					custInfoList = service.queryByPage(page, size);
				}
			}else{
				custInfoList = service.queryByPage(page, size);
			}
			setAttr("custInfoList", custInfoList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("custInfo.jsp");*/
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
			String data1 = getPara("data1");
			String data2 = getPara("data2");
			String mark = getPara("mark");
			
			SystemVersionControl svc = new SystemVersionControl();
			svc.set("id", id);
			svc.set("data1", data1);
			svc.set("data2", data2);
			svc.set("mark", mark);
			svc.set("update_time", DateUtil.getNowTimestamp());
			boolean ret = SystemVersionControl.dao.updateInfo(svc);
			if(ret){
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
