package my.pvcloud.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import my.core.model.News;
import my.core.model.ReturnData;
import my.pvcloud.service.NewsInfoService;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;

@ControllerBind(key = "/newsInfo", path = "/pvcloud")
public class NewInfoController extends Controller {

	NewsInfoService service = Enhancer.enhance(NewsInfoService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 用户建档
	 */
	public void index(){
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<News> newsList = service.queryByPage(page, size);
		setAttr("newsList", newsList);
		render("news.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByCondition(){
		try {
			String ccustInfo = getSessionAttr("custInfo");
			String ccustValue = getSessionAttr("custValue");
			
			Page<News> custInfoList = new Page<News>(null, 0, 0, 0, 0);
			
			String custInfo = getPara("cInfo");
			String custValue = getPara("cValue");
			
			if(("").equals(custInfo) || custInfo==null){
				custInfo = ccustInfo;
			}
			if(("").equals(custValue) || custValue==null){
				custInfo = ccustValue;
			}
			
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
			//用户名称
			if(("addrName").equals(custInfo)){
				custInfoList = service.queryByPage(page, size);
			//用户地址
			}else if(("phoneNum").equals(custInfo)){
				custInfoList = service.queryByPage(page, size);
			}else{
				custInfoList = service.queryByPage(page, size);
			}
			setAttr("custInfoList", custInfoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("custInfo.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		try {
			
			String custInfo=getSessionAttr("custInfo");
			String custValue=getSessionAttr("custValue");
				
			Page<News> custInfoList = new Page<News>(null, 0, 0, 0, 0);	
				
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
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
		render("custInfo.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("custId");
		int custId = 0;
		if(!("").equals(id) && id!=null){
			custId = getParaToInt("custId");
		}
		News custInfo = service.queryById(custId);
		setAttr("custInfo", custInfo);
		render("custInfoAlter.jsp");
	}
	
	//增加资讯初始化
	public void addNews(){
		render("addNews.jsp");
	}
	
	//保存资讯
	public void saveNews(){
		String newsTitle = getPara("newsTitle");
		String newsTypeCd = getPara("newsTypeCd");
		UploadFile uploadFile = getFile("file");
		String content = getPara("content");
		FileService fs=new FileService();
		
		//上传文件
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    String uuid = UUID.randomUUID().toString();
		    File t=new File(Constants.FILE_HOST.LOCALHOST+uuid+"."+names[1]);
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file)/2, ImageTools.getImgHeight(file)/2, 0.5f);
		    file.delete();
		}
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
		    File t=new File(Constants.FILE_HOST.LOCALHOST+uuid+"."+names[1]);
		    String url = Constants.HOST.LOCALHOST+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file)/2, ImageTools.getImgHeight(file)/2, 0.5f);
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
		String id = getPara("custId");
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
		}
		
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int newsId = getParaToInt("newsId");
			int ret = service.updateFlg(newsId, 0);
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
