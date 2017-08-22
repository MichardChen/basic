package my.app.service;

import java.io.IOException;
import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import my.core.constants.Constants;
import my.core.model.AcceessToken;
import my.core.model.Admin;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.model.VertifyCode;
import my.core.tx.TxProxy;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.MD5Util;
import my.pvcloud.util.SMSUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.VertifyUtil;

public class LoginService {

	public static final LoginService service = TxProxy.newProxy(LoginService.class);
	
	//获取验证码
	public ReturnData getCheckCode(LoginDTO dto){
		Member member = Member.dao.queryMember(dto.getMobile());
		ReturnData data = new ReturnData();
		String code = VertifyUtil.getVertifyCode();
		VertifyCode vc = VertifyCode.dao.queryVertifyCode(dto.getMobile());
		if(vc != null){
			Timestamp expireTime = vc.getTimestamp("expire_time");
			Timestamp nowTime = DateUtil.getNowTimestamp();
			if(expireTime.after(nowTime)){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("验证码已发送，10分钟内有效，请稍等接收");
				return data;
			}
		}
		if(member != null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您的手机号码已经注册");
			return data;
		}else{
			VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile());
			if(vCode == null){
				VertifyCode.dao.saveVertifyCode(dto.getMobile(), dto.getUserTypeCd(), code,new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
			}else{
				VertifyCode.dao.updateVertifyCode(dto.getMobile(), code);
			}
			//发送短信
			String shortMsg = "您的验证码是：" + code + "，10分钟内有效，请不要把验证码泄露给其他人。";
			//发送短信
			String ret = null;
			try {
				ret = SMSUtil.sendMessage(shortMsg, dto.getMobile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(StringUtil.equals(ret, "1")){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("验证码发送失败，请重新获取");
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("获取验证码成功，十分钟内有效");
			}
			return data;
		}
	}
	
	//注册
	public ReturnData register(LoginDTO dto) throws JSONException{
		
		ReturnData data = new ReturnData();
		String mobile = dto.getMobile();
		String userPwd = dto.getUserPwd();
		String code = dto.getCode();
		int sex = dto.getSex();
		//获取验证码有效时间
		VertifyCode vCode = VertifyCode.dao.queryVertifyCode(mobile);
		Timestamp expireTime = vCode == null ? null : (Timestamp)vCode.get("expire_time");
		Timestamp now = DateUtil.getNowTimestamp();
		Member member = Member.dao.queryMember(mobile);
		if(member != null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您的手机号码已经注册了");
			return data;
		}
		
		if(expireTime == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有获取验证码");
			return data;
		}
		
		if((expireTime != null)&&(now.after(expireTime))){
			//true，就是过期了
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("验证码过期了，请重新获取");
			return data;
		}
		
		if((expireTime != null) && (expireTime.after(now))){
			//没有过期，获取数据库验证码
			String dcode = vCode.getStr("code");
			if(!StringUtil.equals(code, dcode)){
				//验证码错误
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("请输入正确的验证码");
				return data;
			}
		}
		
		//保存用户
		int id = Member.dao.saveMember(mobile, MD5Util.string2MD5(userPwd),sex,dto.getUserTypeCd());
		if(id != 0){
			VertifyCode.dao.updateVertifyCodeExpire(mobile, now);
			//String token = TextUtil.generateUUID();
			//AcceessToken.dao.saveToken(id, userTypeCd, token);
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			JSONObject rt = new JSONObject();
			//rt.put("token", token);
			rt.put("userId", id);
			data.setMessage("注册成功");
			data.setData(rt);
			return data;
		}
		
		data.setCode(Constants.STATUS_CODE.FAIL);
		data.setMessage("注册失败");
		return data;
	}
	
	//登录
	public ReturnData login(String mobile
						   ,String userPwd
						   ,String userType
						   ,String platForm
						   ,String deviceToken) throws Exception{
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(mobile);
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您的账号尚未注册");
			return data;
		}
		if(!StringUtil.equals(MD5Util.string2MD5(userPwd), member.getStr("userpwd"))){
			data.setMessage("对不起吗，密码错误");
			data.setCode(Constants.STATUS_CODE.FAIL);
			return data;
		}
		
		//保存token
		int userId = member.getInt("id");
		AcceessToken at = AcceessToken.dao.queryToken(userId, userType);
		boolean tokensave = false;
		if(at == null){
			tokensave = AcceessToken.dao.saveToken(userId, userType, deviceToken);
			if(tokensave){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("登录成功");
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("登录失败");
			}
		}else{
			at.updateToken(userId,deviceToken);
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("登录成功");
		}
		data.setData(member);
		return data;
	}
	
	//退出
	public ReturnData logout(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(dto.getMobile());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}
		AcceessToken token = AcceessToken.dao.queryById(member.getInt("id"));
		if(token == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有登录");
			return data;
		}
		
		if(!StringUtil.equals(token.getStr("token"), dto.getToken())){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您的账号在另一处登录");
			return data;
		}
		
		AcceessToken.dao.updateToken(member.getInt("id"), "");
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("退出成功");
		return data;
	}
	
