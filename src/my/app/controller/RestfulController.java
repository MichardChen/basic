package my.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import my.core.model.User;
import my.pvcloud.dto.IndexDTO;
import my.pvcloud.dto.LoginDTO;
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
	public void updateQQ() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateQQ(dto.getUserId(), dto.getQq()));
	}
	
	//修改微信
	public void updateWX() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateWX(dto.getUserId(), dto.getWx()));
	}
	
	//修改昵称
	public void updateNickName() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateNickName(dto.getUserId(), dto.getNickName()));
	}
	
	//认证
	public void updateCertificated() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateCertificate(dto));
	}
	
	//收货地址列表
	public void queryMemberAddressList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMemberAddressList(dto));
	}
	
	//添加收货地址
	public void saveAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveAddress(dto));
	}
	
	//修改收货地址
	public void updateAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.updateAddress(dto));
	}
	
	//查找收货地址
	public void queryAddressById(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryAddressById(dto));
	}
	
	//删除收货地址
	public void deleteAddress(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.deleteAddressById(dto));
	}
	
	//提交反馈
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
	public void queryMessageList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.queryMessageList(dto));
	}
}
