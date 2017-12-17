package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.input.Tailer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.sun.java.swing.plaf.motif.resources.motif;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.PayRecord;
import my.core.model.ReturnData;
import my.core.model.SaleOrder;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.TeaPrice;
import my.core.model.TeapriceLog;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.MemberVO;
import my.core.vo.WareHouseVO;
import my.pvcloud.model.TeaModel;
import my.pvcloud.model.TeaPriceModel;
import my.pvcloud.service.TeaService;
import my.pvcloud.service.WareHouseService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageCompressZipUtil;
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
		removeSessionAttr("newStatus");
		Page<Tea> list = service.queryByPage(page, size);
		ArrayList<TeaModel> models = new ArrayList<>();
		TeaModel model = null;
		for(Tea tea : list.getList()){
			model = new TeaModel();
			model.setKeyCode(tea.getStr("key_code"));
			model.setId(tea.getInt("id"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			model.setName(tea.getStr("tea_title"));
			if(wtm != null){
				BigDecimal st = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("stock")));
				BigDecimal se = new BigDecimal(StringUtil.toStringDefaultZero(tea.getInt("size")));
				try {
					model.setStock(StringUtil.toString(st.divide(se)));
				} catch (Exception e) {
					model.setStock("0");
				}
				BigDecimal originStock = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock")));
				try {
					model.setSaleItems(StringUtil.toString(originStock.divide(se)));
				} catch (Exception e) {
					model.setSaleItems("0");
				}
				model.setSyPiece(StringUtil.toString(wtm.getInt("stock"))+"片");
				model.setOriginStock(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock"))+"片");
				WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
				if(wtmItem != null){
					String size = "";
					CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
					if(sizeType != null){
						size = "元/"+sizeType.getStr("name");
					}
					model.setPrice(StringUtil.toString(wtmItem.getBigDecimal("price"))+size);
					CodeMst s = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
					if(s != null){
						model.setSaleStatus(s.getStr("name"));
					}
				}
			}
			
			//参考价
			TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(model.getId());
			if(teaPrice != null){
				model.setReferencePrice(StringUtil.toString(teaPrice.getBigDecimal("reference_price"))+"元/片");
			}
			model.setUrl(tea.getStr("desc_url"));
			model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			model.setFlg(tea.getInt("flg"));
			model.setStatusCd(tea.getStr("status"));
			CodeMst teaStatus = CodeMst.dao.queryCodestByCode(tea.getStr("status"));
			if(teaStatus != null){
				model.setStatus(teaStatus.getStr("name"));
			}
			//增加显示字段
			model.setBrand(tea.getStr("brand"));
			model.setProductPlace(tea.getStr("product_place"));
			model.setSize(StringUtil.toString(tea.getInt("weight"))+"克/片，"+StringUtil.toString(tea.getInt("size"))+"片/件");
			model.setAmount(StringUtil.toString(tea.getInt("total_output")));
			
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
		String snewStatus=getSessionAttr("newStatus");
		this.setSessionAttr("newStatus",snewStatus);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Tea> list = service.queryByPageParams(page, size,stitle,snewStatus);
		ArrayList<TeaModel> models = new ArrayList<>();
		TeaModel model = null;
		for(Tea tea : list.getList()){
			model = new TeaModel();
			model.setKeyCode(tea.getStr("key_code"));
			model.setId(tea.getInt("id"));
			model.setName(tea.getStr("tea_title"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			if(wtm != null){
				BigDecimal st = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("stock")));
				BigDecimal se = new BigDecimal(StringUtil.toStringDefaultZero(tea.getInt("size")));
				try {
					model.setStock(StringUtil.toString(st.divide(se)));
				} catch (Exception e) {
					model.setStock("0");
				}
				model.setSyPiece(StringUtil.toString(wtm.getInt("stock"))+"片");
				BigDecimal originStock = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock")));
				try {
					model.setSaleItems(StringUtil.toString(originStock.divide(se)));
				} catch (Exception e) {
					model.setSaleItems("0");
				}
				model.setOriginStock(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock"))+"片");
				WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
				if(wtmItem != null){
					String size = "";
					CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
					if(sizeType != null){
						size = "元/"+sizeType.getStr("name");
					}
					model.setPrice(StringUtil.toString(wtmItem.getBigDecimal("price"))+size);
					CodeMst s = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
					if(s != null){
						model.setSaleStatus(s.getStr("name"));
					}
				}
			}
			
			//参考价
			TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(model.getId());
			if(teaPrice != null){
				model.setReferencePrice(StringUtil.toString(teaPrice.getBigDecimal("reference_price"))+"元/片");
			}
			model.setUrl(tea.getStr("desc_url"));
			model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}
			model.setFlg(tea.getInt("flg"));
			model.setStatusCd(tea.getStr("status"));
			CodeMst teaStatus = CodeMst.dao.queryCodestByCode(tea.getStr("status"));
			if(teaStatus != null){
				model.setStatus(teaStatus.getStr("name"));
			}
			//增加显示字段
			model.setBrand(tea.getStr("brand"));
			model.setProductPlace(tea.getStr("product_place"));
			model.setSize(StringUtil.toString(tea.getInt("weight"))+"克/片，"+StringUtil.toString(tea.getInt("size"))+"片/件");
			model.setAmount(StringUtil.toString(tea.getInt("total_output")));
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
		
		String newStatus = getSessionAttr("newStatus");
		String snewStatus = getPara("newStatus");
		newStatus = snewStatus;
		this.setSessionAttr("newStatus",snewStatus);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Tea> list = service.queryByPageParams(page, size,title,newStatus);
			ArrayList<TeaModel> models = new ArrayList<>();
			TeaModel model = null;
			for(Tea tea : list.getList()){
				model = new TeaModel();
				model.setId(tea.getInt("id"));
				model.setKeyCode(tea.getStr("key_code"));
				model.setName(tea.getStr("tea_title"));
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
				if(wtm != null){
					BigDecimal st = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("stock")));
					BigDecimal se = new BigDecimal(StringUtil.toStringDefaultZero(tea.getInt("size")));
					model.setSyPiece(StringUtil.toString(wtm.getInt("stock"))+"片");
					try {
						model.setStock(StringUtil.toString(st.divide(se)));
					} catch (Exception e) {
						model.setStock("0");
					}
					BigDecimal originStock = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock")));
					try {
						model.setSaleItems(StringUtil.toString(originStock.divide(se)));
					} catch (Exception e) {
						model.setSaleItems("0");
					}
					model.setOriginStock(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock"))+"片");
					WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
					if(wtmItem != null){
						String size = "";
						CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
						if(sizeType != null){
							size = "元/"+sizeType.getStr("name");
						}
						model.setPrice(StringUtil.toString(wtmItem.getBigDecimal("price"))+size);
						CodeMst s = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
						if(s != null){
							model.setSaleStatus(s.getStr("name"));
						}
					}
				}
				//参考价
				TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(model.getId());
				if(teaPrice != null){
					model.setReferencePrice(StringUtil.toString(teaPrice.getBigDecimal("reference_price"))+"元/片");
				}
				model.setUrl(tea.getStr("desc_url"));
				model.setCreateTime(StringUtil.toString(tea.getTimestamp("create_time")));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					model.setType(type.getStr("name"));
				}
				model.setFlg(tea.getInt("flg"));
				model.setStatusCd(tea.getStr("status"));
				CodeMst teaStatus = CodeMst.dao.queryCodestByCode(tea.getStr("status"));
				if(teaStatus != null){
					model.setStatus(teaStatus.getStr("name"));
				}
				//增加显示字段
				model.setBrand(tea.getStr("brand"));
				model.setProductPlace(tea.getStr("product_place"));
				model.setSize(StringUtil.toString(tea.getInt("weight"))+"克/片，"+StringUtil.toString(tea.getInt("size"))+"片/件");
				model.setAmount(StringUtil.toString(tea.getInt("total_output")));
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
	
	/**
	 * 修改价格
	 */
	public void alertPrice(){
		String id = getPara("teaId");
		int teaId = 0;
		if(!("").equals(id) && id!=null){
			teaId = getParaToInt("teaId");
		}
		Tea teaInfo = service.queryById(teaId);
		setAttr("model", teaInfo);
		render("editteaprice.jsp");
	}
	
	//增加资讯初始化
	public void addTea(){
		//初始化所有仓库
		List<WareHouse> houses = houseService.queryAllHouse();
		setAttr("houses", houses);
		List<CodeMst> teaType = CodeMst.dao.queryCodestByPcode(Constants.TEA_TYPE_CD.TEA);
		setAttr("teaType", teaType);
		List<CodeMst> brandType = CodeMst.dao.queryCodestByPcode(Constants.BRAND_TYPE_CD.BRAND_TYPE);
		setAttr("brandType", brandType);
		List<CodeMst> place = CodeMst.dao.queryCodestByPcode(Constants.PRODUCT_PLACE.PRODUCT_PLACE_CD);
		setAttr("place", place);
		render("addTea.jsp");
	}
	
	//后台保存茶叶
	public void saveTea(){
		setAttr("closeFlg",1);
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
		    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
		    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
		    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
		    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		//生成html文件
		String htmlUrl="";
		try {
			String uuid = UUID.randomUUID().toString();
			htmlUrl = Constants.HOST.FILE+uuid;
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
        tea.set("key_code", StringUtil.getTeaKeyCode());
        int houseId = getParaToInt("houses");
       
		int teaId = Tea.dao.saveInfos(tea);
		if(teaId != 0){
			//增加仓库-茶叶-用户
			WarehouseTeaMember houseTea = new WarehouseTeaMember();
		    houseTea.set("warehouse_id", houseId);
		    houseTea.set("tea_id", teaId);
		    houseTea.set("origin_stock", StringUtil.toInteger(getPara("amount")));
		    houseTea.set("stock", StringUtil.toInteger(getPara("warehouse"))*StringUtil.toInteger(getPara("size2")));
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
		    	item.set("origin_stock", StringUtil.toInteger(getPara("warehouse")));
		    	item.set("cancle_quality", 0);
		    	int save = WarehouseTeaMemberItem.dao.saveItemInfo(item);
		    	if(save != 0){
		    		//卖茶记录
		    		SaleOrder order = new SaleOrder();
					order.set("warehouse_tea_member_id", retId);
					order.set("wtm_item_id", retId);
					order.set("quality", StringUtil.toInteger(getPara("warehouse")));
					order.set("price", price);
					order.set("size_type_cd", Constants.TEA_UNIT.ITEM);
					order.set("create_time", DateUtil.getNowTimestamp());
					order.set("update_time", DateUtil.getNowTimestamp());
					order.set("status", statusStr);
					order.set("order_no", StringUtil.getOrderNo());
					SaleOrder.dao.saveInfo(order);
					
		    		TeaPrice teaPrice = new TeaPrice();
		    		teaPrice.set("tea_id", teaId);
		    		teaPrice.set("reference_price", StringUtil.toBigDecimal(StringUtil.checkCode(getPara("referencePrice"))));
		    		teaPrice.set("from_price", StringUtil.toBigDecimal(StringUtil.checkCode(getPara("fromPrice"))));
		    		teaPrice.set("to_price", StringUtil.toBigDecimal(StringUtil.checkCode(getPara("toPrice"))));
		    		teaPrice.set("expire_time", Timestamp.valueOf(StringUtil.checkCode(getPara("expireDate"))+" 23:59:59"));
		    		teaPrice.set("update_time", DateUtil.getNowTimestamp());
		    		teaPrice.set("create_time", DateUtil.getNowTimestamp());
		    		boolean saveTeaPrice = TeaPrice.dao.saveInfo(teaPrice);
		    		if(saveTeaPrice){
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
		}else{
			setAttr("message","新增失败");
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "上架茶叶:"+title);
		index();
	}
	
	public void updateTea(){
		setAttr("closeFlg",1);
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
			    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
			    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
			    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
			    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			}
			//生成html文件
			try {
				String uuid = UUID.randomUUID().toString();
				contentUrl = Constants.HOST.FILE+uuid;
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
		    ImageCompressZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
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
			if(ret!=0){
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

	//更新茶叶状态
	public void updateStatus(){
		try{
			int teaId = getParaToInt("id");
			String status = StringUtil.checkCode(getPara("status"));
			int ret = service.updateStatus(teaId, status);
			if(ret!=0){
				//更新在售茶叶
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryPlatTeaInfo(teaId, (Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER);
				if(wtm != null){
					WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
					if (wtmItem != null) {
						String itemStatus = Constants.TEA_STATUS.STOP_SALE;
						if(StringUtil.equals(status, Constants.NEWTEA_STATUS.ON_SALE)){
							itemStatus = Constants.TEA_STATUS.ON_SALE;
						}
						int retData = WarehouseTeaMemberItem.dao.updateWtmItemStatus(wtmItem.getInt("id"), itemStatus);
						if(retData != 0){
							setAttr("message", "修改成功");
						}else{
							setAttr("message", "修改失败");
						}
					}else{
						setAttr("message", "修改失败");
					}
				}else{
					setAttr("message", "修改失败");
				}
			}else{
				setAttr("message", "修改失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
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
		String imgs = teaInfo.getStr("cover_img");
		List<String> list = new ArrayList<>();
		if(StringUtil.isNoneBlank(imgs)){
			String[] images = imgs.split(",");
			for(String str : images){
				list.add(str);
			}
		}
		TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(StringUtil.toInteger(getPara("id")));
		TeaPriceModel model = new TeaPriceModel();
		if(teaPrice != null){
			model.setFromPrice(teaPrice.getBigDecimal("from_price"));
			model.setToPrice(teaPrice.getBigDecimal("to_price"));
			model.setDate(DateUtil.formatTimestampForDate(teaPrice.getTimestamp("expire_time")));
			model.setReferencePrice(teaPrice.getBigDecimal("reference_price")==null?new BigDecimal("0"):teaPrice.getBigDecimal("reference_price"));
		}
		setAttr("teaPrice", model);
		setAttr("list", list);
		render("editTea.jsp");
	}
	
	public void addTeaPrice(){
		int teaId = StringUtil.toInteger(StringUtil.checkCode(getPara("id")));
		setAttr("teaId", teaId);
		render("addTeaPrice.jsp");
	}
	
	public void saveTeaPrice(){
		int teaId = StringUtil.toInteger(StringUtil.checkCode(getPara("teaId")));
		BigDecimal fromPrice = StringUtil.toBigDecimal(StringUtil.checkCode(getPara("fromPrice")));
		BigDecimal toPrice = StringUtil.toBigDecimal(StringUtil.checkCode(getPara("toPrice")));
		TeaPrice teaPrice = new TeaPrice();
		teaPrice.set("tea_id", teaId);
		teaPrice.set("from_price",fromPrice);
		teaPrice.set("to_price",toPrice);
		teaPrice.set("reference_price", StringUtil.toBigDecimal(StringUtil.checkCode(getPara("referencePrice"))));
		teaPrice.set("expire_time", Timestamp.valueOf(StringUtil.checkCode(getPara("expireDate"))+" 23:59:59"));
		teaPrice.set("update_time", DateUtil.getNowTimestamp());
		teaPrice.set("create_time", DateUtil.getNowTimestamp());
		teaPrice.set("mark", StringUtil.checkCode(getPara("mark")));
		boolean saveTeaPrice = TeaPrice.dao.saveInfo(teaPrice);
		if(saveTeaPrice){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "新增茶叶参考价,茶叶id:"+teaId);
			setAttr("message","新增成功");
		}else{
			setAttr("message","新增失败");
		}
		index();
	}
	
	public void updateTeaPrice(){
		try{
			int teaId = getParaToInt("teaId");
			Tea teas = Tea.dao.queryById(teaId);
			BigDecimal price = StringUtil.toBigDecimal(getPara("price"));
			int ret = Tea.dao.updatePrice(teaId, price);
			if(ret!=0){
				Tea tea = Tea.dao.queryById(teaId);
				//保存操作日志
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "修改茶叶价格:原价："+teas.getBigDecimal("tea_price")+",修改后的价格："+tea.getBigDecimal("tea_price"));
				//保存茶叶变更记录
				TeapriceLog teapriceLog = new TeapriceLog();
				teapriceLog.set("tea_id", teaId);
				teapriceLog.set("price", teas.getBigDecimal("tea_price"));
				teapriceLog.set("changed_price", price);
				teapriceLog.set("mark", "修改茶叶价格，原价："+teas.getBigDecimal("tea_price")+"，现价："+price);
				teapriceLog.set("create_time", DateUtil.getNowTimestamp());
				teapriceLog.set("update_time", DateUtil.getNowTimestamp());
				TeapriceLog.dao.saveInfo(teapriceLog);
				setAttr("message", "修改成功");
			}else{
				setAttr("message", "修改失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	public void exportData(){
		 String path = "F:\\upload\\茶叶数据.xls";
		 try {  
			
		 FileOutputStream os = new FileOutputStream(new File(path));  
		// 创建一个workbook 对应一个excel应用文件  
	        XSSFWorkbook workBook = new XSSFWorkbook();  
	        // 在workbook中添加一个sheet,对应Excel文件中的sheet  
	        XSSFSheet sheet = workBook.createSheet("茶叶数据");  
	       ExportUtil exportUtil = new ExportUtil(workBook, sheet);
	       XSSFCellStyle headStyle = exportUtil.getHeadStyle();  
	        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle(); 
	        
	        XSSFRow headRow = sheet.createRow(0);  
	        XSSFCell cell = null;  
	        String[] titles = new String[]{"茶名称","茶编码","茶类型","茶价格","参考价","茶叶发行状态","品牌","产地","规格","发行件数","剩余件数","发行总量","剩余库存","是否删除","注册时间"};
	        for (int i = 0; i < titles.length; i++){  
	            cell = headRow.createCell(i);  
	            cell.setCellStyle(headStyle);  
	            cell.setCellValue(titles[i]);  
	        }  
	        
	        String newStatus = getPara("newStatus");
			String title = getPara("title");
		    List<Tea> list = Tea.dao.exportData(title, newStatus);
		    if (list != null && list.size() > 0){  
		    	for (int j = 0; j < list.size(); j++){  
		    		XSSFRow bodyRow = sheet.createRow(j + 1);
		    		
		    		Tea tea = list.get(j);
		    		//茶名称
		    		cell = bodyRow.createCell(0);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(tea.getStr("tea_title"));
		            
		            //茶编码	
		            cell = bodyRow.createCell(1);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(tea.getStr("key_code"));
		            
		            //茶类型
		            cell = bodyRow.createCell(2);  
		            cell.setCellStyle(bodyStyle); 
		            CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
    				if(type != null){
    					cell.setCellValue(type.getStr("name"));
    				}else{
    					cell.setCellValue("");
    				}
		            
    				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
    				if(wtm != null){
    					BigDecimal st = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("stock")));
    					BigDecimal se = new BigDecimal(StringUtil.toStringDefaultZero(tea.getInt("size")));
    					
    					cell = bodyRow.createCell(12);  
	    		        cell.setCellStyle(bodyStyle);  
	    		        cell.setCellValue(StringUtil.toString(wtm.getInt("stock"))+"片");
    					
    		            
    					try {
    						//剩余件数
    						cell = bodyRow.createCell(10);  
    	    		        cell.setCellStyle(bodyStyle);  
    	    		        cell.setCellValue(StringUtil.toString(st.divide(se)));
    					} catch (Exception e) {
    						cell = bodyRow.createCell(10);  
    	    		        cell.setCellStyle(bodyStyle);  
    	    		        cell.setCellValue("0");
    					}
    					BigDecimal originStock = new BigDecimal(StringUtil.toStringDefaultZero(wtm.getInt("origin_stock")));
    					try {
    						//发行件数
    						cell = bodyRow.createCell(9);  
    	    		        cell.setCellStyle(bodyStyle);  
     	    		        cell.setCellValue(StringUtil.toString(originStock.divide(se))+"件");
    					} catch (Exception e) {
    						cell = bodyRow.createCell(9);  
    	    		        cell.setCellStyle(bodyStyle);  
     	    		        cell.setCellValue("0件");
    					}
    					WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
    					if(wtmItem != null){
    						String size = "";
    						CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
    						if(sizeType != null){
    							size = "元/"+sizeType.getStr("name");
    						}
    						 //茶价格
    			            cell = bodyRow.createCell(3);  
    			            cell.setCellStyle(bodyStyle);  
    			            cell.setCellValue(StringUtil.toString(wtmItem.getBigDecimal("price"))+size);
    			            
    						CodeMst s = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
    						if(s != null){
    							//茶叶发行状态
    				            cell = bodyRow.createCell(5);  
    				            cell.setCellStyle(bodyStyle);
    				            cell.setCellValue(s.getStr("name"));
    						}else{
    							cell = bodyRow.createCell(5);  
    				            cell.setCellStyle(bodyStyle);
    				            cell.setCellValue("");
    						}
    					}
    				}
    				
		           
		            
		            //参考价
		            cell = bodyRow.createCell(4);  
		            cell.setCellStyle(bodyStyle);  
		            TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(tea.getInt("id"));
    				if(teaPrice != null){
    					cell.setCellValue(StringUtil.toString(teaPrice.getBigDecimal("reference_price"))+"元/片");
    				}else{
    					cell.setCellValue("");
    				}
		            
		             
		            //品牌
		            cell = bodyRow.createCell(6);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(tea.getStr("brand"));
		            
		            //产地	
		            cell = bodyRow.createCell(7);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(tea.getStr("product_place"));
		            
		            //规格	
		            cell = bodyRow.createCell(8);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(tea.getInt("weight"))+"克/片，"+StringUtil.toString(tea.getInt("size"))+"片/件");
		            
					//发行总量
		            cell = bodyRow.createCell(11);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(tea.getInt("total_output"))+"片");
						
					
					//是否删除
		            cell = bodyRow.createCell(13);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(tea.getInt("flg")==1?"否":"是");
					
		            //注册时间
		            cell = bodyRow.createCell(14);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(tea.getTimestamp("create_time")));
	            }
	        }
	        workBook.write(os);  
	       }catch(Exception e){  
	        e.printStackTrace();  
	       }  
	      //判断路径是否存在  
	     if(new File(path).isFile()){  
	        renderFile(new File(path));  
	      }else{  
	        renderNull();  
	      }  
	}
}
