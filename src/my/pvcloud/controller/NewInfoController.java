package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import my.core.model.Admin;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.News;
import my.core.model.ReturnData;
import my.core.model.User;
import my.core.vo.MemberVO;
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
		removeSessionAttr("title");
		removeSessionAttr("type");
		removeSessionAttr("hot");
		removeSessionAttr("createTime1");
		removeSessionAttr("createTime2");
		
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
			int userId = news.getInt("update_user_id") == null ? 0 : news.getInt("update_user_id");
			User updateUser = User.dao.queryById(userId);
			if(updateUser != null){
				model.setUpdateUser(updateUser.getStr("username"));
			}else{
				model.setUpdateUser("");
			}
			model.setContent(StringUtil.substring(news.getStr("content"), 0, 100));
			model.setUpdateTime(StringUtil.toString(news.getTimestamp("update_time")));
			model.setHotFlg(news.getInt("hot_flg"));
			model.setUrl(news.getStr("content_url"));
			model.setTopFlg(news.getInt("top_flg")==null?0:news.getInt("top_flg"));
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
		String ptitle = getPara("title");
		this.setSessionAttr("title",ptitle);
		
		String ptype = getPara("type");
		this.setSessionAttr("type",ptype);
		
		String phot = getPara("hot");
		this.setSessionAttr("hot",phot);
		
		String pcreateTime1 = getPara("createTime1");
		this.setSessionAttr("createTime1",pcreateTime1);
		
		String pcreateTime2 = getPara("createTime2");
		this.setSessionAttr("createTime2",pcreateTime2);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        ArrayList<NewsModel> models = new ArrayList<>();
			NewsModel model = null;
			Page<News> newsList = News.dao.queryNewsListByPage(page, size, ptitle,ptype,phot,pcreateTime1,pcreateTime2);
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
				model.setTopFlg(news.getInt("top_flg")==null?0:news.getInt("top_flg"));
				model.setCreateTime(StringUtil.toString(news.getTimestamp("create_time")));
				User user = User.dao.queryById(news.getInt("create_user"));
				if(user != null){
					model.setCreateUser(user.getStr("username"));
				}else{
					model.setCreateUser("");
				}
				User updateUser = User.dao.queryById(news.getInt("update_user_id"));
				if(updateUser != null){
					model.setUpdateUser(updateUser.getStr("username"));
				}else{
					model.setUpdateUser("");
				}
				model.setContent(StringUtil.substring(news.getStr("content"), 0, 100));
				model.setUpdateTime(StringUtil.toString(news.getTimestamp("update_time")));
				model.setHotFlg(news.getInt("hot_flg"));
				model.setUrl(news.getStr("content_url"));
				models.add(model);
			}
			
			setAttr("newsList", newsList);
			setAttr("sList", models);
			render("news.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByPage(){
			String title=getSessionAttr("title");
			String ptype = getSessionAttr("type");
			String phot = getSessionAttr("hot");
			String pcreateTime1 = getSessionAttr("createTime1");
			String pcreateTime2 = getSessionAttr("createTime2");
			
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        ArrayList<NewsModel> models = new ArrayList<>();
			NewsModel model = null;
			Page<News> newsList = News.dao.queryNewsListByPage(page, size, title,ptype,phot,pcreateTime1,pcreateTime2);
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
				model.setTopFlg(news.getInt("top_flg")==null?0:news.getInt("top_flg"));
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
				int updateUserId = news.getInt("update_user_id") == null ? 0 : news.getInt("update_user_id");
				User updateUser = User.dao.queryById(updateUserId);
				if(updateUser != null){
					model.setUpdateUser(updateUser.getStr("username"));
				}else{
					model.setUpdateUser("");
				}
				model.setContent(StringUtil.substring(news.getStr("content"), 0, 100));
				model.setUpdateTime(StringUtil.toString(news.getTimestamp("update_time")));
				model.setHotFlg(news.getInt("hot_flg"));
				model.setUrl(news.getStr("content_url"));
				models.add(model);
			}
			
			setAttr("newsList", newsList);
			setAttr("sList", models);
			render("news.jsp");
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
		setAttr("closeFlg",1);
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile = getFile("newImg");
		int hot = StringUtil.toInteger(getPara("hot"));
		String newsTitle = StringUtil.checkCode(getPara("newsTitle"));
		String newsTypeCd = StringUtil.checkCode(getPara("newsTypeCd"));
		String content = StringUtil.formatHTML(newsTitle, StringUtil.checkCode(getPara("content")));
		String useLink = StringUtil.checkCode(getPara("useLink"));
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
			/*StringBuilder sb = new StringBuilder();
			FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html");
			PrintStream printStream = new PrintStream(fos);
			sb.append(content);
			printStream.print(sb);*/
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(Constants.FILE_HOST.FILE+uuid+".html"),"utf-8"),true);
			pw.println(content);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String contentUrl = Constants.HOST.FILE+uuid+".html";
		int operateUserId = (Integer)getSessionAttr("agentId");
		if(StringUtil.equals(useLink, "1")){
			contentUrl = StringUtil.checkCode(getPara("link"));
		}
        
		//保存资讯
		int ret = News.dao.saveNews(logo
								   ,newsTitle
								   ,newsTypeCd
								   ,hot
								   ,(Integer)getSessionAttr("agentId")
								   ,1
								   ,content
								   ,contentUrl
								   ,operateUserId);
		if(ret != 0){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "新增资讯:"+newsTitle);
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
		setAttr("closeFlg",1);
		String id = getPara("custId");
		int integral = getParaToInt("integral");
		String phoneNum = StringUtil.checkCode(getPara("phoneNum"));
		String addrname = StringUtil.checkCode(getPara("addrname"));
		News custInfo = new News();
		int custId = 0;
		if(!("").equals(id) && id!=null){
			custId = getParaToInt("custId");
			custInfo = service.queryById(custId);
		}
		custInfo.set("integral", integral);
		custInfo.set("phonenum", phoneNum);
		custInfo.set("addrname", addrname);
		int operateUserId=(Integer)getSessionAttr("agentId");
		custInfo.set("update_user_id", operateUserId);
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
			int operateUserId = (Integer)getSessionAttr("agentId");
			int ret = service.updateFlg(newsId, 0,operateUserId);
			if(ret==0){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "删除资讯,id:"+newsId);
				setAttr("message", "删除成功");
			}else{
				setAttr("message", "删除失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}

	//置顶
	public void saveTop(){
		try{
			int newsId = getParaToInt("newsId");
			int topFlg = getParaToInt("top");
			int max = 1;
			if(topFlg == 0){
				//取消置顶
				max = 0;
			}else{
				//置顶
				News news = News.dao.queryMaxNews();
				if(news != null){
					if(news.getInt("top_flg")!=null){
						max = news.getInt("top_flg")+1;
					}
				}
			}
			int ret = News.dao.saveTop(newsId,max);
			if(ret!=0){
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
