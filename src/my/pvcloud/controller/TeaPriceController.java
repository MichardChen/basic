package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Tea;
import my.core.model.TeaPrice;
import my.pvcloud.model.TeaPriceListModel;
import my.pvcloud.service.TeaPriceService;
import my.pvcloud.util.StringUtil;
/**
 * 参考价controller
 * @author Chen Dang
 * @date 2017年11月13日 下午7:24:00 
 * @version 1.0
 * @Description:
 */
@ControllerBind(key = "/teaPriceInfo", path = "/pvcloud")
public class TeaPriceController extends Controller {

	TeaPriceService service = Enhancer.enhance(TeaPriceService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("name");
		Page<TeaPrice> list = service.queryByPage(page, size);
		ArrayList<TeaPriceListModel> models = new ArrayList<>();
		TeaPriceListModel model = null;
		for(TeaPrice record : list.getList()){
			model = new TeaPriceListModel();
			model.setId(record.getInt("id"));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
			}
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setExpireTime(StringUtil.toString(record.getTimestamp("expire_time")));
			model.setFromPrice(StringUtil.toString(record.getBigDecimal("from_price"))+"元/片");
			model.setToPrice(StringUtil.toString(record.getBigDecimal("to_price"))+"元/片");
			model.setReferencePrice(StringUtil.toString(record.getBigDecimal("reference_price"))+"元/片");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("teaprice.jsp");
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
        Page<TeaPrice> list = service.queryByPageParams(page, size,name);
		ArrayList<TeaPriceListModel> models = new ArrayList<>();
		TeaPriceListModel model = null;
		for(TeaPrice record : list.getList()){
			model = new TeaPriceListModel();
			model.setId(record.getInt("id"));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
			}
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setExpireTime(StringUtil.toString(record.getTimestamp("expire_time")));
			model.setFromPrice(StringUtil.toString(record.getBigDecimal("from_price"))+"元/片");
			model.setToPrice(StringUtil.toString(record.getBigDecimal("to_price"))+"元/片");
			model.setReferencePrice(StringUtil.toString(record.getBigDecimal("reference_price"))+"元/片");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("teaprice.jsp");
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
	        
	        Page<TeaPrice> list = service.queryByPageParams(page, size, name);
			ArrayList<TeaPriceListModel> models = new ArrayList<>();
			TeaPriceListModel model = null;
			for(TeaPrice record : list.getList()){
				model = new TeaPriceListModel();
				model.setId(record.getInt("id"));
				Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
				if(tea != null){
					model.setTeaName(tea.getStr("tea_title"));
				}
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				model.setExpireTime(StringUtil.toString(record.getTimestamp("expire_time")));
				model.setFromPrice(StringUtil.toString(record.getBigDecimal("from_price"))+"元/片");
				model.setToPrice(StringUtil.toString(record.getBigDecimal("to_price"))+"元/片");
				model.setReferencePrice(StringUtil.toString(record.getBigDecimal("reference_price"))+"元/片");
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("teaprice.jsp");
	}
	
	
	/**
	 * 更新
	 */
	public void update(){
		
	}
}
