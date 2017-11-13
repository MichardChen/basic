package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Tea;
import my.core.model.TeapriceLog;
import my.pvcloud.model.TeaPriceListModel;
import my.pvcloud.service.TeaPriceLogService;
import my.pvcloud.util.StringUtil;
/**
 * 新茶发行价格controller，暂时没有用到
 * @author Chen Dang
 * @date 2017年11月13日 下午7:24:18 
 * @version 1.0
 * @Description:
 */
@ControllerBind(key = "/teaPriceLogInfo", path = "/pvcloud")
public class TeaPriceLogController extends Controller {

	TeaPriceLogService service = Enhancer.enhance(TeaPriceLogService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("name");
		Page<TeapriceLog> list = service.queryByPage(page, size);
		ArrayList<TeaPriceListModel> models = new ArrayList<>();
		TeaPriceListModel model = null;
		for(TeapriceLog record : list.getList()){
			model = new TeaPriceListModel();
			model.setId(record.getInt("id"));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
			}
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setFromPrice(StringUtil.toString(record.getBigDecimal("price"))+"元/件");
			model.setToPrice(StringUtil.toString(record.getBigDecimal("changed_price"))+"元/件");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("teapricelog.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String name=getSessionAttr("name");
		this.setSessionAttr("name",name);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<TeapriceLog> list = service.queryByPageParams(page, size,name);
		ArrayList<TeaPriceListModel> models = new ArrayList<>();
		TeaPriceListModel model = null;
		for(TeapriceLog record : list.getList()){
			model = new TeaPriceListModel();
			model.setId(record.getInt("id"));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
			}
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setFromPrice(StringUtil.toString(record.getBigDecimal("price"))+"元/件");
			model.setToPrice(StringUtil.toString(record.getBigDecimal("changed_price"))+"元/件");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("teapricelog.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String name = getSessionAttr("name");
		String pname = getPara("name");
		name = pname;
		this.setSessionAttr("name",name);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<TeapriceLog> list = service.queryByPageParams(page, size, name);
			ArrayList<TeaPriceListModel> models = new ArrayList<>();
			TeaPriceListModel model = null;
			for(TeapriceLog record : list.getList()){
				model = new TeaPriceListModel();
				model.setId(record.getInt("id"));
				Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
				if(tea != null){
					model.setTeaName(tea.getStr("tea_title"));
				}
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				model.setFromPrice(StringUtil.toString(record.getBigDecimal("price"))+"元/件");
				model.setToPrice(StringUtil.toString(record.getBigDecimal("changed_price"))+"元/件");
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("teapricelog.jsp");
	}
	
	
	/**
	 * 更新
	 */
	public void update(){
		
	}
}
