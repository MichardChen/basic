package my.pvcloud.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.Carousel;
import my.core.model.News;
import my.core.model.SystemVersionControl;
import my.core.vo.CarouselVO;
import my.pvcloud.model.CustInfo;
import my.pvcloud.service.CarouselService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/carouselInfo", path = "/pvcloud")
public class CarouselController extends Controller {

	CarouselService service = Enhancer.enhance(CarouselService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<Carousel> list = service.queryByPage(page, size);
		ArrayList<CarouselVO> models = new ArrayList<>();
		CarouselVO model = null;
		for(Carousel carousel : list.getList()){
			model = new CarouselVO();
			model.setId(carousel.getInt("id"));
			model.setFlg(carousel.getInt("flg"));
			model.setRealUrl(carousel.getStr("real_url"));
			model.setImgUrl(carousel.getStr("img_url"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("carousel.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Carousel> list = service.queryByPage(page, size);
			ArrayList<CarouselVO> models = new ArrayList<>();
			CarouselVO model = null;
			for(Carousel carousel : list.getList()){
				model = new CarouselVO();
				model.setId(carousel.getInt("id"));
				model.setFlg(carousel.getInt("flg"));
				model.setRealUrl(carousel.getStr("real_url"));
				model.setImgUrl(carousel.getStr("img_url"));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("carousel.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		Carousel model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
		render("addCarousel.jsp");
	}
	
	/**
	 * 新增
	 */
	public void saveCarousel(){
		//表单中有提交图片，要先获取图片
		UploadFile uploadFile = getFile("img");
		String realUrl = getPara("realUrl");
		String mark = getPara("mark");
		FileService fs=new FileService();
		
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    File t=new File(Constants.FILE_HOST.IMG+uuid+"."+names[1]);
		    logo = Constants.HOST.IMG+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		
		Carousel c = new Carousel();
		c.set("mark", mark);
		c.set("real_url", realUrl);
		c.set("img_url", logo);
		c.set("create_time", DateUtil.getNowTimestamp());
		c.set("update_time", DateUtil.getNowTimestamp());
		c.set("flg", 1);
		//保存
		boolean ret = Carousel.dao.saveInfo(c);
		if(ret){
			setAttr("message","新增成功");
		}else{
			setAttr("message","新增失败");
		}
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = getParaToInt("id");
			int ret = service.updateFlg(id, 0);
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
