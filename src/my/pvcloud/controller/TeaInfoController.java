package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.sun.java.swing.plaf.motif.resources.motif;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.model.Tea;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.WareHouseVO;
import my.pvcloud.model.TeaModel;
import my.pvcloud.service.TeaService;
import my.pvcloud.service.WareHouseService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/teaInfo", path = "/pvcloud")
public class TeaInfoController extends Controller {

	TeaService service = Enhancer.enhance(TeaService.class);
	WareHouseService houseService = Enhancer.enhance(WareHouseService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 茶列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<Tea> list = service.queryByPage(page, size);
		ArrayList<TeaModel> models = new ArrayList<>();
		TeaModel model = null;
		for(Tea tea : list.getList()){
			model = new TeaModel();
			model.setId(tea.getInt("id"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			model.setName(tea.getStr("tea_title"));
			if(wtm != null){
				model.setPrice(wtm.getBigDecimal("price"));
			}
			model.setUrl(tea.getStr("desc_url"));
			model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			model.setFlg(tea.getInt("flg"));
			if(tea.getInt("flg")==1){
				model.setStatus("正常");
			}else{
				model.setStatus("已删除");
			}
			models.add(model);
		}
		setAttr("teaList", list);
		setAttr("sList", models);
		render("teas.jsp");
	}
	
	/**
	 * 模糊查询底部分页
	 */
	public void queryByPage(){
		String stitle=getSessionAttr("title");
		this.setSessionAttr("title",stitle);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Tea> list = service.queryByPageParams(page, size,stitle);
		ArrayList<TeaModel> models = new ArrayList<>();
		TeaModel model = null;
		for(Tea tea : list.getList()){
			model = new TeaModel();
			model.setId(tea.getInt("id"));
			model.setName(tea.getStr("tea_title"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			if(wtm != null){
				model.setPrice(wtm.getBigDecimal("price"));
			}
			model.setUrl(tea.getStr("desc_url"));
			model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			model.setFlg(tea.getInt("flg"));
			if(tea.getInt("flg")==1){
				model.setStatus("正常");
			}else{
				model.setStatus("已删除");
			}
			models.add(model);
		}
		setAttr("teaList", list);
		setAttr("sList", models);
		render("teas.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		
		String stitle = getPara("title");
		title = stitle;
		
		this.setSessionAttr("title",stitle);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Tea> list = service.queryByPageParams(page, size,title);
			ArrayList<TeaModel> models = new ArrayList<>();
			TeaModel model = null;
			for(Tea tea : list.getList()){
				model = new TeaModel();
				model.setId(tea.getInt("id"));
				model.setName(tea.getStr("tea_title"));
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
				if(wtm != null){
					model.setPrice(wtm.getBigDecimal("price"));
				}
				model.setUrl(tea.getStr("desc_url"));
				model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					model.setType(type.getStr("name"));
				}
				model.setFlg(tea.getInt("flg"));
				if(tea.getInt("flg")==1){
					model.setStatus("正常");
				}else{
					model.setStatus("已删除");
				}
				models.add(model);
			}
			setAttr("teaList", list);
			setAttr("sList", models);
			render("teas.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("teaId");
		int teaId = 0;
		if(!("").equals(id) && id!=null){
			teaId = getParaToInt("teaId");
		}
		Tea teaInfo = service.queryById(teaId);
		setAttr("teaInfo", teaInfo);
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryPlatTeaInfo(teaId, (Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER);
		if(wtm != null){
			int houseId = wtm.getInt("warehouse_id");
			WareHouse house = WareHouse.dao.queryById(houseId);
			if(house != null){
				setAttr("warehouse", house.getStr("warehouse_name"));
			}else{
				setAttr("warehouse", null);
			}
		}else{
			setAttr("warehouse", null);
		}
		render("custInfoAlter.jsp");
	}
	
	//增加资讯初始化
	public void addTea(){
		//初始化所有仓库
		List<WareHouse> houses = houseService.queryAllHouse();
		setAttr("houses", houses);
		render("addTea.jsp");
	}
	
	//后台保存茶叶
	public void saveTea(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile1 = getFile("coverImg1");
		UploadFile uploadFile2 = getFile("coverImg2");
		UploadFile uploadFile3 = getFile("coverImg3");
		UploadFile uploadFile4 = getFile("coverImg4");
		String title = StringUtil.checkCode(getPara("title"));
		BigDecimal price = StringUtil.toBigDecimal(StringUtil.checkCode(getPara("price")));
		String typeCd = StringUtil.checkCode(getPara("typeCd"));
		String content = StringUtil.formatHTML(title, StringUtil.checkCode(getPara("content")));
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		if(uploadFile1 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile1.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile1.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		if(uploadFile2 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile2.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile2.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		if(uploadFile3 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile3.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile3.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		if(uploadFile4 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile4.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile4.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		//生成html文件
		String htmlUrl="";
		try {
			String uuid = UUID.randomUUID().toString();
			htmlUrl = Constants.HOST.FILE+uuid;
			StringBuilder sb = new StringBuilder();
			FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html");
			PrintStream printStream = new PrintStream(fos);
			sb.append(content);
			printStream.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        String contentUrl = htmlUrl+".html";
		//保存资讯
        Tea tea = new Tea();
        tea.set("tea_title",title);
        tea.set("brand", StringUtil.checkCode(getPara("brand")));
        tea.set("product_place", StringUtil.checkCode(getPara("place")));
        tea.set("product_date", DateUtil.stringToDate(StringUtil.checkCode(getPara("birthday"))));
        tea.set("sale_from_date", DateUtil.stringToDate(StringUtil.checkCode(getPara("fromtime"))));
        tea.set("sale_to_date", DateUtil.stringToDate(StringUtil.checkCode(getPara("totime"))));
        tea.set("weight", StringUtil.toInteger(getPara("size1")));
        tea.set("size",  StringUtil.toInteger(getPara("size2")));
        tea.set("total_output", StringUtil.toInteger(getPara("amount")));
        tea.set("tea_price",price);
        tea.set("sale_count",0);
        tea.set("certificate_flg", StringUtil.toInteger(getPara("certificate")));
        tea.set("type_cd",typeCd);
        tea.set("create_time", DateUtil.getNowTimestamp());
        tea.set("update_time", DateUtil.getNowTimestamp());
        tea.set("tea_desc", content);
        tea.set("desc_url", contentUrl);
        tea.set("cover_img", logo);
        tea.set("flg", 1);
        tea.set("status",getPara("status"));
        int houseId = getParaToInt("houses");
       
		int teaId = Tea.dao.saveInfos(tea);
		if(teaId != 0){
			//增加仓库-茶叶-用户
			WarehouseTeaMember houseTea = new WarehouseTeaMember();
		    houseTea.set("warehouse_id", houseId);
		    houseTea.set("tea_id", teaId);
		    houseTea.set("stock", StringUtil.toInteger(getPara("warehouse")));
		    houseTea.set("member_id", (Integer)getSessionAttr("agentId"));
		    houseTea.set("member_type_cd", Constants.USER_TYPE.PLATFORM_USER);
		    houseTea.set("create_time", DateUtil.getNowTimestamp());
		    houseTea.set("update_time", DateUtil.getNowTimestamp());
		    int retId = WarehouseTeaMember.dao.saveWarehouseTeaMember(houseTea);
		    if(retId != 0){
		    	WarehouseTeaMemberItem item = new WarehouseTeaMemberItem();
		    	item.set("warehouse_tea_member_id", retId);
		    	item.set("price", price);
		    	String statusStr = Constants.TEA_STATUS.STOP_SALE;
		    	if(StringUtil.equals(getPara("status"), Constants.NEWTEA_STATUS.ON_SALE)){
		    		statusStr = Constants.TEA_STATUS.ON_SALE;
		    	}
		    	item.set("status", statusStr);
		    	item.set("quality", StringUtil.toInteger(getPara("warehouse")));
		    	item.set("create_time", DateUtil.getNowTimestamp());
		    	item.set("update_time", DateUtil.getNowTimestamp());
		    	item.set("size_type_cd", Constants.TEA_UNIT.ITEM);
		    	boolean save = WarehouseTeaMemberItem.dao.saveInfo(item);
		    	if(save){
		    		setAttr("message","新增成功");
		    	}else{
		    		setAttr("message","新增失败");
		    	}
		    }else{
		    	setAttr("message","新增失败");
		    }
		}else{
			setAttr("message","新增失败");
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "上架茶叶:"+title);
		index();
	}
	
	public void updateTea(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile1 = getFile("coverImg1");
		UploadFile uploadFile2 = getFile("coverImg2");
		UploadFile uploadFile3 = getFile("coverImg3");
		UploadFile uploadFile4 = getFile("coverImg4");
		String title = StringUtil.checkCode(getPara("title"));
		BigDecimal price = StringUtil.toBigDecimal(getPara("price"));
		String typeCd = StringUtil.checkCode(getPara("typeCd"));
		String content = StringUtil.formatHTML(title, StringUtil.checkCode(getPara("content")));
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		int reset = StringUtil.toInteger(getPara("reset"));
		String contentUrl = "";
		if(reset == 1){
			
			if(uploadFile1 != null){
				String uuid = UUID.randomUUID().toString();
				String fileName = uploadFile1.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile1.getFile();
			    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
			    logo = Constants.HOST.TEA+uuid+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			}
			if(uploadFile2 != null){
				String uuid = UUID.randomUUID().toString();
				String fileName = uploadFile2.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile2.getFile();
			    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
			    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			}
			if(uploadFile3 != null){
				String uuid = UUID.randomUUID().toString();
				String fileName = uploadFile3.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile3.getFile();
			    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
			    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			}
			if(uploadFile4 != null){
				String uuid = UUID.randomUUID().toString();
				String fileName = uploadFile4.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile4.getFile();
			    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
			    logo = logo + "," + Constants.HOST.TEA+uuid+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			}
			//生成html文件
			try {
				String uuid = UUID.randomUUID().toString();
				contentUrl = Constants.HOST.FILE+uuid;
				StringBuilder sb = new StringBuilder();
				FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html");
				PrintStream printStream = new PrintStream(fos);
				sb.append(content);
				printStream.print(sb);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		//保存资讯
        Tea tea = new Tea();
        int teaId = StringUtil.toInteger(getPara("id"));
        tea.set("id", teaId);
        tea.set("tea_title",title);
        tea.set("brand", getPara("brand"));
        tea.set("product_place", getPara("place"));
        tea.set("product_date", DateUtil.stringToDate(getPara("birthday")));
        tea.set("sale_from_date", DateUtil.stringToDate(getPara("fromtime")));
        tea.set("sale_to_date", DateUtil.stringToDate(getPara("totime")));
        tea.set("weight", StringUtil.toInteger(getPara("size1")));
        tea.set("size",  StringUtil.toInteger(getPara("size2")));
        tea.set("total_output", StringUtil.toInteger(getPara("amount")));
       // tea.set("stock", StringUtil.toInteger(getPara("warehouse")));
       // tea.set("tea_price",price);
        tea.set("sale_count",0);
        tea.set("certificate_flg", StringUtil.toInteger(getPara("certificate")));
        tea.set("type_cd",typeCd);
        tea.set("create_time", DateUtil.getNowTimestamp());
        tea.set("update_time", DateUtil.getNowTimestamp());
        tea.set("tea_desc", content);
        tea.set("desc_url", contentUrl);
        tea.set("status",getPara("status"));
        if(reset == 1){
        	tea.set("cover_img", logo);
        }
        
        tea.set("flg", 1);
		boolean ret = Tea.dao.updateInfo(tea);
		if(ret){
			//更新库存
			int stock = StringUtil.toInteger(getPara("warehouse"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryPlatTeaInfo(teaId
																			,(Integer)getSessionAttr("agentId")
																			,Constants.USER_TYPE.PLATFORM_USER);
			WarehouseTeaMember wtmsMember = new WarehouseTeaMember();
			wtmsMember.set("id", wtm.getInt("id"));
			wtmsMember.set("stock", stock);
			wtmsMember.set("update_time", DateUtil.getNowTimestamp());
			boolean updateFlg = WarehouseTeaMember.dao.updateInfo(wtmsMember);
			if(updateFlg){
				String status = Constants.TEA_STATUS.STOP_SALE;
				if(StringUtil.equals(getPara("status"), Constants.NEWTEA_STATUS.ON_SALE)){
					status = Constants.TEA_STATUS.ON_SALE;
				}
				int rets = WarehouseTeaMemberItem.dao.updateTeaInfo(wtm.getInt("id"), price, status, stock);
		    	if(rets != 0){
		    		setAttr("message","修改成功");
		    	}else{
		    		setAttr("message","修改失败");
		    	}
			}else{
				setAttr("message","修改失败");
			}
		}else{
			setAttr("message","修改失败");
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "修改茶叶:"+title);
		index();
	}
	
	//上传文件
	public void uploadFile(){
		
		UploadFile uploadFile = getFile("file");
		FileService fs=new FileService();
		
		//上传文件
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    String uuid = UUID.randomUUID().toString();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    String url = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ReturnData data = new ReturnData();
		    Map<String, Object> map = new HashMap<>();
		    map.put("imgUrl", url);
		    map.put("imgName", uuid+"."+names[1]);
		    data.setData(map);
		    renderJson(data);
		}
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int teaId = getParaToInt("id");
			int ret = service.updateFlg(teaId, 0);
			if(ret==0){
				Tea tea = Tea.dao.queryById(teaId);
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "下架茶叶:"+tea.getStr("tea_title"));
				setAttr("message", "删除成功");
			}else{
				setAttr("message", "删除失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}

	//推送
	public void push(){
		/*try{
			int newsId = getParaToInt("newsId");
			int ret = service.updateFlg(newsId, 1);
			if(ret==0){
				setAttr("message", "发布成功");
			}else{
				setAttr("message", "发布失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}*/
		index();
	}
	
	public void editTea(){
		Tea teaInfo = service.queryById(StringUtil.toInteger(getPara("id")));
		setAttr("teaInfo", teaInfo);
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryPlatTeaInfo(StringUtil.toInteger(getPara("id")), (Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER);
		if(wtm != null){
			setAttr("stock", wtm.getInt("stock"));
			int houseId = wtm.getInt("warehouse_id");
			WareHouse house = WareHouse.dao.queryById(houseId);
			if(house != null){
				setAttr("warehouse", house.getStr("warehouse_name"));
			}else{
				setAttr("warehouse", null);
			}
		}else{
			setAttr("warehouse", null);
			setAttr("stock", 0);
		}
		render("editTea.jsp");
	}
}