	//忘记密码，获取验证码
	public ReturnData getForgetCheckCode(LoginDTO dto){
		Member member = Member.dao.queryMember(dto.getMobile());
		String code = VertifyUtil.getVertifyCode();
		ReturnData data = new ReturnData();
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您的手机号码还未注册");
			return data;
		}else{
			//获取VertifyCode
			VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile());
			if(vCode == null){
				boolean isSave = VertifyCode.dao.saveVertifyCode(dto.getMobile(), dto.getUserTypeCd(), code,new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
				if(isSave){
					//发送短信
					String shortMsg = "您的验证码是：" + code + "，10分钟内有效，请不要把验证码泄露给其他人。";
					//发送短信
					String ret = null;
					try {
						ret = SMSUtil.sendMessage(shortMsg, dto.getMobile());
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(StringUtil.equals(ret, "1")){
						data.setCode(Constants.STATUS_CODE.FAIL);
						data.setMessage("验证码发送失败，请重新获取");
					}else{
						data.setCode(Constants.STATUS_CODE.FAIL);
						data.setMessage("获取验证码成功，十分钟内有效");
					}
					return data;
				}else{
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("获取验证码失败");
					return data;
				}
			}else{
				//更新验证码
				VertifyCode.dao.updateVertifyCode(dto.getMobile(), code);
				//发送短信
				//发送短信
				String shortMsg = "您的验证码是：" + code + "，10分钟内有效，请不要把验证码泄露给其他人。";
				//发送短信
				String ret = null;
				try {
					ret = SMSUtil.sendMessage(shortMsg, dto.getMobile());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(StringUtil.equals(ret, "1")){
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("验证码发送失败，请重新获取");
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("获取验证码成功，十分钟内有效");
				}
				return data;
			}
		}
	}
	
	//保存修改密码
	public ReturnData saveForgetPwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile());
		if(vCode == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("请重新获取验证码");
			return data;
		}
		if(!StringUtil.equals(dto.getCode(), vCode.getStr("code"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("请输入正确的验证码");
			return data;
		}
		
		//判断验证码是不是过期
		Timestamp expireTime = (Timestamp)vCode.get("expire_time");
		Timestamp now = DateUtil.getNowTimestamp();
		if((expireTime != null)&&(now.after(expireTime))){
			//true，就是过期了
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("验证码过期了，请重新获取");
			return data;
		 }else{
			//把验证码设置为过期
			VertifyCode.dao.updateVertifyCodeExpire(dto.getMobile(), now);
			//保存密码
			Member.dao.updatePwd(dto.getMobile(), MD5Util.string2MD5(dto.getUserPwd()));
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("密码修改成功");
			return data;
		 }
	}
	
	//客户修改密码
	public ReturnData modifyUserPwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(dto.getMobile());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}
		if(!StringUtil.equals(member.getStr("userPwd"), MD5Util.string2MD5(dto.getOldPwd()))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，旧密码错误");
			return data;
		}
		//保存密码
		Member.dao.updatePwd(dto.getMobile(), MD5Util.string2MD5(dto.getNewPwd()));
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("密码修改成功");
		return data;
	}
	
	//客户修改密码
	public ReturnData modifyEmployeePwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		Admin admin = Admin.dao.queryAdminByMobile(dto.getMobile());
		if(admin == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}
		if(!StringUtil.equals(admin.getStr("password"), MD5Util.string2MD5(dto.getOldPwd()))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，旧密码错误");
			return data;
		}
		//保存密码
		Admin.dao.updatePwd(dto.getMobile(), MD5Util.string2MD5(dto.getNewPwd()));
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("密码修改成功");
		return data;
	}
	
	//查询用户会员
	public Member queryMember(String mobile,String userPwd){
		return Member.dao.queryMember(mobile,userPwd);
	}
}
