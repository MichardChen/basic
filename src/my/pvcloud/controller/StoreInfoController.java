package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.ReturnData;
import my.core.model.Store;
import my.core.model.Tea;
import my.pvcloud.model.StoreModel;
import my.pvcloud.model.TeaModel;
import my.pvcloud.service.StoreService;
import my.pvcloud.service.TeaService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/storeInfo", path = "/pvcloud")
public class StoreInfoController extends Controller {

	StoreService service = Enhancer.enhance(StoreService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<Store> list = service.queryByPage(page, size);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			if(store.getInt("flg")==1){
				model.setStatus("通过");
			}else if(store.getInt("flg")==0){
				model.setStatus("未通过");
			}else if(store.getInt("flg")==2){
				model.setStatus("待审核");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("store.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Store> list = service.queryByPageParams(page, size,title);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			if(store.getInt("flg")==1){
				model.setStatus("通过");
			}else if(store.getInt("flg")==0){
				model.setStatus("未通过");
			}else if(store.getInt("flg")==2){
				model.setStatus("待审核");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("store.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		this.setSessionAttr("title",title);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Store> list = service.queryByPageParams(page, size,title);
			ArrayList<StoreModel> models = new ArrayList<>();
			StoreModel model = null;
			for(Store store : list.getList()){
				model = new StoreModel();
				model.setId(store.getInt("id"));
				model.setTitle(store.getStr("store_name"));
				model.setFlg(store.getInt("flg"));
				if(store.getInt("flg")==1){
					model.setStatus("通过");
				}else if(store.getInt("flg")==0){
					model.setStatus("未通过");
				}else if(store.getInt("flg")==2){
					model.setStatus("待审核");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("store.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		Store store = service.queryById(StringUtil.toInteger(id));
		setAttr("model", store);
		render("storeInfoAlter.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			int flg = StringUtil.toInteger(getPara("flg"));
			int ret = service.updateFlg(id, flg);
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
