package my.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.el.ArrayELResolver;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;
import org.huadalink.route.ControllerBind;
import org.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.app.service.LoginService;
import my.app.service.RestService;
import my.core.constants.Constants;
import my.core.interceptor.RequestInterceptor;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.model.User;
import my.core.vo.StoreDetailVO;
import my.pvcloud.dto.IndexDTO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.MD5Util;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.VertifyUtil;

@ControllerBind(key = "/rest", path = "/rest")
public class RestfulController extends Controller{

	LoginService service = Enhancer.enhance(LoginService.class);
    RestService restService = Enhancer.enhance(RestService.class);
    
    @Before(RequestInterceptor.class)
    public void login(){
    	ReturnData data = new ReturnData();
    	data.setCode(Constants.STATUS_CODE.SUCCESS);
    	data.setMessage("查询成功");
    	List<String> d = new ArrayList<String>();
    	d.add("1");
    	d.add("2");
    	d.add("3");
    	data.setData(d);
		renderJson(data);
	}
	
	//获取验证码
	public void getCheckCode() throws Exception{
		LoginDTO dto =  LoginDTO.getInstance(getRequest());
		String code = VertifyUtil.getVertifyCode();
		dto.setCode(code);
		renderJson(service.getCheckCode(dto));
	}
	
	//注册
	public void register() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		ReturnData rt = service.register(dto);
		renderJson(rt);
	}
	
	//登录
	public void loginWeb() throws Exception{
		
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.login(dto.getMobile()
								,dto.getUserPwd()
								,dto.getUserTypeCd()
								,dto.getPlatForm()
								,dto.getAccessToken()));
	}
	
	//获取忘记密码验证码
	public void getForgetCheckCode() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.getForgetCheckCode(dto));
	}
	
	//保存忘记密码
	public void saveForgetPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveForgetPwd(dto));
	}
	
	//客户修改密码
	@Before(RequestInterceptor.class)
	public void modifyPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.modifyUserPwd(dto));
	}
	
	//退出
	@Before(RequestInterceptor.class)
	public void logout() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.logout(dto));
	}
	
	//首页接口
	public void index() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//获取轮播图
		renderJson(service.index(dto));
	}
	
	//资讯列表
	public void queryNewsList() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewsList(dto));
	}
	
	//资讯详情
	public void queryNewsContent() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewsDetail(dto));
	}
	
	//上传头像
	@Before(RequestInterceptor.class)
	public void uploadIcon() throws Exception{
		UploadFile uploadFile = getFile("icon");
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//表单中有提交图片，要先获取图片
		FileService fs=new FileService();
		String logo = "";
		//上传文件
		String uuid = UUID.randomUUID().toString();
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    File t=new File(Constants.FILE_HOST.ICON+uuid+"."+names[1]);
		    logo = Constants.HOST.ICON+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		}
		renderJson(service.updateIcon(dto.getUserId(), logo));
	}
	
	//修改qq
	@Before(RequestInterceptor.class)
	public void updateQQ() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateQQ(dto.getUserId(), dto.getQq()));
	}
	
	//修改微信
	@Before(RequestInterceptor.class)
	public void updateWX() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateWX(dto.getUserId(), dto.getWx()));
	}
	
	//修改昵称
	@Before(RequestInterceptor.class)
	public void updateNickName() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateNickName(dto.getUserId(), dto.getNickName()));
	}
	
	//认证
	@Before(RequestInterceptor.class)
	public void updateCertificated() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateCertificate(dto));
	}
	
	//收货地址列表
	@Before(RequestInterceptor.class)
	public void queryMemberAddressList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMemberAddressList(dto));
	}
	
	//添加收货地址
	@Before(RequestInterceptor.class)
	public void saveAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveAddress(dto));
	}
	
	//修改收货地址
	@Before(RequestInterceptor.class)
	public void updateAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateAddress(dto));
	}
	
	//查找收货地址
	@Before(RequestInterceptor.class)
	public void queryAddressById(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryAddressById(dto));
	}
	
	//删除收货地址
	@Before(RequestInterceptor.class)
	public void deleteAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.deleteAddressById(dto));
	}
	
	//提交反馈
	@Before(RequestInterceptor.class)
	public void saveFeedBack(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveFeedback(dto));
	}
	
	//检查版本更新
	@Before(RequestInterceptor.class)
	public void queryAppVersion(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryVersion(dto));
	}
	
	//查询消息列表
	@Before(RequestInterceptor.class)
	public void queryMessageList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMessageList(dto));
	}
	
	//新茶发售列表
	public void queryNewTeaSaleList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewTeaSaleList(dto));
	}
	
	//查询新茶
	public void queryNewTeaById(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewTeaById(dto));
	}
	
	//绑定门店
	@Before(RequestInterceptor.class)
	public void bindStore(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//上传头像
		UploadFile uploadFile = getFile("img1");
		UploadFile uploadFile1 = getFile("img2");
		UploadFile uploadFile2 = getFile("img3");
		Integer provinceId = getParaToInt("provinceId");
		Integer cityId = getParaToInt("cityId");
		Integer districtId = getParaToInt("districtId");
		String address = getPara("address");
		Float lgt = StringUtil.toFloat(getPara("longitude"));
		Float lat = StringUtil.toFloat(getPara("latitude"));
		String name = getPara("name");
		String mobile = getPara("mobile");
		String teaStr = getPara("tea");
		String fromTime = getPara("fromTime");
		String toTime = getPara("toTime");
		String mark = getPara("mark");
		
		Store store = new Store();
		store.set("province_id", provinceId);
		store.set("city_id", cityId);
		store.set("district_id", districtId);
		store.set("store_address", address);
		store.set("longitude", lgt);
		store.set("latitude", lat);
		store.set("store_name", name);
		store.set("link_phone", mobile);
		store.set("business_tea", teaStr);
		store.set("business_fromtime", fromTime);
		store.set("business_totime", toTime);
		store.set("store_desc", mark);
		store.set("member_id", dto.getUserId());
		store.set("create_time", DateUtil.getNowTimestamp());
		store.set("update_time", DateUtil.getNowTimestamp());
		store.set("status", Constants.STORE_STATUS.STAY_CERTIFICATE);

		boolean ret = Store.dao.saveInfo(store);
		boolean ret1 = true;
		boolean ret2 = true;
		boolean ret3 = true;
		ReturnData data = new ReturnData();
		if(ret){
			int id = store.getInt("id");
			//表单中有提交图片，要先获取图片
			FileService fs=new FileService();
			String logo1 = "";
			String logo2 = "";
			String logo3 = "";
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
				storeImage.set("create_time", DateUtil.getNowTimestamp());
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret1 = storeImage.dao.saveInfo(storeImage);
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
				storeImage1.set("create_time", DateUtil.getNowTimestamp());
				storeImage1.set("update_time", DateUtil.getNowTimestamp());
				ret2 = storeImage1.dao.saveInfo(storeImage1);
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
				storeImage2.set("create_time", DateUtil.getNowTimestamp());
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret3 = storeImage2.dao.saveInfo(storeImage2);
			}
			if(ret1 && ret2 && ret3){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("提交成功，请等待平台审核");
				renderJson(data);
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败");
			renderJson(data);
		}
	}
	
	//查询绑定门店详情
	@Before(RequestInterceptor.class)
	public void queryStoreDetail(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		int memberId = dto.getUserId();
		ReturnData data = new ReturnData();
		if(memberId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户数据出错");
			renderJson(data);
		}
		Store store = Store.dao.queryById(memberId);
		StoreDetailVO vo = new StoreDetailVO();
		if(store != null){
			vo.setStoreId(store.getInt("id"));
			vo.setAddress(store.getStr("store_address"));
			vo.setCityId(store.getInt("city_id"));
			vo.setDistrictId(store.getInt("district_id"));
			vo.setProvinceId(store.getInt("province_id"));
			vo.setFromTime(store.getStr("business_fromtime"));
			vo.setToTime(store.getStr("business_totime"));
			vo.setLatitude(store.getFloat("latitude"));
			vo.setLongitude(store.getFloat("longitude"));
			vo.setMark(store.getStr("store_desc"));
			vo.setMobile(store.getStr("link_phone"));
			vo.setName(store.getStr("store_name"));
			vo.setTea(store.getStr("business_tea"));
			List<StoreImage> storeImage = StoreImage.dao.queryStoreImages(store.getInt("id"));
			ArrayList<String> imgArrayList = new ArrayList<>();
			for(StoreImage img : storeImage){
				imgArrayList.add(img.getStr("img"));
			}
			vo.setImgs(imgArrayList);
			vo.setStatus(store.getStr("status"));
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("查询成功");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("data", vo);
			data.setData(map);
			renderJson(data);
		}
	}
	
	//更新绑定门店
	@Before(RequestInterceptor.class)
	public void updateBindStore(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//上传头像
		UploadFile uploadFile = getFile("img1");
		UploadFile uploadFile1 = getFile("img2");
		UploadFile uploadFile2 = getFile("img3");
		Integer provinceId = getParaToInt("provinceId");
		Integer cityId = getParaToInt("cityId");
		Integer districtId = getParaToInt("districtId");
		String address = getPara("address");
		Float lgt = StringUtil.toFloat(getPara("longitude"));
		Float lat = StringUtil.toFloat(getPara("latitude"));
		String name = getPara("name");
		String mobile = getPara("mobile");
		String teaStr = getPara("tea");
		String fromTime = getPara("fromTime");
		String toTime = getPara("toTime");
		String mark = getPara("mark");
		
		Store store = new Store();
		store.set("id", getParaToInt("storeId"));
		store.set("province_id", provinceId);
		store.set("city_id", cityId);
		store.set("district_id", districtId);
		store.set("store_address", address);
		store.set("longitude", lgt);
		store.set("latitude", lat);
		store.set("store_name", name);
		store.set("link_phone", mobile);
		store.set("business_tea", teaStr);
		store.set("business_fromtime", fromTime);
		store.set("business_totime", toTime);
		store.set("store_desc", mark);
		store.set("member_id", dto.getUserId());
		store.set("update_time", DateUtil.getNowTimestamp());
		store.set("status", Constants.STORE_STATUS.STAY_CERTIFICATE);
	
		boolean ret = Store.dao.updateInfo(store);
		boolean ret1 = true;
		boolean ret2 = true;
		boolean ret3 = true;
		ReturnData data = new ReturnData();
		if(ret){
			int id = getParaToInt("storeId");
			//表单中有提交图片，要先获取图片
			FileService fs=new FileService();
			String logo1 = "";
			String logo2 = "";
			String logo3 = "";
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
				storeImage.set("update_time", DateUtil.getNowTimestamp());
				ret1 = storeImage.dao.updateInfo(storeImage);
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
				storeImage1.set("update_time", DateUtil.getNowTimestamp());
				ret2 = storeImage1.dao.updateInfo(storeImage1);
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
				storeImage2.set("update_time", DateUtil.getNowTimestamp());
				ret3 = storeImage2.dao.updateInfo(storeImage2);
			}
			if(ret1 && ret2 && ret3){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("提交成功，请等待平台审核");
				renderJson(data);
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败");
			renderJson(data);
		}
	}
	
	//账单
	public void queryBuyNewTeaRecord(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		String queryType = dto.getType();
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.BUY_TEA)){
			//买茶记录
			renderJson(service.queryBuyNewTeaRecord(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.SALE_TEA)){
			//卖茶记录
			renderJson(service.querySaleTeaRecord(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.WAREHOUSE_FEE)){
			//仓储费记录
			renderJson(service.queryWareHouseRecords(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.GET_TEA)){
			//取茶记录
			renderJson(service.queryGetTeaRecords(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.RECHARGE)){
			//充值记录
			
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.WITHDRAW)){
			//提现记录
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.REFUND)){
			//退款记录
		}
		
		ReturnData data = new ReturnData();
		data.setCode(Constants.STATUS_CODE.FAIL);
		data.setMessage("查询失败");
		renderJson(data);
	}
}
