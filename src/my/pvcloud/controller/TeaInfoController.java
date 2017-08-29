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
import my.core.model.Tea;
import my.pvcloud.model.TeaModel;
import my.pvcloud.service.TeaService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/teaInfo", path = "/pvcloud")
public class TeaInfoController extends Controller {

	TeaService service = Enhancer.enhance(TeaService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 茶列表
	 */
	public void index(){
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<Tea> list = service.queryByPage(page, size);
		ArrayList<TeaModel> models = new ArrayList<>();
		TeaModel model = null;
		for(Tea tea : list.getList()){
			model = new TeaModel();
			model.setId(tea.getInt("id"));
			model.setName(tea.getStr("tea_title"));
			model.setPrice(tea.getBigDecimal("tea_price"));
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
		render("addTea.jsp");
	}
	
	//保存茶叶
	public void saveTea(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile = getFile("coverImg");
		String title = getPara("title");
		BigDecimal price = StringUtil.toBigDecimal(getPara("price"));
		String typeCd = getPara("typeCd");
		String content = getPara("content");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
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
        tea.set("tea_price",price);
        tea.set("type_cd",typeCd);
        tea.set("create_time", DateUtil.getNowTimestamp());
        tea.set("update_time", DateUtil.getNowTimestamp());
        tea.set("tea_desc", content);
        tea.set("desc_url", contentUrl);
        tea.set("flg", 1);
		boolean ret = Tea.dao.saveInfo(tea);
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
