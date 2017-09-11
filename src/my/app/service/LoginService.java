package my.app.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.plugin.activerecord.Page;
import com.sun.crypto.provider.RSACipher;

import my.core.constants.Constants;
import my.core.model.AcceessToken;
import my.core.model.Admin;
import my.core.model.BankCardRecord;
import my.core.model.BuyCart;
import my.core.model.Carousel;
import my.core.model.City;
import my.core.model.CodeMst;
import my.core.model.District;
import my.core.model.Document;
import my.core.model.FeedBack;
import my.core.model.GetTeaRecord;
import my.core.model.Member;
import my.core.model.Message;
import my.core.model.News;
import my.core.model.Order;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.RecordListModel;
import my.core.model.ReturnData;
import my.core.model.SystemVersionControl;
import my.core.model.Tea;
import my.core.model.VertifyCode;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.tx.TxProxy;
import my.core.vo.AddressDetailVO;
import my.core.vo.AddressVO;
import my.core.vo.BuyCartListVO;
import my.core.vo.BuyTeaListVO;
import my.core.vo.CarouselVO;
import my.core.vo.MessageListVO;
import my.core.vo.NewTeaSaleListModel;
import my.core.vo.NewsVO;
import my.core.vo.TeaDetailModelVO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.MD5Util;
import my.pvcloud.util.SMSUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.TextUtil;
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
		String token = TextUtil.generateUUID();
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
		int id = Member.dao.saveMember(mobile, MD5Util.string2MD5(userPwd),sex,dto.getUserTypeCd(),Constants.MEMBER_STATUS.NOT_CERTIFICATED);
		if(id != 0){
			Member m = Member.dao.queryMemberById(id);
			Map<String, Object> map = new HashMap<>();
			map.put("member", m);
			map.put("accessToken", token);
			VertifyCode.dao.updateVertifyCodeExpire(mobile, now);
			//保存token
			AcceessToken at = AcceessToken.dao.queryToken(id, Constants.USER_TYPE.USER_TYPE_CLIENT);
			boolean tokensave = false;
			if(at == null){
				tokensave = AcceessToken.dao.saveToken(id, Constants.USER_TYPE.USER_TYPE_CLIENT, token);
				if(tokensave){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("注册成功");
					data.setData(map);
					return data;
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("注册失败");
					return data;
				}
			}else{
				AcceessToken.dao.updateToken(id, token);
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("注册成功");
				data.setData(map);
				return data;
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("注册失败");
			return data;
		}
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
		String token = TextUtil.generateUUID();
		if(at == null){
			tokensave = AcceessToken.dao.saveToken(userId, userType, token);
			if(tokensave){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("登录成功");
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("登录失败");
			}
		}else{
			at.updateToken(userId,token);
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("登录成功");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("member", member);
		map.put("accessToken", token);
		data.setData(map);
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
						data.setCode(Constants.STATUS_CODE.SUCCESS);
						data.setMessage("获取验证码成功，十分钟内有效");
					}
					return data;
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
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
					data.setCode(Constants.STATUS_CODE.SUCCESS);
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
	
	public ReturnData index(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		List<Carousel> carousels = Carousel.dao.queryCarouselList(4, 1);
		List<CarouselVO> vos = new ArrayList<CarouselVO>();
		CarouselVO vo = null;
		//查询轮播图
		for(Carousel carousel : carousels){
			 vo = new CarouselVO();
			 vo.setImgUrl(carousel.getStr("img_url"));
			 vo.setRealUrl(carousel.getStr("real_url"));
			 vos.add(vo);
		}
		
		//获取前四条资讯
		Page<News> news = News.dao.queryByPage(1, 4);
		List<NewsVO> newsVOs = new ArrayList<NewsVO>();
		NewsVO nv = null;
		for(News n : news.getList()){
			nv = new NewsVO();
			nv.setTitle(n.getStr("news_title"));
			nv.setDate(DateUtil.format(n.getTimestamp("create_time"), "yyyy-MM-dd"));
			nv.setHotFlg(n.getInt("hot_flg"));
			nv.setImg(n.getStr("news_logo"));
			CodeMst type = CodeMst.dao.queryCodestByCode(n.getStr("news_type_cd"));
			if(type != null){
				nv.setType(type.getStr("name"));
			}
			nv.setNewsId(n.getInt("id"));
			newsVOs.add(nv);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("carousel", vos);
		map.put("news", newsVOs);
		Member member = Member.dao.queryMember(dto.getMobile());
		map.put("member", member);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");;
		data.setData(map);
		return data;
	}
	
	//资讯列表
	public ReturnData queryNewsList(LoginDTO dto) throws Exception{
		
		ReturnData data = new ReturnData();
		//获取前四条资讯
		Page<News> news = News.dao.queryByPage(dto.getPageNum(), dto.getPageSize());
		List<NewsVO> newsVOs = new ArrayList<NewsVO>();
		NewsVO nv = null;
		for(News n : news.getList()){
			nv = new NewsVO();
			nv.setTitle(n.getStr("news_title"));
			nv.setDate(DateUtil.format(n.getTimestamp("create_time"), "yyyy-MM-dd"));
			nv.setHotFlg(n.getInt("hot_flg"));
			nv.setImg(n.getStr("news_logo"));
			CodeMst type = CodeMst.dao.queryCodestByCode(n.getStr("news_type_cd"));
			if(type != null){
				nv.setType(type.getStr("name"));
			}
			//nv.setContent(n.getStr("content"));
			nv.setNewsId(n.getInt("id"));
			nv.setShareUrl(n.getStr("content_url"));
			newsVOs.add(nv);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("news", newsVOs);
		data.setData(map);
		return data;
	}
	
	//资讯详情
	public ReturnData queryNewsDetail(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		News news = News.dao.queryById(dto.getNewsId());
		if(news == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，资讯不存在");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("查询成功");
			Map<String, Object> map = new HashMap<>();
			map.put("content", news.getStr("content"));
			data.setData(map);
		}
		return data;
	}
	
	//上传头像
	public ReturnData updateIcon(int userId,String icon){
		
		ReturnData data = new ReturnData();
		int ret = Member.dao.updateIcon(userId, icon);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
		return data;
	}
	
	//修改qq
	public ReturnData updateQQ(int userId,String qq){
		
		ReturnData data = new ReturnData();
		int ret = Member.dao.updateQQ(userId, qq);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
		return data;
	}
		
	//修改微信
	public ReturnData updateWX(int userId,String wx){
		
		ReturnData data = new ReturnData();
		int ret = Member.dao.updateWX(userId, wx);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
		return data;
	}
	
	//修改昵称
	public ReturnData updateNickName(int userId,String nickName){

		ReturnData data = new ReturnData();
		int ret = Member.dao.updateNickName(userId, nickName);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
			return data;
	}

	//认证
	public ReturnData updateCertificate(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		int ret = Member.dao.updateCertification(dto.getUserId()
												    ,dto.getUserName()
												    ,dto.getCardNo()
												    ,Constants.MEMBER_STATUS.CERTIFICATED);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
		return data;
	}
	
	//查询邮寄地址
	public ReturnData queryMemberAddressList(LoginDTO dto){
		ReturnData data = new ReturnData();
		Page<ReceiveAddress> pages = ReceiveAddress.dao.queryByPage(dto.getPageNum()
																   ,dto.getPageSize()
																   ,dto.getUserId()
																   ,Constants.COMMON_STATUS.NORMAL);
		List<AddressVO> vos = new ArrayList<>();
		List<ReceiveAddress> list = pages.getList();
		AddressVO vs = null;
		for(ReceiveAddress ra : list){
			vs = new AddressVO();
			String address = "";
			Province province = Province.dao.queryProvince(ra.getInt("province_id"));
			City city = City.dao.queryCity(ra.getInt("city_id"));
			District district = District.dao.queryDistrict(ra.getInt("district_id"));
			if(province!=null){
				address = address + province.getStr("name") + "省";
			}
			if(city != null){
				address = address + city.getStr("name") + "市";
			}
			if(district != null){
				address = address + district.getStr("name") + "区";
			}
			vs.setAddress(address+ra.getStr("address"));
			vs.setAddressId(ra.getInt("id"));
			vs.setDefaultFlg(ra.getInt("default_flg"));
			vs.setLinkTel(ra.getStr("mobile"));
			vs.setLinkMan(ra.getStr("receiveman_name"));
			vos.add(vs);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("address", vos);
		data.setData(map);
		return data;
	}
	
	//保存收货地址
	public ReturnData saveAddress(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		ReceiveAddress ra = new ReceiveAddress();
		ra.set("receiveman_name", dto.getLinkMan());
		ra.set("mobile", dto.getMobile());
		ra.set("province_id", dto.getProvinceId());
		ra.set("city_id", dto.getCityId());
		ra.set("district_id", dto.getDistrictId());
		ra.set("address", dto.getAddress());
		ra.set("status", Constants.COMMON_STATUS.NORMAL);
		ra.set("default_flg", dto.getFlg());
		ra.set("member_id", dto.getUserId());
		ra.set("create_time", DateUtil.getNowTimestamp());
		ra.set("update_time", DateUtil.getNowTimestamp());
		boolean save = ReceiveAddress.dao.saveInfo(ra);
		if(save){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}
		return data;
	}
	
	//修改收货地址
	public ReturnData updateAddress(LoginDTO dto){

		ReturnData data = new ReturnData();
		ReceiveAddress ra = new ReceiveAddress();
		ra.set("receiveman_name", dto.getLinkMan());
		ra.set("mobile", dto.getMobile());
		ra.set("province_id", dto.getProvinceId());
		ra.set("city_id", dto.getCityId());
		ra.set("district_id", dto.getDistrictId());
		ra.set("address", dto.getAddress());
		ra.set("status", Constants.COMMON_STATUS.NORMAL);
		ra.set("default_flg", dto.getFlg());
		ra.set("member_id", dto.getUserId());
		ra.set("update_time", DateUtil.getNowTimestamp());
		ra.set("id", dto.getId());
		boolean save = ReceiveAddress.dao.updateInfo(ra);
		if(save){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("修改成功");
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("修改失败");
		}
		return data;
	}
	
	//查询收货地址详情
	public ReturnData queryAddressById(LoginDTO dto){

		ReturnData data = new ReturnData();
		ReceiveAddress ra = ReceiveAddress.dao.queryById(dto.getId(),Constants.COMMON_STATUS.NORMAL);
		AddressDetailVO vo = new AddressDetailVO();
		if(ra != null){
			vo.setId(ra.getInt("id"));
			vo.setAddress(ra.getStr("address"));
			vo.setCityId(ra.getInt("city_id"));
			vo.setProvinceId(ra.getInt("province_id"));
			vo.setDistrictId(ra.getInt("district_id"));
			vo.setDefaultFlg(ra.getInt("default_flg"));
			vo.setReceiverMan(ra.getStr("receiveman_name"));
			vo.setMobile(ra.getStr("mobile"));
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("查询成功");
			Map<String, Object> map = new HashMap<>();
			map.put("address", vo);
			data.setData(map);
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，地址不存在");
			Map<String, Object> map = new HashMap<>();
			map.put("address", ra);
			data.setData(map);
		}
		return data;
	}
	
	//删除收货地址
	public ReturnData deleteAddressById(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		ReceiveAddress ra = new ReceiveAddress();
		ra.set("update_time", DateUtil.getNowTimestamp());
		ra.set("id", dto.getId());
		ra.set("status", Constants.COMMON_STATUS.DELETE);
		
		boolean deleteFlg = ReceiveAddress.dao.updateInfo(ra);
		if(deleteFlg){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("删除成功");
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("删除失败");
		}
		return data;
	}
	
	//提交意见反馈
	public ReturnData saveFeedback(LoginDTO dto){
				
		ReturnData data = new ReturnData();
		FeedBack feedBack = new FeedBack();
		feedBack.set("user_id", dto.getUserId());
		feedBack.set("user_type_cd", dto.getUserTypeCd());
		feedBack.set("feedback", dto.getFeedBack());
		feedBack.set("create_time", DateUtil.getNowTimestamp());
		feedBack.set("update_time", DateUtil.getNowTimestamp());
		feedBack.set("readed", 1);
		boolean ret = FeedBack.dao.saveInfo(feedBack);
		if(!ret){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
		return data;
	}
	

	//查询版本
	public ReturnData queryVersion(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		String vtc = dto.getVersionTypeCd();
		Map<String, Object> map = new HashMap<>();
		SystemVersionControl svc = SystemVersionControl.dao.querySystemVersionControl(vtc);
		if(svc == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错");
			return data;
		}
		String dbVersion = svc.getStr("version");
		if(StringUtil.equals(vtc, Constants.VERSION_TYPE.ANDROID)){
			Integer version = StringUtil.toInteger(dto.getVersion());
			Integer newVersion = StringUtil.toInteger(dbVersion);
			if(newVersion > version){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("发现新版本："+svc.getStr("mark"));
				map.put("url", svc.getStr("data1"));
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("当前版本已是最新版本");
				map.put("url", null);
			}
		}
		if(StringUtil.equals(vtc, Constants.VERSION_TYPE.IOS)){
			String version = dto.getVersion();
			if(!StringUtil.equals(dbVersion,version)){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("发现新版本："+svc.getStr("mark"));
				map.put("url", svc.getStr("data2"));
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("当前版本已是最新版本");
				map.put("url", null);
			}
		}
		data.setData(map);
		return data;
	}
	
	//查询消息列表
	public ReturnData queryMessageList(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		int userId = dto.getUserId();
		String typeCd = dto.getType();
		List<Message> messages = Message.dao.queryMessages(userId, typeCd);
		List<MessageListVO> vos = new ArrayList<>();
		MessageListVO vo = null;
		for(Message message : messages){
			vo = new MessageListVO();
			vo.setId(message.getInt("id"));
			vo.setTitle(message.getStr("title"));
			vo.setTypeCd(message.getStr("message_type_cd"));
			vo.setDate(DateUtil.formatTimestampForDate(message.getTimestamp("create_time")));
			CodeMst type = CodeMst.dao.queryCodestByCode(vo.getTypeCd());
			if(type != null){
				vo.setType(type.getStr("name"));
			}else{
				vo.setType("");
			}
			vo.setParams(message.getStr("params"));
			vos.add(vo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("messages", vos);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//查询新茶发售列表
	public ReturnData queryNewTeaSaleList(LoginDTO dto){
				
		ReturnData data = new ReturnData();
		List<Tea> list = Tea.dao.queryNewTeaSale(dto.getPageSize(), dto.getPageNum());
		List<NewTeaSaleListModel> models = new ArrayList<>();
		NewTeaSaleListModel model = null;
		for(Tea tea : list){
			model = new NewTeaSaleListModel();
			model.setTeaId(tea.getInt("id"));
			
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			if(wtm != null){
				model.setStock(wtm.getInt("stock"));
			}
			String coverImg = tea.getStr("cover_img");
			String[] imgs = coverImg.split(",");
			model.setImg(imgs[0]);
			model.setName(tea.getStr("tea_title"));
			model.setStatus(tea.getStr("status"));
			CodeMst statusCodeMst = CodeMst.dao.queryCodestByCode(tea.getStr("status"));
			if(statusCodeMst != null){
				model.setStatusName(statusCodeMst.getStr("name"));
			}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		//新茶发售备注
		Document newTeaSaleMark = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.NEW_TEA_SALE_MARK);
		if(newTeaSaleMark != null){
			map.put("newTeaSaleMark", newTeaSaleMark.getStr("content"));
		}
		//发售说明
		Document saleComment = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.SALE_COMMENT);
		if(saleComment != null){
			map.put("saleCommentUrl", saleComment.getStr("desc_url"));
		}
		map.put("models", models);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	

	//查询新茶发售列表
	public ReturnData queryNewTeaById(LoginDTO dto){
				
		ReturnData data = new ReturnData();
		Tea tea = Tea.dao.queryById(dto.getId());
		if(tea == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("查询失败，茶叶数据不存在");
			return data;
		}
		
		TeaDetailModelVO vo = new TeaDetailModelVO();
		String coverImgs = tea.getStr("cover_img");
		if(StringUtil.isNoneBlank(coverImgs)){
			String[] imgs = coverImgs.split(",");
			List<String> cList = new ArrayList<>();
			for(String str : imgs){
				cList.add(str);
			}
			vo.setImg(cList);
		}
		vo.setId(tea.getInt("id"));
		vo.setName(tea.getStr("tea_title"));
		vo.setAmount(StringUtil.toString(tea.getInt("total_output")));
		vo.setBirthday(DateUtil.format(tea.getDate("product_date")));
		vo.setBrand(tea.getStr("brand"));
		vo.setCertificateFlg(tea.getInt("certificate_flg"));
		Document mst = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.CERTIFICATE_TIP);
		if(mst != null){
			vo.setComment(mst.getStr("content"));
		}
		CodeMst phoneCodeMst = CodeMst.dao.queryCodestByCode(Constants.PHONE.CUSTOM);
		if(phoneCodeMst != null){
			vo.setCustomPhone(phoneCodeMst.getStr("data2"));
		}
		vo.setDescUrl(tea.getStr("desc_url"));
		
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
		if(wtm != null){
			vo.setPrice(StringUtil.toString(wtm.getBigDecimal("price")));
			vo.setStock(StringUtil.toString(wtm.getInt("stock")));
		}
		vo.setProductPlace(tea.getStr("product_place"));
		vo.setSaleTime(tea.getDate("sale_from_date")+"至"+tea.getDate("sale_to_date"));
		vo.setSize(tea.getInt("size1")+"克/片、"+tea.getInt("size2")+"片/件");
		vo.setSize2(tea.getInt("size2")+"片/件");
		CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
		if(type != null){
			vo.setType(type.getStr("name"));
		}
		vo.setStatus(tea.getStr("status"));
		Map<String, Object> map = new HashMap<>();
		map.put("tea", vo);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//新茶购买记录
	public ReturnData queryBuyNewTeaRecord(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<Order> list = Order.dao.queryBuyNewTeaRecord(dto.getPageSize()
														 ,dto.getPageNum()
														 ,dto.getUserId()
														 ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		RecordListModel model = null;
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		for(Order order : list){
			model = new RecordListModel();
			model.setType(type);
			model.setId(order.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+order.getInt("quality")+"片");
			}
			model.setMoneys("-"+StringUtil.toString(order.getBigDecimal("pay_amount")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//卖茶记录
	public ReturnData querySaleTeaRecord(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<Order> list = Order.dao.querySaleTeaRecord(dto.getPageSize()
														 ,dto.getPageNum()
														 ,dto.getUserId()
														 ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		RecordListModel model = null;
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		for(Order order : list){
			model = new RecordListModel();
			model.setType(type);
			model.setId(order.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+order.getInt("quality")+"片");
			}
			model.setMoneys("+"+StringUtil.toString(order.getBigDecimal("pay_amount")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//取茶记录
	public ReturnData queryGetTeaRecords(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<GetTeaRecord> list = GetTeaRecord.dao.queryRecords(dto.getPageSize()
															   ,dto.getPageNum()
															   ,dto.getUserId()
															   ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		for(GetTeaRecord record : list){
			model = new RecordListModel();
			model.setType(type);
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+record.getInt("quality")+"片");
			}
			//model.setMoneys(StringUtil.toString(record.getBigDecimal("warehouse_fee")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//仓储费记录
	public ReturnData queryWareHouseRecords(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<GetTeaRecord> list = GetTeaRecord.dao.queryRecords(dto.getPageSize()
															   ,dto.getPageNum()
															   ,dto.getUserId()
															   ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		for(GetTeaRecord record : list){
			model = new RecordListModel();
			model.setType(type);
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+record.getInt("quality")+"片");
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("warehouse_fee")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//充值记录
	public ReturnData queryRechargeRecords(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<BankCardRecord> list = BankCardRecord.dao.queryRecords(dto.getPageSize()
																   ,dto.getPageNum()
																   ,dto.getUserId()
																   ,Constants.BANK_MANU_TYPE_CD.RECHARGE
																   ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		for(BankCardRecord record : list){
			model = new RecordListModel();
			model.setType(type);
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys("+"+StringUtil.toString(record.getBigDecimal("moneys")));
			model.setContent("银行卡充值："+StringUtil.toString(record.getBigDecimal("moneys")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//提现记录
	public ReturnData queryWithDrawRecords(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<BankCardRecord> list = BankCardRecord.dao.queryRecords(dto.getPageSize()
																   ,dto.getPageNum()
																   ,dto.getUserId()
																   ,Constants.BANK_MANU_TYPE_CD.WITHDRAW
																   ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		for(BankCardRecord record : list){
			model = new RecordListModel();
			model.setType(type);
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys("+"+StringUtil.toString(record.getBigDecimal("moneys")));
			model.setContent("账号提现："+StringUtil.toString(record.getBigDecimal("moneys")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//退款记录
	public ReturnData queryRefundRecords(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<BankCardRecord> list = BankCardRecord.dao.queryRecords(dto.getPageSize()
																   ,dto.getPageNum()
																   ,dto.getUserId()
																   ,Constants.BANK_MANU_TYPE_CD.REFUND
																   ,dto.getDate());
		List<RecordListModel> models = new ArrayList<>();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		for(BankCardRecord record : list){
			model = new RecordListModel();
			model.setType(type);
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys("+"+StringUtil.toString(record.getBigDecimal("moneys")));
			model.setContent("账号退款："+StringUtil.toString(record.getBigDecimal("moneys")));
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("logs", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//添加购物车
	public ReturnData addBuyCart(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		BuyCart cart = new BuyCart();
		cart.set("warehouse_tea_member_id", dto.getTeaId());
		cart.set("quality", dto.getQuality());
		cart.set("status", Constants.ORDER_STATUS.SHOPPING_CART);
		cart.set("create_time", DateUtil.getNowTimestamp());
		cart.set("update_time", DateUtil.getNowTimestamp());
		cart.set("size", dto.getSize());
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(dto.getTeaId());
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据不存在");
			return data;
		}
		int memberId = wtm.getInt("member_id");
		String memberUserCd = wtm.getStr("member_type_cd");
		cart.set("member_id", dto.getUserId());
		cart.set("sale_id", memberId);
		cart.set("sale_user_type", memberUserCd);
		boolean save = BuyCart.dao.saveInfo(cart);
		if(save){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("添加成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("添加失败");
			return data;
		}
	}
	
	//删除购物车
	public ReturnData deleteBuyCart(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		int ret = BuyCart.dao.updateStatus(dto.getBuyCartIds(), Constants.ORDER_STATUS.DELETE);
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("删除成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("删除失败");
			return data;
		}
	}
	
	//购物车列表
	public ReturnData queryBuyCartLists(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		List<BuyCart> carts = BuyCart.dao.queryBuyCart(dto.getPageSize()
													  ,dto.getPageNum()
													  ,dto.getUserId());
		
		//查询购物车有多少个
		Long count = BuyCart.dao.queryBuycartCount(dto.getUserId());
		List<BuyCartListVO> vos = new ArrayList<>();
		BuyCartListVO vo = null;
		for(BuyCart cart:carts){
			vo = new BuyCartListVO();
			vo.setCartId(cart.getInt("id"));
			vo.setQuality(cart.getInt("quality"));
			CodeMst size = CodeMst.dao.queryCodestByCode(cart.getStr("size"));
			if(size != null){
				vo.setSize(size.getStr("name"));
			}else{
				vo.setSize(StringUtil.STRING_BLANK);
			}
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(cart.getInt("warehouse_tea_member_id"));
			if(wtm != null){
				WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
				if(house != null){
					vo.setWarehouse(house.getStr("warehouse_name"));
				}else{
					vo.setWarehouse(StringUtil.STRING_BLANK);
				}
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if(tea != null){
					vo.setImg(tea.getStr("cover_img"));
					vo.setName(tea.getStr("tea_title"));
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if(type!=null){
						vo.setType(type.getStr("name"));
					}else{
						vo.setType(StringUtil.STRING_BLANK);
					}
				}else{
					vo.setImg(StringUtil.STRING_BLANK);
					vo.setName(StringUtil.STRING_BLANK);
					vo.setType(StringUtil.STRING_BLANK);
				}
				if(StringUtil.equals(cart.getStr("size"), Constants.TEA_UNIT.PIECE)){
					vo.setPrice(wtm.getBigDecimal("piece_price"));
				}else if(StringUtil.equals(cart.getStr("size"), Constants.TEA_UNIT.ITEM)){
					vo.setPrice(wtm.getBigDecimal("item_price"));
				}else{
					vo.setPrice(new BigDecimal("0"));
				}
				
				vo.setStock(wtm.getInt("stock"));
			}else{
				vo.setWarehouse(StringUtil.STRING_BLANK);
				vo.setImg(StringUtil.STRING_BLANK);
				vo.setName(StringUtil.STRING_BLANK);
				vo.setPrice(new BigDecimal("0"));
				vo.setType(StringUtil.STRING_BLANK);
				vo.setStock(0);
			}
			vos.add(vo);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("data", vos);
		map.put("buycartCount", count);
		data.setData(map);
		return data;
	} 
	
	//我要买茶
	public ReturnData queryTeaLists(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		List<Tea> teas = Tea.dao.queryBuyTeaList(dto.getPageSize(), dto.getPageNum(),dto.getName());
		List<BuyTeaListVO> vos = new ArrayList<>();
		BuyTeaListVO vo = null;
		for(Tea tea : teas){
			vo = new BuyTeaListVO();
			vo.setId(tea.getInt("id"));
			vo.setImg(tea.getStr("cover_img"));
			vo.setName(tea.getStr("tea_title"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryTeaOnPlatform(Constants.USER_TYPE.PLATFORM_USER, tea.getInt("id"));
			if(wtm != null){
				vo.setPrice(wtm.getBigDecimal("piece_price"));
			}
			vo.setSize("片");
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				vo.setType(type.getStr("name"));
			}
			vos.add(vo);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("data", vos);
		data.setData(map);
		return data;
	}
	
	public ReturnData queryTeaByIdList(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		String priceFlg = dto.getPriceType();
		int wareHouseId = dto.getWareHouseId();
		int quality = dto.getQuality();
		
		String size = dto.getSize();
		List<WarehouseTeaMember> list = WarehouseTeaMember.dao.queryTeaByIdList(size
																			   ,priceFlg
																			   ,wareHouseId
																			   ,quality
																			   ,dto.getPageSize()
																			   ,dto.getPageNum());
		 
		return data;
	}
}
