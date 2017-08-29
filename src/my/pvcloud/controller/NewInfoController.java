package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
import my.core.model.Admin;
import my.core.model.CodeMst;
import my.core.model.News;
import my.core.model.ReturnData;
import my.core.model.User;
import my.pvcloud.model.NewsModel;
import my.pvcloud.service.NewsInfoService;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

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
		ArrayList<NewsModel> models = new ArrayList<>();
		NewsModel model = null;
		for(News news : newsList.getList()){
			model = new NewsModel();
			model.setId(news.getInt("id"));
			model.setTitle(news.getStr("news_title"));
			CodeMst type = CodeMst.dao.queryCodestByCode(news.getStr("news_type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}else{
				model.setType("");
			}
			
			Integer status = (Integer)news.getInt("flg");
			model.setFlg(status);
			if(status == 1){
				model.setStatus("正常");
			}else{
				model.setStatus("删除");
			}
			
			model.setCreateTime(StringUtil.toString(news.getTimestamp("create_time")));
			User user = User.dao.queryById(news.getInt("create_user"));
			if(user != null){
				model.setCreateUser(user.getStr("username"));
			}else{
				model.setCreateUser("");
			}
			model.setUrl(news.getStr("content_url"));
			models.add(model);
		}
		
		setAttr("newsList", newsList);
		setAttr("sList", models);
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
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile = getFile("newImg");
		int hot = StringUtil.toInteger(getPara("hot"));
		String newsTitle = getPara("newsTitle");
		String newsTypeCd = getPara("newsTypeCd");
		String content = getPara("content");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    File t=new File(Constants.FILE_HOST.LOCALHOST+uuid+"."+names[1]);
		    logo = Constants.HOST.LOCALHOST+uuid+"."+names[1];
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
		int ret = News.dao.saveNews(logo
								   ,newsTitle
								   ,newsTypeCd
								   ,hot
								   ,(Integer)getSessionAttr("agentId")
								   ,0
								   ,content
								   ,contentUrl);
		if(ret != 0){
			setAttr("message","新增成功");
		}else{
			setAttr("message","新增失败");
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
		    File t=new File(Constants.FILE_HOST.LOCALHOST+uuid+"."+names[1]);
		    String url = Constants.HOST.LOCALHOST+uuid+"."+names[1];
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

	//推送
	public void push(){
		try{
			int newsId = getParaToInt("newsId");
			int ret = service.updateFlg(newsId, 1);
			if(ret==0){
				setAttr("message", "发布成功");
			}else{
				setAttr("message", "发布失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
