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

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.model.Tea;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
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
		render("custInfoAlter.jsp");
	}
	
	//增加资讯初始化
	public void addTea(){
		//初始化所有仓库
		List<WareHouse> houses = houseService.queryAllHouse();
		setAttr("houses", houses);
		render("addTea.jsp");
	}
	
	//保存茶叶
	public void saveTea(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile1 = getFile("coverImg1");
		UploadFile uploadFile2 = getFile("coverImg2");
		UploadFile uploadFile3 = getFile("coverImg3");
		UploadFile uploadFile4 = getFile("coverImg4");
		String title = getPara("title");
		BigDecimal price = StringUtil.toBigDecimal(getPara("price"));
		String typeCd = getPara("typeCd");
		String content = getPara("content");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		if(uploadFile1 != null){
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
			StringBuilder sb = new StringBuilder();
			FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html");
			PrintStream printStream = new PrintStream(fos);
			sb.append(content);
			printStream.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        String contentUrl = Constants.HOST.FILE+uuid+".html";
		//保存资讯
        Tea tea = new Tea();
        tea.set("tea_title",title);
        tea.set("brand", getPara("brand"));
       // tea.set("owner_user_id", (Integer)getSessionAttr("agentId"));
       // tea.set("owner_user_type_cd", Constants.USER_TYPE.PLATFORM_USER);
        tea.set("product_place", getPara("place"));
        tea.set("product_date", DateUtil.stringToDate(getPara("birthday")));
        tea.set("sale_from_date", DateUtil.stringToDate(getPara("fromtime")));
        tea.set("sale_to_date", DateUtil.stringToDate(getPara("totime")));
        tea.set("size1", StringUtil.toInteger(getPara("size1")));
        tea.set("size2",  StringUtil.toInteger(getPara("size2")));
        tea.set("total_output", StringUtil.toInteger(getPara("amount")));
      //  tea.set("stock", StringUtil.toInteger(getPara("warehouse")));
     //   tea.set("tea_price",price);
        tea.set("sale_count",0);
        tea.set("certificate_flg", StringUtil.toInteger(getPara("certificate")));
        tea.set("type_cd",typeCd);
        tea.set("create_time", DateUtil.getNowTimestamp());
        tea.set("update_time", DateUtil.getNowTimestamp());
        tea.set("tea_desc", content);
        tea.set("desc_url", contentUrl);
        tea.set("cover_img", logo);
        tea.set("flg", 1);
        int houseId = getParaToInt("houses");
       
		boolean ret = Tea.dao.saveInfo(tea);
		if(ret){
			//增加仓库-茶叶-用户
			WarehouseTeaMember houseTea = new WarehouseTeaMember();
		    houseTea.set("warehouse_id", houseId);
		    houseTea.set("tea_id", tea.getInt("id"));
		    houseTea.set("price", price);
		    houseTea.set("stock", StringUtil.toInteger(getPara("warehouse")));
		    houseTea.set("member_id", (Integer)getSessionAttr("agentId"));
		    houseTea.set("member_type_cd", Constants.USER_TYPE.PLATFORM_USER);
		    houseTea.set("create_time", DateUtil.getNowTimestamp());
		    houseTea.set("update_time", DateUtil.getNowTimestamp());
		    WarehouseTeaMember.dao.saveInfo(houseTea);
			setAttr("message","新增成功");
		}else{
			setAttr("message","新增失败");
		}
		index();
	}
	
	public void updateTea(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile1 = getFile("coverImg1");
		UploadFile uploadFile2 = getFile("coverImg2");
		UploadFile uploadFile3 = getFile("coverImg3");
		UploadFile uploadFile4 = getFile("coverImg4");
		String title = getPara("title");
		BigDecimal price = StringUtil.toBigDecimal(getPara("price"));
		String typeCd = getPara("typeCd");
		String content = getPara("content");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		int reset = StringUtil.toInteger(getPara("reset"));
		if(reset == 1){
			
			if(uploadFile1 != null){
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
				StringBuilder sb = new StringBuilder();
				FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html");
				PrintStream printStream = new PrintStream(fos);
				sb.append(content);
				printStream.print(sb);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
        String contentUrl = Constants.HOST.FILE+uuid+".html";
		//保存资讯
        Tea tea = new Tea();
        tea.set("id", StringUtil.toInteger(getPara("id")));
        tea.set("tea_title",title);
        tea.set("brand", getPara("brand"));
        tea.set("product_place", getPara("place"));
        tea.set("product_date", DateUtil.stringToDate(getPara("birthday")));
        tea.set("sale_from_date", DateUtil.stringToDate(getPara("fromtime")));
        tea.set("sale_to_date", DateUtil.stringToDate(getPara("totime")));
        tea.set("size1", StringUtil.toInteger(getPara("size1")));
        tea.set("size2",  StringUtil.toInteger(getPara("size2")));
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
        
/*        WarehouseTeaMember houseTea = new WarehouseTeaMember();
	    houseTea.set("warehouse_id", houseId);
	    houseTea.set("tea_id", tea.getInt("id"));
	    houseTea.set("price", price);
	    houseTea.set("stock", StringUtil.toInteger(getPara("warehouse")));
	    houseTea.set("member_id", (Integer)getSessionAttr("agentId"));
	    houseTea.set("member_type_cd", Constants.USER_TYPE.PLATFORM_USER);
	    houseTea.set("create_time", DateUtil.getNowTimestamp());
	    houseTea.set("update_time", DateUtil.getNowTimestamp());
	    WarehouseTeaMember.dao.saveInfo(houseTea);
	    */
	    
        if(reset == 1){
        	tea.set("cover_img", logo);
        }
        
        tea.set("flg", 1);
		boolean ret = Tea.dao.updateInfo(tea);
		if(ret){
			setAttr("message","修改成功");
		}else{
			setAttr("message","修改失败");
		}
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
	 * 修改（保存）
	 */
	public void update(){
		/*String id = getPara("custId");
		int integral = getParaToInt("integral");
		String phoneNum = getPara("phoneNum");
		String addrname = getPara("addrname");
		News custInfo = new News();
		int custId = 0;
		if(!("").equals(id) && id!=null){
			custId = getParaToInt("custId");
			custInfo = service.queryById(custId);
		}
		custInfo.set("integral", integral);
		custInfo.set("phonenum", phoneNum);
		custInfo.set("addrname", addrname);
		if(custId==0){
			custInfo.set("register_date", new Date());
			if(service.saveInfo(custInfo)){
				setAttr("message","新增成功");
			}else{
				setAttr("message", "新增失败");
			}
		}else{
			custInfo.set("update_date", new Date());
			if(service.updateInfo(custInfo)){
				setAttr("message","修改成功");
			}else{
				setAttr("message", "修改失败");
			}
		}*/
		
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int teaId = getParaToInt("id");
			int ret = service.updateFlg(teaId, 0);
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
		render("editTea.jsp");
	}
}
