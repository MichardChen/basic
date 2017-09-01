package my.pvcloud.controller;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.Carousel;
import my.core.model.WareHouse;
import my.core.vo.WareHouseVO;
import my.pvcloud.service.WareHouseService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/warehouseInfo", path = "/pvcloud")
public class WareHouseController extends Controller {

	WareHouseService service = Enhancer.enhance(WareHouseService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 仓库列表
	 */
	public void index(){
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<WareHouse> list = service.queryByPage(page, size);
		ArrayList<WareHouseVO> models = new ArrayList<>();
		WareHouseVO model = null;
		for(WareHouse house : list.getList()){
			model = new WareHouseVO();
			model.setId(house.getInt("id"));
			model.setMark(house.getStr("mark"));
			model.setCreateTime(StringUtil.toString(house.getTimestamp("create_time")));
			model.setName(house.getStr("warehouse_name"));
			model.setFlg(house.getInt("flg"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("warehouse.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
			Page<WareHouse> list = service.queryByPage(page, size);
			ArrayList<WareHouseVO> models = new ArrayList<>();
			WareHouseVO model = null;
			for(WareHouse house : list.getList()){
				model = new WareHouseVO();
				model.setId(house.getInt("id"));
				model.setMark(house.getStr("mark"));
				model.setCreateTime(StringUtil.toString(house.getTimestamp("create_time")));
				model.setName(house.getStr("warehouse_name"));
				model.setFlg(house.getInt("flg"));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("warehouse.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		WareHouse model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
		render("addWarehouse.jsp");
	}
	
	/**
	 * 新增
	 */
	public void saveWareHouse(){
		WareHouse house = new WareHouse();
		house.set("mark", getPara("mark"));
		house.set("warehouse_name", getPara("name"));
		house.set("create_time", DateUtil.getNowTimestamp());
		house.set("update_time", DateUtil.getNowTimestamp());
		house.set("flg", 1);
		//保存
		boolean ret = WareHouse.dao.saveInfo(house);
		if(ret){
			setAttr("message","新增成功");
		}else{
			setAttr("message","新增失败");
		}
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = getParaToInt("id");
			int ret = service.updateFlg(id, 0);
			if(ret==0){
				setAttr("message", "删除成功");
			}else{
				setAttr("message", "删除失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
