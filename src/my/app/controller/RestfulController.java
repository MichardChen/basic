package my.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.app.service.LoginService;
import my.app.service.RestService;
import my.core.constants.Constants;
import my.core.interceptor.ContainFileInterceptor;
import my.core.interceptor.RequestInterceptor;
import my.core.model.CashJournal;
import my.core.model.Document;
import my.core.model.ReturnData;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.vo.StoreDetailVO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
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
	//@Before(RequestInterceptor.class)
	public void logout() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.logout(dto));
	}
	
	//首页接口，获取初始化数据
	public void index() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//获取初始化数据
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
	public void uploadIcon() throws Exception{
		UploadFile uploadFile = getFile("icon");
		
		ContainFileInterceptor interceptor = new ContainFileInterceptor();
		ReturnData data1 = interceptor.vertifyToken(getRequest());
		if(!StringUtil.equals(data1.getCode(), Constants.STATUS_CODE.SUCCESS)){
			renderJson(data1);
			return;
		}
		
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
	
	@Before(RequestInterceptor.class)
	public void queryMessageListDetail(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMessageListDetail(dto));
	}
	
	//新茶发售列表
	@Before(RequestInterceptor.class)
	public void queryNewTeaSaleList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewTeaSaleList(dto));
	}
	
	//查询新茶发售详情
	@Before(RequestInterceptor.class)
	public void queryNewTeaById(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryNewTeaById(dto));
	}
	
	//绑定门店
	public void bindStore(){
		
		//上传头像
		UploadFile uploadFile = getFile("img1");
		UploadFile uploadFile1 = getFile("img2");
		UploadFile uploadFile2 = getFile("img3");
		UploadFile uploadFile3 = getFile("img4");
		UploadFile uploadFile4 = getFile("img5");
		UploadFile uploadFile5 = getFile("img6");
		
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		ContainFileInterceptor interceptor = new ContainFileInterceptor();
		ReturnData data1 = interceptor.vertifyToken(getRequest());
		if(!StringUtil.equals(data1.getCode(), Constants.STATUS_CODE.SUCCESS)){
			renderJson(data1);
			return;
		}
		
		Integer provinceId = getParaToInt("provinceId");
		Integer cityId = getParaToInt("cityId");
		Integer districtId = getParaToInt("districtId");
		String address = StringUtil.checkCode(getPara("address"));
		Float lgt = StringUtil.toFloat(getPara("longitude"));
		Float lat = StringUtil.toFloat(getPara("latitude"));
		String name = StringUtil.checkCode(getPara("name"));
		String mobile = getPara("mobile");
		String teaStr = StringUtil.checkCode(getPara("tea"));
		String fromTime = StringUtil.checkCode(getPara("fromTime"));
		String toTime = StringUtil.checkCode(getPara("toTime"));
		String mark = StringUtil.checkCode(getPara("mark"));
		
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
		store.set("status", Constants.VERTIFY_STATUS.STAY_CERTIFICATE);
		store.set("city_district", dto.getCityDistrict());
		store.set("key_code", StringUtil.getStoreKeyCode());
		
		int s = Store.dao.saveInfos(store);
		System.out.println("==========storeId====="+s);
		boolean ret = false;
		if(s == 0){
			ret = false;
		}else{
			ret = true;
		}
		boolean ret1 = true;
		boolean ret2 = true;
		boolean ret3 = true;
		boolean ret4 = true;
		boolean ret5 = true;
		boolean ret6 = true;
		ReturnData data = new ReturnData();
		if(ret){
			int id = s;
			//表单中有提交图片，要先获取图片
			FileService fs=new FileService();
			String logo1 = "";
			String logo2 = "";
			String logo3 = "";
			String logo4 = "";
			String logo5 = "";
			String logo6 = "";
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
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("提交成功，请等待平台审核");
				renderJson(data);
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败，请重新提交");
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
			data.setMessage("对不起，您扫描的商家不存在");
			renderJson(data);
		}
		Store store = Store.dao.queryMemberStore(memberId);
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
			vo.setCityDistrict(store.getStr("city_district"));
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
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您还没有绑定门店");
			renderJson(data);
		}
	}
	@Before(RequestInterceptor.class)
	public void findStoreDetail(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		ReturnData data = new ReturnData();
		int memberId = dto.getUserId();
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
	@Before(RequestInterceptor.class)
	public void findStoreDetail1(int memberId){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		ReturnData data = new ReturnData();
		System.out.println("===findStoreDetail=storeId=========="+memberId);
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
	public void updateBindStore(){
		
		//上传头像
		UploadFile uploadFile = getFile("img1");
		UploadFile uploadFile1 = getFile("img2");
		UploadFile uploadFile2 = getFile("img3");
		UploadFile uploadFile3 = getFile("img4");
		UploadFile uploadFile4 = getFile("img5");
		UploadFile uploadFile5 = getFile("img6");
		
		ContainFileInterceptor interceptor = new ContainFileInterceptor();
		ReturnData data1 = interceptor.vertifyToken(getRequest());
		if(!StringUtil.equals(data1.getCode(), Constants.STATUS_CODE.SUCCESS)){
			renderJson(data1);
			return;
		}
		
		Integer provinceId = getParaToInt("provinceId");
		Integer cityId = getParaToInt("cityId");
		Integer districtId = getParaToInt("districtId");
		String address = StringUtil.checkCode(getPara("address"));
		Float lgt = StringUtil.toFloat(getPara("longitude"));
		Float lat = StringUtil.toFloat(getPara("latitude"));
		String name = StringUtil.checkCode(getPara("name"));
		String mobile = StringUtil.checkCode(getPara("mobile"));
		String teaStr = StringUtil.checkCode(getPara("tea"));
		String fromTime = StringUtil.checkCode(getPara("fromTime"));
		String toTime = StringUtil.checkCode(getPara("toTime"));
		String mark = StringUtil.checkCode(getPara("mark"));
		String cityDistrict = StringUtil.checkCode(getPara("cityDistrict"));
		
		//Store store = new Store();
		int storeId = getParaToInt("storeId");
		/*store.set("id", storeId);
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
		store.set("status", Constants.VERTIFY_STATUS.STAY_CERTIFICATE);*/
	
		boolean ret = Store.dao.updateData(storeId,address, lgt, lat, name, mobile, teaStr, fromTime, toTime, mark, Constants.VERTIFY_STATUS.STAY_CERTIFICATE,cityDistrict);
		boolean ret1 = true;
		boolean ret2 = true;
		boolean ret3 = true;
		boolean ret4 = true;
		boolean ret5 = true;
		boolean ret6 = true;
		ReturnData data = new ReturnData();
		if(ret){
			int id = getParaToInt("storeId");
			//表单中有提交图片，要先获取图片
			FileService fs=new FileService();
			String logo1 = "";
			String logo2 = "";
			String logo3 = "";
			String logo4 = "";
			String logo5 = "";
			String logo6 = "";
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
				ret1 = StoreImage.dao.updateInfo(logo1,storeId,1);
				if(!ret1){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo1);
					imgs.set("flg", 1);
					imgs.set("seq", 1);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret1 = StoreImage.dao.saveInfo(imgs);
				}
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
				ret2 = StoreImage.dao.updateInfo(logo2,storeId,2);
				if(!ret2){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo2);
					imgs.set("flg", 1);
					imgs.set("seq", 2);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret2 = StoreImage.dao.saveInfo(imgs);
				}
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
				ret3 = StoreImage.dao.updateInfo(logo3,storeId,3);
				if(!ret3){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo3);
					imgs.set("flg", 1);
					imgs.set("seq", 3);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret3 = StoreImage.dao.saveInfo(imgs);
				}
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
				ret4 = StoreImage.dao.updateInfo(logo4, storeId, 4);
				if(!ret4){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo4);
					imgs.set("flg", 1);
					imgs.set("seq", 4);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret4 = StoreImage.dao.saveInfo(imgs);
				}
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
				ret5 = StoreImage.dao.updateInfo(logo5, storeId, 5);
				if(!ret5){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo5);
					imgs.set("flg", 1);
					imgs.set("seq", 5);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret5 = StoreImage.dao.saveInfo(imgs);
				}
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
				ret6 = StoreImage.dao.updateInfo(logo6, storeId, 6);
				if(!ret6){
					//不存在，就要保存了
					StoreImage imgs = new StoreImage();
					imgs.set("store_id", storeId);
					imgs.set("img", logo6);
					imgs.set("flg", 1);
					imgs.set("seq", 6);
					imgs.set("create_time", DateUtil.getNowTimestamp());
					imgs.set("update_time", DateUtil.getNowTimestamp());
					ret6 = StoreImage.dao.saveInfo(imgs);
				}
			}
			
			if(ret1 && ret2 && ret3 && ret4 && ret5 && ret6){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("提交成功，请等待平台审核");
				renderJson(data);
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("提交失败，请重新提交");
				renderJson(data);
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败，请重新提交");
			renderJson(data);
		}
	}
	
	//账单
	@Before(RequestInterceptor.class)
	public void queryRecord(){
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
			renderJson(service.queryRechargeRecords(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.WITHDRAW)){
			//提现记录
			renderJson(service.queryWithDrawRecords(dto));
			return;
		}
		
		if(StringUtil.equals(queryType, Constants.LOG_TYPE_CD.REFUND)){
			//退款记录
			renderJson(service.queryRefundRecords(dto));
			return;
		}
		
		ReturnData data = new ReturnData();
		data.setCode(Constants.STATUS_CODE.FAIL);
		data.setMessage("查询失败");
		renderJson(data);
	}
	
	//添加到购物车
	@Before(RequestInterceptor.class)
	public void addBuyCart(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.addBuyCart(dto));
	}
	
	//删除购物车
	@Before(RequestInterceptor.class)
	public void deleteBuyCart(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.deleteBuyCart(dto));
	}
	
	//购物车列表
	@Before(RequestInterceptor.class)
	public void queryBuyCartList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryBuyCartLists(dto));
	}
	
	//我要买茶列表
	@Before(RequestInterceptor.class)
	public void queryBuyTeaList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaLists(dto));
	}
	
	//我要买茶按片按件列表
	@Before(RequestInterceptor.class)
	public void queryTeaList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaByIdList(dto));
	}
	
	//我要买茶分析
	//@Before(RequestInterceptor.class)
	public void queryTeaAnalysis(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaAnalysis(dto));
	}
	
	//新茶发行->新茶发行详情->选择规格(具体茶叶的规格)
	@Before(RequestInterceptor.class)
	public void queryTeaSize(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaSize(dto));
	}
	
	//茶资产
	@Before(RequestInterceptor.class)
	public void queryTeaProperty(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaProperty(dto));
	}
	
	//仓储详情
	@Before(RequestInterceptor.class)
	public void queryWareHouseDetail(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryWareHouseDetail(dto));
	}
	
	//我要卖茶出售页面
	@Before(RequestInterceptor.class)
	public void saleTea(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saleTea(dto));
	}
	
	//确定卖茶
	@Before(RequestInterceptor.class)
	public void confirmSaleTea(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.confirmSaleTea(dto));
	}
	
	//取茶初始化
	@Before(RequestInterceptor.class)
	public void takeTeaInit(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.takeTeaInit(dto));
	}
	
	//取茶
	@Before(RequestInterceptor.class)
	public void takeTea(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.takeTea(dto));
	}
	
	//获取文档列表
	public void getDocumentList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.getDocumentList(dto));
	}
	
	//撤单
	@Before(RequestInterceptor.class)
	public void resetOrder(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.resetOrder(dto));
	}
	
	//我要喝茶列表
	//@Before(RequestInterceptor.class)
	public void queryTeaStoreList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaStoreList(dto));
	}
	
	//门店详情
	@Before(RequestInterceptor.class)
	public void queryTeaStoreDetail(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryTeaStoreDetail(dto));
	}
	
	//绑定银行卡
	public void bindBankCard(){
		
		UploadFile uploadFile = getFile("cardImg");
		UploadFile uploadFile1 = getFile("icCardImg");
		
		ContainFileInterceptor interceptor = new ContainFileInterceptor();
		ReturnData data1 = interceptor.vertifyToken(getRequest());
		if(!StringUtil.equals(data1.getCode(), Constants.STATUS_CODE.SUCCESS)){
			renderJson(data1);
			return;
		}
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		FileService fs=new FileService();
		String logo1 = "";
		//上传文件
		//第一张图
		String uuid1 = UUID.randomUUID().toString();
		if(uploadFile != null){
			String fileName = uploadFile.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile.getFile();
		    File t=new File(Constants.FILE_HOST.IMG+uuid1+"."+names[1]);
		    logo1 = Constants.HOST.IMG+uuid1+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    dto.setIcon(logo1);
		}
		
		String logo2 = "";
		//上传文件
		//第一张图
		String uuid2 = UUID.randomUUID().toString();
		if(uploadFile1 != null){
			String fileName = uploadFile1.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile1.getFile();
		    File t=new File(Constants.FILE_HOST.IMG+uuid2+"."+names[1]);
		    logo2 = Constants.HOST.IMG+uuid2+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    dto.setIdCardImg(logo2);
		}
		
		renderJson(service.bingBankCard(dto));
	}
	
	//申请提现
	@Before(RequestInterceptor.class)
	public void withDraw(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.withDraw(dto));
	}
	
	//出售列表
	@Before(RequestInterceptor.class)
	public void querySaleOrderList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.querySaleOrderList(dto));
	}
	
	//我要卖茶列表
	@Before(RequestInterceptor.class)
	public void queryIWantSaleTeaList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryIWantSaleTeaList(dto));
	}
	
	//客户保存支付密码
	@Before(RequestInterceptor.class)
	public void savePayPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveUserPayPwd(dto));
	}
	
	//客户修改支付密码
	@Before(RequestInterceptor.class)
	public void modifyPayPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.modifyUserPayPwd(dto));
	}
	
	//扫码绑定会员
	@Before(RequestInterceptor.class)
	public void bindMember() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.bindMember(dto));
	}
	
	//获取账号余额
	@Before(RequestInterceptor.class)
	public void queryMemberMoney() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMemberMoney(dto));
	}
	
	//提现初始化页面
	@Before(RequestInterceptor.class)
	public void withDrawInit() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.withDrawInit(dto));
	}
	
	//获取忘记支付密码，验证码
	public void getForgetPayCode() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.getForgetPayCode(dto));
	}
	
	//保存忘记支付密码
	@Before(RequestInterceptor.class)
	public void saveForgetPayPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveForgetPayPwd(dto));
	}
	
	//查询银行卡
	@Before(RequestInterceptor.class)
	public void queryBankCard() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryBankCard(dto));
	}
	
	//扫码，查询商家详情
	@Before(RequestInterceptor.class)
	public void queryStore() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryStore(dto));
	}
	
	//付款(选择规格=下单)
	@Before(RequestInterceptor.class)
	public void pay() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.pay(dto));
	}
	
	//购物车下单
	@Before(RequestInterceptor.class)
	public void addOrder() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.addOrder(dto));
	}
	
	//联系我们
	public void contactUs() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.addOrder(dto));
	}
	
	//查询codemst
	public void queryCodeMst() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryCodeMst(dto));
	}
	
	//获取个人数据
	@Before(RequestInterceptor.class)
	public void queryPersonData() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryPersonData(dto));
	}
	
	//跳转文档
	public void queryDocument() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		Document document = Document.dao.queryByTypeCd(dto.getType());
		if(document != null){
			redirect(document.getStr("desc_url"));
		}
	}
}
