package my.pvcloud.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import my.core.constants.Constants;
import my.core.model.GetTeaRecord;
import my.core.model.Log;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.vo.WareHouseVO;
import my.pvcloud.service.WareHouseService;
import my.pvcloud.util.DateUtil;
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
		removeSessionAttr("title");
		Page<WareHouse> list = service.queryByPage(page, size);
		ArrayList<WareHouseVO> models = new ArrayList<>();
		WareHouseVO model = null;
		//查询出所有tea
		List<Tea> teaList = Tea.dao.queryAllTeas();
		Map<Integer, Integer> teaMap = new HashMap<Integer, Integer>();
		for(Tea tea : teaList){
			teaMap.put(tea.getInt("id"), tea.getInt("size"));
		}
		
		for(WareHouse house : list.getList()){
			model = new WareHouseVO();
			model.setId(house.getInt("id"));
			model.setMark(house.getStr("mark"));
			model.setCreateTime(StringUtil.toString(house.getTimestamp("create_time")));
			model.setName(house.getStr("warehouse_name"));
			model.setFlg(house.getInt("flg"));
			model.setUpdateTime(StringUtil.toString(house.getTimestamp("update_time")));
			int updateUserId = house.getInt("update_user_id") == null ? 0 : house.getInt("update_user_id");
			int createUserId = house.getInt("create_user_id") == null ? 0 : house.getInt("create_user_id");
			User updateUser = User.dao.queryById(updateUserId);
			User createUser = User.dao.queryById(createUserId);
			if(createUser != null){
				model.setCreateUser(createUser.getStr("username"));
			}else{
				model.setCreateUser("");
			}
			if(updateUser != null){
				model.setUpdateUser(updateUser.getStr("username"));
			}else{
				model.setUpdateUser("");
			}
			
			//在库
			BigDecimal stock = WarehouseTeaMember.dao.queryWarehouseTeaMemberListCount(house.getInt("id"));
			if(stock != null){
				model.setStock(StringUtil.toString(stock));
			}else{
				model.setStock("0");
			}
			//总库存
			//在售和撤单
			List<Record> records = WarehouseTeaMember.dao.queryWarehouseTeaQuality(house.getInt("id"));
			int onSale = 0;
			for(Record record : records){
				int teaId = record.getInt("teaId");
				String size = record.getStr("size");
				String status = record.getStr("status");
				int quality = record.getInt("quality");
				if(teaId != 0){
					int teaSize = teaMap.get(teaId);
					if(StringUtil.equals(status, Constants.TEA_STATUS.ON_SALE)){
						//在售
						if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
							onSale = onSale+teaSize*quality;
						}
						if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
							onSale = onSale+quality;
						}
					}
				}
			}
			model.setOnSaleStock(StringUtil.toString(onSale));
			//判断取茶
			List<Record> getList = GetTeaRecord.dao.queryWarehouseTeaGetNum(house.getInt("id"));
			int onGet = 0;
			for(Record record : getList){
				int teaId = record.getInt("teaId");
				String size = record.getStr("size");
				int quality = record.getInt("quality");
				if(teaId != 0){
					int teaSize = teaMap.get(teaId);
					//在售
					if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
						onGet = onGet+teaSize*quality;
					}
					if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
						onGet = onGet+quality;
					}
				}
			}
			model.setOnGet(StringUtil.toString(onGet));
			if(stock != null){
				model.setAllStock(StringUtil.toString(stock.add(StringUtil.toBigDecimal(StringUtil.toString(onSale)))));
			}else{
				model.setAllStock(StringUtil.toString(onSale));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("warehouse.jsp");
	}
	
	public void queryByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		this.setSessionAttr("title", title);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<WareHouse> list = service.queryByPageParams(page, size,title);
		ArrayList<WareHouseVO> models = new ArrayList<>();
		WareHouseVO model = null;
		//查询出所有tea
		List<Tea> teaList = Tea.dao.queryAllTeas();
		Map<Integer, Integer> teaMap = new HashMap<Integer, Integer>();
		for(Tea tea : teaList){
			teaMap.put(tea.getInt("id"), tea.getInt("size"));
		}
		for(WareHouse house : list.getList()){
			model = new WareHouseVO();
			model.setId(house.getInt("id"));
			model.setMark(house.getStr("mark"));
			model.setCreateTime(StringUtil.toString(house.getTimestamp("create_time")));
			model.setName(house.getStr("warehouse_name"));
			model.setFlg(house.getInt("flg"));
			model.setUpdateTime(StringUtil.toString(house.getTimestamp("update_time")));
			int updateUserId = house.getInt("update_user_id") == null ? 0 : house.getInt("update_user_id");
			int createUserId = house.getInt("create_user_id") == null ? 0 : house.getInt("create_user_id");
			User updateUser = User.dao.queryById(updateUserId);
			User createUser = User.dao.queryById(createUserId);
			if(createUser != null){
				model.setCreateUser(createUser.getStr("username"));
			}else{
				model.setCreateUser("");
			}
			if(updateUser != null){
				model.setUpdateUser(updateUser.getStr("username"));
			}else{
				model.setUpdateUser("");
			}
			
			BigDecimal stock = WarehouseTeaMember.dao.queryWarehouseTeaMemberListCount(house.getInt("id"));
			if(stock != null){
				model.setStock(StringUtil.toString(stock));
			}else{
				model.setStock("0");
			}
			
			//总库存
			//在售和撤单
			List<Record> records = WarehouseTeaMember.dao.queryWarehouseTeaQuality(house.getInt("id"));
			int onSale = 0;
			for(Record record : records){
				int teaId = record.getInt("teaId");
				String size = record.getStr("size");
				String status = record.getStr("status");
				int quality = record.getInt("quality");
				if(teaId != 0){
					int teaSize = teaMap.get(teaId);
					if(StringUtil.equals(status, Constants.TEA_STATUS.ON_SALE)){
						//在售
						if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
							onSale = onSale+teaSize*quality;
						}
						if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
							onSale = onSale+quality;
						}
					}
				}
			}
			model.setOnSaleStock(StringUtil.toString(onSale));
			//判断取茶
			List<Record> getList = GetTeaRecord.dao.queryWarehouseTeaGetNum(house.getInt("id"));
			int onGet = 0;
			for(Record record : getList){
				int teaId = record.getInt("teaId");
				String size = record.getStr("size");
				int quality = record.getInt("quality");
				if(teaId != 0){
					int teaSize = teaMap.get(teaId);
					//在售
					if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
						onGet = onGet+teaSize*quality;
					}
					if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
						onGet = onGet+quality;
					}
				}
			}
			model.setOnGet(StringUtil.toString(onGet));
			if(stock != null){
				model.setAllStock(StringUtil.toString(stock.add(StringUtil.toBigDecimal(StringUtil.toString(onSale)))));
			}else{
				model.setAllStock(StringUtil.toString(onSale));
			}
			
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
			
		
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
			Page<WareHouse> list = service.queryByPage(page, size);
			ArrayList<WareHouseVO> models = new ArrayList<>();
			WareHouseVO model = null;
			//查询出所有tea
			List<Tea> teaList = Tea.dao.queryAllTeas();
			Map<Integer, Integer> teaMap = new HashMap<Integer, Integer>();
			for(Tea tea : teaList){
				teaMap.put(tea.getInt("id"), tea.getInt("size"));
			}
			for(WareHouse house : list.getList()){
				model = new WareHouseVO();
				model.setId(house.getInt("id"));
				model.setMark(house.getStr("mark"));
				model.setCreateTime(StringUtil.toString(house.getTimestamp("create_time")));
				model.setName(house.getStr("warehouse_name"));
				model.setFlg(house.getInt("flg"));
				int updateUserId = house.getInt("update_user_id") == null ? 0 : house.getInt("update_user_id");
				int createUserId = house.getInt("create_user_id") == null ? 0 : house.getInt("create_user_id");
				User updateUser = User.dao.queryById(updateUserId);
				User createUser = User.dao.queryById(createUserId);
				if(createUser != null){
					model.setCreateUser(createUser.getStr("username"));
				}else{
					model.setCreateUser("");
				}
				if(updateUser != null){
					model.setUpdateUser(updateUser.getStr("username"));
				}else{
					model.setUpdateUser("");
				}
				BigDecimal stock = WarehouseTeaMember.dao.queryWarehouseTeaMemberListCount(house.getInt("id"));
				if(stock != null){
					model.setStock(StringUtil.toString(stock));
				}else{
					model.setStock("0");
				}
				
				//在售和撤单
				List<Record> records = WarehouseTeaMember.dao.queryWarehouseTeaQuality(house.getInt("id"));
				int onSale = 0;
				for(Record record : records){
					int teaId = record.getInt("teaId");
					String size = record.getStr("size");
					String status = record.getStr("status");
					int quality = record.getInt("quality");
					if(teaId != 0){
						int teaSize = teaMap.get(teaId);
						if(StringUtil.equals(status, Constants.TEA_STATUS.ON_SALE)){
							//在售
							if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
								onSale = onSale+teaSize*quality;
							}
							if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
								onSale = onSale+quality;
							}
						}
					}
				}
				model.setOnSaleStock(StringUtil.toString(onSale));
				//判断取茶
				List<Record> getList = GetTeaRecord.dao.queryWarehouseTeaGetNum(house.getInt("id"));
				int onGet = 0;
				for(Record record : getList){
					int teaId = record.getInt("teaId");
					String size = record.getStr("size");
					int quality = record.getInt("quality");
					if(teaId != 0){
						int teaSize = teaMap.get(teaId);
						//在售
						if(StringUtil.equals(size, Constants.TEA_UNIT.ITEM)){
							onGet = onGet+teaSize*quality;
						}
						if(StringUtil.equals(size, Constants.TEA_UNIT.PIECE)){
							onGet = onGet+quality;
						}
					}
				}
				model.setOnGet(StringUtil.toString(onGet));
				if(stock != null){
					model.setAllStock(StringUtil.toString(stock.add(StringUtil.toBigDecimal(StringUtil.toString(onSale)))));
				}else{
					model.setAllStock(StringUtil.toString(onSale));
				}
				
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
		if(StringUtil.isNotBlank(getPara("id"))){
			updateWareHouse();
		}else{
			WareHouse house = new WareHouse();
			house.set("mark", StringUtil.checkCode(getPara("mark")));
			house.set("warehouse_name", StringUtil.checkCode(getPara("name")));
			house.set("create_time", DateUtil.getNowTimestamp());
			house.set("update_time", DateUtil.getNowTimestamp());
			house.set("flg", 1);
			house.set("create_user_id", (Integer)getSessionAttr("agentId"));
			house.set("update_user_id", (Integer)getSessionAttr("agentId"));
			//保存
			boolean ret = WareHouse.dao.saveInfo(house);
			if(ret){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "新增仓库:"+getPara("name"));
				setAttr("message","新增成功");
			}else{
				setAttr("message","新增失败");
			}
			index();
		}
	}
	
	public void updateWareHouse(){
		WareHouse house = new WareHouse();
		house.set("mark", StringUtil.checkCode(getPara("mark")));
		house.set("warehouse_name", StringUtil.checkCode(getPara("name")));
		house.set("update_time", DateUtil.getNowTimestamp());
		house.set("id", StringUtil.toInteger(getPara("id")));
		house.set("update_user_id", (Integer)getSessionAttr("agentId"));
		//保存
		boolean ret = WareHouse.dao.updateInfo(house);
		if(ret){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "修改仓库:"+getPara("name"));
			setAttr("message","修改成功");
		}else{
			setAttr("message","修改失败");
		}
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = getParaToInt("id");
			BigDecimal wtmCounts = WarehouseTeaMember.dao.queryWarehouseTeaMemberListCount(id);
			if((wtmCounts != null)&&(wtmCounts.intValue() != 0)){
				setAttr("message", "删除失败，此仓库还有在库茶叶，不能删除");
			}else{
				int ret = service.updateFlg(id, 0,(Integer)getSessionAttr("agentId"));
				if(ret==0){
					WareHouse wareHouse = WareHouse.dao.queryById(id);
					Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "删除仓库:"+wareHouse.getStr("warehouse_name"));
					setAttr("message", "删除成功");
				}else{
					setAttr("message", "删除失败");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	public void edit(){
		WareHouse house = service.queryById(StringUtil.toInteger(getPara("id")));
		setAttr("data", house);
		render("editHouse.jsp");
	}
}
