package my.pvcloud.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.vo.MemberVO;
import my.pvcloud.model.CityModel;
import my.pvcloud.model.StoreModel;
import my.pvcloud.service.Service;
import my.pvcloud.service.StoreService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.QRCodeUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/storeInfo", path = "/pvcloud")
public class StoreInfoController extends Controller {

	StoreService service = Enhancer.enhance(StoreService.class);
	Service commonService = Enhancer.enhance(Service.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		removeSessionAttr("status");
		removeSessionAttr("mobile");
		Page<Store> list = service.queryByPage(page, size);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setKeyCode(store.getStr("key_code"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			model.setStatusCd(store.getStr("status"));
			model.setAddress(store.getStr("store_address"));
			CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
			if(statusCodeMst != null){
				model.setStatus(statusCodeMst.getStr("name"));
			}
			Member member2 = Member.dao.queryById(store.getInt("member_id"));
			if(member2 != null){
				model.setMobile(member2.getStr("mobile"));
				model.setUserName(member2.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("store.jsp");
	}
	
	//查询门店会员
	public void queryMemberList(){
		int storeId = StringUtil.toInteger(getPara("storeId"));
		int flg = StringUtil.toInteger(getPara("flg"));
		if(flg != 1){
			storeId = (Integer)getSessionAttr("queryStoreId");
		}else{
			removeSessionAttr("queryStoreId");
			setSessionAttr("queryStoreId", storeId);
		}
		Page<Member> list = service.queryStoreMemberList(page, size,storeId,"","");
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setKeyCode(member.getStr("id_code"));
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("nick_name"));
			model.setUserName(member.getStr("name"));
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			//查询用户已提现金额和提现中的金额
			BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
			model.setApplingMoneys(StringUtil.toString(applying));
			BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
			model.setApplyedMoneys(StringUtil.toString(applySuccess));
			BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
			model.setRechargeMoneys(StringUtil.toString(paySuccess));
			
			
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
			Store store = Store.dao.queryById(member.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}else{
				model.setStore("");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("storemember.jsp");
	}
	
	//搜索查询
	public void queryMemberByConditionByPage(){
		
		String cmobile = getSessionAttr("cmobile");
		String cname = getSessionAttr("cname");
		
		String mobile = getPara("cmobile");
		cmobile = mobile;
		this.setSessionAttr("cmobile",cmobile);
		
		String name = getPara("cname");
		cname = name;
		this.setSessionAttr("cname",cname);
		
		int storeId=(Integer)getSessionAttr("queryStoreId");
		this.setSessionAttr("storeId",storeId);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Member> list = service.queryStoreMemberList(page, size,storeId,name,mobile);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setKeyCode(member.getStr("id_code"));
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("nick_name"));
			model.setUserName(member.getStr("name"));
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			//查询用户已提现金额和提现中的金额
			BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
			model.setApplingMoneys(StringUtil.toString(applying));
			BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
			model.setApplyedMoneys(StringUtil.toString(applySuccess));
			BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
			model.setRechargeMoneys(StringUtil.toString(paySuccess));
			
			
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
			Store store = Store.dao.queryById(member.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}else{
				model.setStore("");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("storemember.jsp");
	}
	
	//门店会员分页
	public void queryMemberListByPage(){
		
		String cmobile=getSessionAttr("cmobile");
		this.setSessionAttr("cmobile",cmobile);
		
		String cname=getSessionAttr("cname");
		this.setSessionAttr("cname",cname);
		
		int storeId=(Integer)getSessionAttr("queryStoreId");
		this.setSessionAttr("storeId",storeId);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Member> list = service.queryStoreMemberList(page, size,storeId,cname,cmobile);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setKeyCode(member.getStr("id_code"));
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("nick_name"));
			model.setUserName(member.getStr("name"));
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			//查询用户已提现金额和提现中的金额
			BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
			model.setApplingMoneys(StringUtil.toString(applying));
			BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
			model.setApplyedMoneys(StringUtil.toString(applySuccess));
			BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
			model.setRechargeMoneys(StringUtil.toString(paySuccess));
			
			
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
			Store store = Store.dao.queryById(member.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}else{
				model.setStore("");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("storemember.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		String s=getSessionAttr("status");
		this.setSessionAttr("status",s);
		String m=getSessionAttr("mobile");
		this.setSessionAttr("mobile",m);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Member member = Member.dao.queryMember(m);
        int memerId = member == null ? 0 : member.getInt("id");
        Page<Store> list = service.queryByPageParams(page, size,title,s,memerId);
		ArrayList<StoreModel> models = new ArrayList<>();
		StoreModel model = null;
		for(Store store : list.getList()){
			model = new StoreModel();
			model.setId(store.getInt("id"));
			model.setKeyCode(store.getStr("key_code"));
			model.setTitle(store.getStr("store_name"));
			model.setFlg(store.getInt("flg"));
			model.setStatusCd(store.getStr("status"));
			model.setAddress(store.getStr("store_address"));
			CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
			if(statusCodeMst != null){
				model.setStatus(statusCodeMst.getStr("name"));
			}
			Member member2 = Member.dao.queryById(store.getInt("member_id"));
			if(member2 != null){
				model.setMobile(member2.getStr("mobile"));
				model.setUserName(member2.getStr("name"));
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
		this.setSessionAttr("title",ptitle);
		String s = getPara("status");
		
		
		String m = getPara("mobile");
		
		
		this.setSessionAttr("status",s);
		this.setSessionAttr("mobile",m);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Member member = Member.dao.queryMember(m);
	        int memerId = member == null ? 0 : member.getInt("id");
	        Page<Store> list = service.queryByPageParams(page, size,title,s,memerId);
			ArrayList<StoreModel> models = new ArrayList<>();
			StoreModel model = null;
			for(Store store : list.getList()){
				model = new StoreModel();
				model.setKeyCode(store.getStr("key_code"));
				model.setId(store.getInt("id"));
				model.setTitle(store.getStr("store_name"));
				model.setFlg(store.getInt("flg"));
				model.setStatusCd(store.getStr("status"));
				model.setAddress(store.getStr("store_address"));
				CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(model.getStatusCd());
				if(statusCodeMst != null){
					model.setStatus(statusCodeMst.getStr("name"));
				}
				Member member2 = Member.dao.queryById(store.getInt("member_id"));
				if(member2 != null){
					model.setMobile(member2.getStr("mobile"));
					model.setUserName(member2.getStr("name"));
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
		setAttr("imgSize", 6-imgs.size());
		setAttr("model", store);
		setAttr("imgs", url);
		render("storeInfoAlter.jsp");
	}
	
	public void edit(){
		int id = StringUtil.toInteger(getPara("id"));
		Store store = service.queryById(id);
		List<StoreImage> imgs = StoreImage.dao.queryStoreImages(id);
		List<String> url = new ArrayList<>();
		for(StoreImage imgImage : imgs){
			url.add(imgImage.getStr("img"));
		}
		setAttr("imgSize", imgs.size());
		setAttr("model", store);
		setAttr("imgs", url);
		render("editStore.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			String status = StringUtil.checkCode(getPara("flg"));
			int ret = service.updateFlg(id, status);
			if(ret==0){
				
				Store store = Store.dao.queryById(id);
				
				//消息
				String stStr = "";
				if(StringUtil.equals(status, "110003")){
					//更新用户为经销商
					if(store != null){
						int memberId = store.getInt("member_id");
						int rets = Member.dao.updateRole(memberId, Constants.ROLE_CD.BUSINESS_USER);
						if(rets != 0){
							setAttr("message", "操作成功");
						}else{
							setAttr("message", "操作失败");
						}
					}
					stStr = "您的门店已审核通过";
				}
				if(StringUtil.equals(status, "110004")){
					//更新用户为普通用户
					if(store != null){
						int memberId = store.getInt("member_id");
						int rets = Member.dao.updateRole(memberId, Constants.ROLE_CD.NORMAL_USER);
						if(rets != 0){
							setAttr("message", "操作成功");
						}else{
							setAttr("message", "操作失败");
						}
					}
					stStr = "您的门店审核未通过，请重新提交";
				}
			
				int userId = 0;
				if(store != null){
					userId = store.getInt("member_id") == null ? 0 : store.getInt("member_id");
				}
				
				/*Message message = new Message();
				message.set("message_type_cd", Constants.MESSAGE_TYPE.STORE_REVIEW_MSG);
				message.set("message",stStr);
				message.set("title","门店审核");
				message.set("params", "{id:"+id+"}");
				message.set("create_time", DateUtil.getNowTimestamp());
				message.set("update_time", DateUtil.getNowTimestamp());
				message.set("user_id", userId);
				boolean messageSave = Message.dao.saveInfo(message);*/
				
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新门店状态:"+store.getStr("store_name"));
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
		Store store = Store.dao.queryById(storeId);
		int memberId = store == null ? 0 : store.getInt("member_id");
		//QRCodeUtil.QRCodeCreate(StringUtil.toString(memberId), "//home//ewcode//qrcode.jpg", 8, "//home//ewcode//icon.png");
		QRCodeUtil.QRCodeCreate("http://www.yibuwangluo.cn/zznj/h5/share.jsp?businessId="+memberId, "D://upload//ewcode//qrcode.jpg", 8, "D://upload//ewcode//icon.png");
        HttpServletResponse response = getResponse();
		response.setContentType("application/binary");
	    //设置Content-Disposition
		Member member = Member.dao.queryById(memberId);
		String idCode = "";
		if((member != null)&&(StringUtil.isNoneBlank(member.getStr("id_code")))){
			idCode = member.getStr("id_code")+"_";
		}
		String storeName = store.getStr("store_name")==null ? idCode+"未命名" : idCode+store.getStr("store_name");
		String name = new String(storeName.getBytes(), "ISO-8859-1");
	    response.setHeader("Content-Disposition", "attachment;filename="+name+".jpeg");  
	    //读取目标文件，通过response将目标文件写到客户端  
	    //获取目标文件的绝对路径  
	    //String fullFileName = "//home//ewcode//qrcode.jpg"; 
	    String fullFileName = "D://upload//ewcode//qrcode.jpg"; 
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
	
	//更新门店图片
	public void updateStoreImages(){
		UploadFile uploadFile1 = getFile("img1");
		UploadFile uploadFile2 = getFile("img2");
		UploadFile uploadFile3 = getFile("img3");
		UploadFile uploadFile4 = getFile("img4");
		UploadFile uploadFile5 = getFile("img5");
		UploadFile uploadFile6 = getFile("img6");
		int storeId = StringUtil.toInteger(StringUtil.checkCode(getPara("storeId")));
		FileService fs=new FileService();
		
		String logo1 = "";
		String logo2 = "";
		String logo3 = "";
		String logo4 = "";
		String logo5 = "";
		String logo6 = "";
		boolean ret1 = true;
		boolean ret2 = true;
		boolean ret3 = true;
		boolean ret4 = true;
		boolean ret5 = true;
		boolean ret6 = true;
		
		String storeName = StringUtil.checkCode(getPara("storeName"));
		String cityDistrict = StringUtil.checkCode(getPara("cityDistrict"));
		String address = StringUtil.checkCode(getPara("address"));
		Float longtitude = StringUtil.toFloat(StringUtil.checkCode(getPara("longtitude")));
		Float latitude = StringUtil.toFloat(StringUtil.checkCode(getPara("latitude")));
		String bussineeTea = StringUtil.checkCode(getPara("bussineeTea"));
		String mobile = StringUtil.checkCode(getPara("mobile"));
		String fromTime = StringUtil.checkCode(getPara("fromTime"));
		String toTime = StringUtil.checkCode(getPara("toTime"));
		String storeDetail = StringUtil.checkCode(getPara("storeDetail"));
		String status = StringUtil.checkCode(getPara("status"));
		
		Store store2 = new Store();
		store2.set("id",storeId);
		store2.set("store_address", address);
		store2.set("longitude", longtitude);
		store2.set("latitude", latitude);
		store2.set("store_name", storeName);
		store2.set("link_phone", mobile);
		store2.set("business_tea", bussineeTea);
		store2.set("business_fromtime", fromTime);
		store2.set("business_totime", toTime);
		store2.set("store_desc", storeDetail);
		store2.set("update_time", DateUtil.getNowTimestamp());
		store2.set("status", status);
		store2.set("city_district", cityDistrict);

		boolean updateFlg = Store.dao.updateInfo(store2);
		//上传文件
		if(uploadFile1 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile1.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile1.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo1 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret1 = StoreImage.dao.updateInfo(logo1, storeId, 1);
		    if(!ret1){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo1);
				storeImage.set("flg", 1);
				storeImage.set("seq", 1);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret1 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(uploadFile2 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile2.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile2.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo2 =  Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret2 = StoreImage.dao.updateInfo(logo2, storeId, 2);
		    if(!ret2){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo2);
				storeImage.set("flg", 1);
				storeImage.set("seq", 2);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret2 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(uploadFile3 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile3.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile3.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo3 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret3 = StoreImage.dao.updateInfo(logo3, storeId, 3);
		    if(!ret3){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo3);
				storeImage.set("flg", 1);
				storeImage.set("seq", 3);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret3 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(uploadFile4 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile4.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile4.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo4 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret4 = StoreImage.dao.updateInfo(logo4, storeId, 4);
		    if(!ret4){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo4);
				storeImage.set("flg", 1);
				storeImage.set("seq", 4);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret4 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(uploadFile5 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile5.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile5.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo5 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret5 = StoreImage.dao.updateInfo(logo5, storeId, 5);
		    if(!ret5){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo5);
				storeImage.set("flg", 1);
				storeImage.set("seq", 5);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret5 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(uploadFile6 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile6.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile6.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo6 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    ret6 = StoreImage.dao.updateInfo(logo6, storeId, 6);
		    if(!ret6){
		    	StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", storeId);
				storeImage.set("img", logo6);
				storeImage.set("flg", 1);
				storeImage.set("seq", 6);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret6 = StoreImage.dao.saveInfo(storeImage);
		    }
		}
		if(updateFlg && ret1 && ret2 && ret3 && ret4 && ret5 && ret6){
			setAttr("message", "更新成功");
		}else{
			setAttr("message", "更新失败");
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "修改门店信息，门店id："+storeId);
		index();
	}
	
	//新增门店初始化
	public void addStoreInit(){
		int id = StringUtil.toInteger(getPara("id"));
		Store store = service.queryById(id);
		if(store != null){
			setAttr("message", "对不起，该用户已经有开通的门店，暂时不能开第二家");
		}
		//获取省市区的数据
		List<CityModel> provinces = commonService.queryCity(0, 0);
		setAttr("provinces", provinces);
		setAttr("memberId", id);
		render("addStore.jsp");
	}
	
	//新增门店
	public void saveStore(){
		
		UploadFile uploadFile = getFile("img1");
		UploadFile uploadFile1 = getFile("img2");
		UploadFile uploadFile2 = getFile("img3");
		UploadFile uploadFile3 = getFile("img4");
		UploadFile uploadFile4 = getFile("img5");
		UploadFile uploadFile5 = getFile("img6");
		
		//判断是否开店
		int memberId = StringUtil.toInteger(getPara("memberId"));
		Store store = Store.dao.queryMemberStore(memberId);
		if(store != null){
			setAttr("message", "对不起，该用户已经有开通的门店，暂时不能开第二家");
			index();
		}else{
			String storeName = StringUtil.checkCode(getPara("storeName"));
			String cityDistrict = StringUtil.checkCode(getPara("cityDistrict"));
			String address = StringUtil.checkCode(getPara("address"));
			Float longtitude = StringUtil.toFloat(StringUtil.checkCode(getPara("longtitude")));
			Float latitude = StringUtil.toFloat(StringUtil.checkCode(getPara("latitude")));
			String bussineeTea = StringUtil.checkCode(getPara("bussineeTea"));
			String mobile = StringUtil.checkCode(getPara("mobile"));
			String fromTime = StringUtil.checkCode(getPara("fromTime"));
			String toTime = StringUtil.checkCode(getPara("toTime"));
			String storeDetail = StringUtil.checkCode(getPara("storeDetail"));
			String status = StringUtil.checkCode(getPara("status"));
			
			Store store2 = new Store();
			store2.set("province_id", 0);
			store2.set("city_id", 0);
			store2.set("district_id", 0);
			
			store2.set("store_address", address);
			store2.set("longitude", longtitude);
			store2.set("latitude", latitude);
			store2.set("store_name", storeName);
			store2.set("link_phone", mobile);
			store2.set("business_tea", bussineeTea);
			store2.set("business_fromtime", fromTime);
			store2.set("business_totime", toTime);
			store2.set("store_desc", storeDetail);
			store2.set("member_id", memberId);
			store2.set("create_time", DateUtil.getNowTimestamp());
			store2.set("update_time", DateUtil.getNowTimestamp());
			store2.set("status", status);
			store2.set("city_district", cityDistrict);
			
			Store store3 = Store.dao.queryNewCode();
			String code = "";
			if(store3!=null){
				code = store3.getStr("key_code");
			}
			store2.set("key_code", StringUtil.getStoreKeyCode(code));
			
			int id = Store.dao.saveInfos(store2);
			FileService fs=new FileService();
			
			String logo1 = "";
			String logo2 = "";
			String logo3 = "";
			String logo4 = "";
			String logo5 = "";
			String logo6 = "";
			boolean ret1 = true;
			boolean ret2 = true;
			boolean ret3 = true;
			boolean ret4 = true;
			boolean ret5 = true;
			boolean ret6 = true;
			
			//上传文件
			//第一张图
			String uuid1 = UUID.randomUUID().toString();
			if(uploadFile != null){
				String fileName = uploadFile.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid1+"."+names[1]);
			    logo1 = Constants.HOST.STORE+uuid1+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage = new StoreImage();
				storeImage.set("store_id", id);
				storeImage.set("img", logo1);
				storeImage.set("flg", 1);
				storeImage.set("seq", 1);
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret1 = StoreImage.dao.saveInfo(storeImage);
			}
			//第二张图
			String uuid2 = UUID.randomUUID().toString();
			if(uploadFile1 != null){
				String fileName = uploadFile1.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile1.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid2+"."+names[1]);
			    logo2 = Constants.HOST.STORE+uuid2+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage1 = new StoreImage();
				storeImage1.set("store_id", id);
				storeImage1.set("img", logo2);
				storeImage1.set("flg", 1);
				storeImage1.set("seq", 2);
				storeImage1.set("create_time", DateUtil.getNowTimestamp());
				storeImage1.set("update_time", DateUtil.getNowTimestamp());
				ret2 = StoreImage.dao.saveInfo(storeImage1);
			}
			//第三张图
			String uuid3 = UUID.randomUUID().toString();
			if(uploadFile2 != null){
				String fileName = uploadFile2.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile2.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid3+"."+names[1]);
			    logo3 = Constants.HOST.STORE+uuid3+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage2 = new StoreImage();
				storeImage2.set("store_id", id);
				storeImage2.set("img", logo3);
				storeImage2.set("flg", 1);
				storeImage2.set("seq", 3);
				storeImage2.set("create_time", DateUtil.getNowTimestamp());
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret3 = StoreImage.dao.saveInfo(storeImage2);
			}
			//第四张图
			String uuid4 = UUID.randomUUID().toString();
			if(uploadFile3 != null){
				String fileName = uploadFile3.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile3.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid4+"."+names[1]);
			    logo4 = Constants.HOST.STORE+uuid4+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage2 = new StoreImage();
				storeImage2.set("store_id", id);
				storeImage2.set("img", logo4);
				storeImage2.set("flg", 1);
				storeImage2.set("seq", 4);
				storeImage2.set("create_time", DateUtil.getNowTimestamp());
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret4 = StoreImage.dao.saveInfo(storeImage2);
			}
			//第五张图
			String uuid5 = UUID.randomUUID().toString();
			if(uploadFile4 != null){
				String fileName = uploadFile4.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile4.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid5+"."+names[1]);
			    logo5 = Constants.HOST.STORE+uuid5+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage2 = new StoreImage();
				storeImage2.set("store_id", id);
				storeImage2.set("img", logo5);
				storeImage2.set("flg", 1);
				storeImage2.set("seq", 5);
				storeImage2.set("create_time", DateUtil.getNowTimestamp());
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret5 = StoreImage.dao.saveInfo(storeImage2);
			}
			//第六张图
			String uuid6 = UUID.randomUUID().toString();
			if(uploadFile5 != null){
				String fileName = uploadFile5.getOriginalFileName();
				String[] names = fileName.split("\\.");
			    File file=uploadFile5.getFile();
			    File t=new File(Constants.FILE_HOST.STORE+uuid6+"."+names[1]);
			    logo6 = Constants.HOST.STORE+uuid6+"."+names[1];
			    try{
			        t.createNewFile();
			    }catch(IOException e){
			        e.printStackTrace();
			    }
			    
			    fs.fileChannelCopy(file, t);
			    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
			    file.delete();
			    
			    StoreImage storeImage2 = new StoreImage();
				storeImage2.set("store_id", id);
				storeImage2.set("img", logo6);
				storeImage2.set("flg", 1);
				storeImage2.set("seq", 6);
				storeImage2.set("create_time", DateUtil.getNowTimestamp());
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret6 = StoreImage.dao.saveInfo(storeImage2);
			}
			if(ret1 && ret2 && ret3 && ret4 && ret5 && ret6){
				setAttr("message", "新增成功");
			}else{
				setAttr("message", "新增失败");
			}
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "新开门店，门店id："+id);
			index();
		}
	}
}
