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
import my.core.model.Document;
import my.core.model.ReturnData;
import my.core.model.Tea;
import my.pvcloud.model.DocumentModel;
import my.pvcloud.model.TeaModel;
import my.pvcloud.service.Service;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/documentInfo", path = "/pvcloud")
public class DodumentController extends Controller {

	Service service = Enhancer.enhance(Service.class);
	
	int page=1;
	int size=10;
	
	public void index(){
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<Document> list = service.queryByPage(page, size);
		ArrayList<DocumentModel> models = new ArrayList<>();
		DocumentModel model = null;
		for(Document document : list.getList()){
			model = new DocumentModel();
			model.setId(document.getInt("id"));
			model.setContent(document.getStr("content"));
			model.setFlg(document.getInt("flg"));
			model.setTitle(document.getStr("title"));
			model.setUrl(document.getStr("desc_url"));
			CodeMst type = CodeMst.dao.queryCodestByCode(document.getStr("type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			model.setFlg(document.getInt("flg"));
			if(document.getInt("flg")==1){
				model.setStatus("通过");
			}else{
				model.setStatus("未通过");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("document.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByCondition(){
		/*try {
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
		render("custInfo.jsp");*/
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		/*try {
			
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
		render("custInfo.jsp");*/
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		int teaId = 0;
		if(!("").equals(id) && id!=null){
			teaId = getParaToInt("id");
		}
		Document document = service.queryById(teaId);
		setAttr("document", document);
		render("custInfoAlter.jsp");
	}
	
	//增加
	public void addDocument(){
		render("addDocument.jsp");
	}
	
	//新增保存
	public void saveDocument(){
		//表单中有提交图片，要先获取图片
		String title = getPara("title");
		String typeCd = getPara("typeCd");
		String content = getPara("content");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		//生成html文件
		try {
			StringBuilder sb = new StringBuilder();
			FileOutputStream fos = new FileOutputStream(Constants.FILE_HOST.DOCUMENT+uuid+".html");
			PrintStream printStream = new PrintStream(fos);
			sb.append(content);
			printStream.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        String contentUrl = Constants.HOST.DOCUMENT+uuid+".html";
		//保存
        Document document = new Document();
        document.set("title",title);
        document.set("type_cd",typeCd);
        document.set("create_time", DateUtil.getNowTimestamp());
        document.set("update_time", DateUtil.getNowTimestamp());
        document.set("content", content);
        document.set("desc_url", contentUrl);
        document.set("flg", 1);
		boolean ret = Document.dao.saveInfo(document);
		if(ret){
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
		    File t=new File(Constants.FILE_HOST.DOCUMENT+uuid+"."+names[1]);
		    String url = Constants.HOST.DOCUMENT+uuid+"."+names[1];
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
		Document teaInfo = service.queryById(StringUtil.toInteger(getPara("id")));
		setAttr("teaInfo", teaInfo);
		render("editTea.jsp");
	}
}