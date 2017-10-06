package my.pvcloud.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import my.core.model.StoreImage;
import my.core.model.Tea;
import my.pvcloud.model.StoreModel;
import my.pvcloud.model.TeaModel;
import my.pvcloud.service.StoreService;
import my.pvcloud.service.TeaService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.HttpURLConnectionUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.QRCodeUtil;
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
		removeSessionAttr("status");
		Page<Store> list = service.queryByPage(page, size);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			model.setStatusCd(store.getStr("status"));
			CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
			if(statusCodeMst != null){
				model.setStatus(statusCodeMst.getStr("name"));
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
		String s=getSessionAttr("status");
		this.setSessionAttr("status",s);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Store> list = service.queryByPageParams(page, size,title,s);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			model.setStatusCd(store.getStr("status"));
			CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
			if(statusCodeMst != null){
				model.setStatus(statusCodeMst.getStr("name"));
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
		String s = getPara("status");
		title = ptitle;
		
		this.setSessionAttr("title",title);
		this.setSessionAttr("status",s);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Store> list = service.queryByPageParams(page, size,title,s);
			ArrayList<StoreModel> models = new ArrayList<>();
			StoreModel model = null;
			for(Store store : list.getList()){
				model = new StoreModel();
				model.setId(store.getInt("id"));
				model.setTitle(store.getStr("store_name"));
				model.setFlg(store.getInt("flg"));
				model.setStatusCd(store.getStr("status"));
				CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
				if(statusCodeMst != null){
					model.setStatus(statusCodeMst.getStr("name"));
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
		int id = StringUtil.toInteger(getPara("id"));
		Store store = service.queryById(id);
		List<StoreImage> imgs = StoreImage.dao.queryStoreImages(id);
		List<String> url = new ArrayList<>();
		for(StoreImage imgImage : imgs){
			url.add(imgImage.getStr("img"));
		}
		setAttr("model", store);
		setAttr("imgs", url);
		render("storeInfoAlter.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			String status = getPara("status");
			int ret = service.updateFlg(id, status);
			if(ret==0){
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	//生成二维码
	public void generateQRCode() throws Exception{
		
		int storeId = StringUtil.toInteger(getPara("id"));
		QRCodeUtil.QRCodeCreate("http://192.168.1.131:8087/zznj/rest/bindMember?storeId="+storeId, "F://qrcode.jpg", 15, "F://icon.png");
        HttpServletResponse response = getResponse();
		response.setContentType("application/binary;charset=ISO8859_1");
	    //设置Content-Disposition  
	    response.setHeader("Content-Disposition", "attachment;filename=a.jpeg");  
	    //读取目标文件，通过response将目标文件写到客户端  
	    //获取目标文件的绝对路径  
	    String fullFileName = "F://qrcode.jpg";  
	    //读取文件  
	    InputStream in = new FileInputStream(fullFileName);  
	    OutputStream out = response.getOutputStream();  
	          
	    //写文件  
	    int b;  
	    while((b=in.read())!= -1)  {  
	            out.write(b);  
	        }  
	          
	    in.close();  
	    out.close();  
	}
}
