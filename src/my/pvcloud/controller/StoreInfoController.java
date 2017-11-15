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
import java.sql.Timestamp;
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
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.model.ReturnData;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.model.Tea;
import my.core.model.TeaPrice;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.MemberVO;
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
		Page<Member> list = service.queryStoreMemberList(page, size,storeId);
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
	
	public void queryMemberListByPage(){
		int storeId=(Integer)getSessionAttr("queryStoreId");
		this.setSessionAttr("storeId",storeId);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Member> list = service.queryStoreMemberList(page, size,storeId);
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
			String status = StringUtil.checkCode(getPara("flg"));
			int ret = service.updateFlg(id, status);
			if(ret==0){
				Store store = Store.dao.queryById(id);
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新门店状态:"+store.getStr("store_name"));
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
		Store store = Store.dao.queryById(storeId);
		int memberId = store == null ? 0 : store.getInt("member_id");
		QRCodeUtil.QRCodeCreate(StringUtil.toString(memberId), "//home//ewcode//qrcode.jpg", 15, "//home//ewcode//icon.png");
		//QRCodeUtil.QRCodeCreate(StringUtil.toString(memberId), "D://upload//ewcode//qrcode.jpg", 15, "D://upload//ewcode//icon.png");
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
	    String fullFileName = "//home//ewcode//qrcode.jpg"; 
	    //String fullFileName = "D://upload//ewcode//qrcode.jpg"; 
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
		}
		if(ret1 && ret2 && ret3 && ret4 && ret5 && ret6){
			setAttr("message", "更新成功");
		}else{
			setAttr("message", "更新失败");
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "修改门店图片，门店id："+storeId);
		index();
	}
}
