package my.app.service;

import java.beans.Transient;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import my.core.constants.Constants;
import my.core.model.AcceessToken;
import my.core.model.Admin;
import my.core.model.BankCardRecord;
import my.core.model.BuyCart;
import my.core.model.Carousel;
import my.core.model.CashJournal;
import my.core.model.City;
import my.core.model.CodeMst;
import my.core.model.District;
import my.core.model.Document;
import my.core.model.FeedBack;
import my.core.model.GetTeaRecord;
import my.core.model.Invoice;
import my.core.model.InvoiceGetteaRecord;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.Message;
import my.core.model.News;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.RecordListModel;
import my.core.model.ReturnData;
import my.core.model.SaleOrder;
import my.core.model.Store;
import my.core.model.StoreEvaluate;
import my.core.model.StoreImage;
import my.core.model.SystemVersionControl;
import my.core.model.Tea;
import my.core.model.TeaPrice;
import my.core.model.User;
import my.core.model.VertifyCode;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.tx.TxProxy;
import my.core.vo.AddressDetailVO;
import my.core.vo.AddressVO;
import my.core.vo.BankCardDetailVO;
import my.core.vo.BankCardVO;
import my.core.vo.BuyCartListVO;
import my.core.vo.BuyTeaListVO;
import my.core.vo.CarouselVO;
import my.core.vo.ChooseAddressVO;
import my.core.vo.CodeMstVO;
import my.core.vo.DataListVO;
import my.core.vo.DistanceModel;
import my.core.vo.DocumentListVO;
import my.core.vo.EvaluateListModel;
import my.core.vo.InvoiceListModel;
import my.core.vo.MemberDataVO;
import my.core.vo.MemberOrderListModel;
import my.core.vo.MessageListDetailVO;
import my.core.vo.MessageListVO;
import my.core.vo.NewTeaSaleListModel;
import my.core.vo.NewsVO;
import my.core.vo.OrderAnalysisVO;
import my.core.vo.ReferencePriceModel;
import my.core.vo.SaleOrderListVO;
import my.core.vo.SelectSizeTeaListVO;
import my.core.vo.StoreDetailListVO;
import my.core.vo.StoreMemberListModel;
import my.core.vo.TeaDetailModelVO;
import my.core.vo.TeaPropertyListVO;
import my.core.vo.TeaStoreListVO;
import my.core.vo.TeaWarehouseDetailVO;
import my.core.vo.WantSaleTeaListVO;
import my.core.vo.WarehouseStockVO;
import my.core.vo.WithDrawInitVO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.GeoUtil;
import my.pvcloud.util.SMSUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.TextUtil;
import my.pvcloud.util.VertifyUtil;
import my.pvcloud.vo.StoreDetailVO;
import net.sf.json.JSONObject;

public class LoginService {

	public static final LoginService service = TxProxy.newProxy(LoginService.class);
	
	//获取验证码
	public ReturnData getCheckCode(LoginDTO dto){
		Member member = Member.dao.queryMember(dto.getMobile());
		ReturnData data = new ReturnData();
		String code = VertifyUtil.getVertifyCode();
		VertifyCode vc = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.REGISTER);
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
			VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.REGISTER);
			if(vCode == null){
				VertifyCode.dao.saveVertifyCode(dto.getMobile(), dto.getUserTypeCd(), code,new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),Constants.SHORT_MESSAGE_TYPE.REGISTER);
			}else{
				VertifyCode.dao.updateVertifyCode(dto.getMobile(), code,Constants.SHORT_MESSAGE_TYPE.REGISTER);
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
		VertifyCode vCode = VertifyCode.dao.queryVertifyCode(mobile,Constants.SHORT_MESSAGE_TYPE.REGISTER);
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
		String invateCode = dto.getInvateCode();
		int storeId = 0;
		Store businessStore = Store.dao.queryStoreByInviteCode(invateCode);
		if(businessStore != null){
			storeId = businessStore.getInt("id");
		}
		
		int id = Member.dao.saveMember(mobile, userPwd,sex,dto.getUserTypeCd(),Constants.MEMBER_STATUS.NOT_CERTIFICATED,storeId);
		if(id != 0){
			Member m = Member.dao.queryMemberById(id);
			Map<String, Object> map = new HashMap<>();
			map.put("member", m);
			map.put("accessToken", token);
			VertifyCode.dao.updateVertifyCodeExpire(mobile, now,Constants.SHORT_MESSAGE_TYPE.REGISTER);
			//保存token
			AcceessToken at = AcceessToken.dao.queryToken(id, Constants.USER_TYPE.USER_TYPE_CLIENT,dto.getPlatForm());
			boolean tokensave = false;
			if(at == null){
				tokensave = AcceessToken.dao.saveToken(id, Constants.USER_TYPE.USER_TYPE_CLIENT, token,StringUtil.checkCode(dto.getPlatForm()));
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
				AcceessToken.dao.updateToken(id, token,dto.getPlatForm());
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
		if(!StringUtil.equals(userPwd, member.getStr("userpwd"))){
			data.setMessage("对不起，密码错误");
			data.setCode(Constants.STATUS_CODE.FAIL);
			return data;
		}
		
		//保存token
		int userId = member.getInt("id");
		AcceessToken at = AcceessToken.dao.queryToken(userId, userType,platForm);
		boolean tokensave = false;
		String token = TextUtil.generateUUID();
		//更新token
		if(StringUtil.equals(platForm, Constants.PLATFORM.ANDROID)){
			AcceessToken.dao.updateToken(userId, "", Constants.PLATFORM.IOS);
		}
		if(StringUtil.equals(platForm, Constants.PLATFORM.IOS)){
			AcceessToken.dao.updateToken(userId, "", Constants.PLATFORM.ANDROID);
		}
		if(at == null){
			tokensave = AcceessToken.dao.saveToken(userId, userType, token,platForm);
			if(tokensave){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("登录成功");
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("登录失败");
			}
		}else{
			at.updateToken(userId,token,platForm);
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
	/*	if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}*/
		AcceessToken token = AcceessToken.dao.queryById(member.getInt("id"),dto.getPlatForm());
		if((token == null)  ||(!StringUtil.equals(token.getStr("token"), dto.getAccessToken()))){
			data.setCode("5701");
			data.setMessage("对不起，您的账号在另一处登录");
			return data;
		}
		
		AcceessToken.dao.updateToken(member.getInt("id"), "",dto.getPlatForm());
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("退出成功");
		return data;
	}
	
	//忘记密码，获取验证码
	public ReturnData getForgetCheckCode(LoginDTO dto){
		Member member = Member.dao.queryMember(dto.getMobile());
		String code = VertifyUtil.getVertifyCode();
		ReturnData data = new ReturnData();
		VertifyCode vc = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
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
			VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
			if(vCode == null){
				boolean isSave = VertifyCode.dao.saveVertifyCode(dto.getMobile(), dto.getUserTypeCd(), code,new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
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
				VertifyCode.dao.updateVertifyCode(dto.getMobile(), code,Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
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
		VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
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
			VertifyCode.dao.updateVertifyCodeExpire(dto.getMobile(), now,Constants.SHORT_MESSAGE_TYPE.FORGET_REGISTER_PWD);
			//保存密码
			Member.dao.updatePwd(dto.getMobile(), dto.getUserPwd());
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
		if(!StringUtil.equals(member.getStr("userPwd"), dto.getOldPwd())){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，旧密码错误");
			return data;
		}
		//保存密码
		Member.dao.updatePwd(dto.getMobile(), dto.getNewPwd());
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
		if(!StringUtil.equals(admin.getStr("password"), dto.getOldPwd())){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，旧密码错误");
			return data;
		}
		//保存密码
		Admin.dao.updatePwd(dto.getMobile(), dto.getNewPwd());
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
		List<Carousel> carousels = Carousel.dao.queryCarouselList(100, 1);
		List<CarouselVO> vos = new ArrayList<CarouselVO>();
		CarouselVO vo = null;
		Map<String, Object> map = new HashMap<>();
		//查询轮播图
		for(Carousel carousel : carousels){
			 vo = new CarouselVO();
			 vo.setImgUrl(carousel.getStr("img_url"));
			 vo.setRealUrl(carousel.getStr("real_url"));
			 vos.add(vo);
		}
		
		//判断是否绑定银行卡
		MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		if(memberBankcard != null){
			//已绑定
			String status = memberBankcard.getStr("status");
			if(StringUtil.isBlank(status)||StringUtil.equals(status, Constants.BIND_BANKCARD_STATUS.APPLING)
					||StringUtil.equals(status, Constants.BIND_BANKCARD_STATUS.APPLY_SUCCESS)){
				map.put("bindCardFlg", 1);
			}else{
				map.put("bindCardFlg", 0);
			}
		}else{
			//未绑定
			map.put("bindCardFlg", 0);
		}
		
		//是否苹果更新
		CodeMst iosUpdate = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.IOS_UPDATE_SHOW);
		if(iosUpdate != null){
			map.put("updateShowFlg", iosUpdate.getInt("data1"));
		}else{
			map.put("updateShowFlg", 0);
		}
		
		Document document = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.TRADE_CONTRACT);
		if(document != null){
			map.put("tradeContract", document.getStr("desc_url"));
		}else{
			map.put("tradeContract", "");
		}
		
		//判断是否提交绑定门店
		Store store = Store.dao.queryMemberStore(dto.getUserId());
		if(store != null){
			//已绑定
			map.put("bindStoreFlg", 1);
		}else{
			//未绑定
			map.put("bindStoreFlg", 0);
		}
		
		//app图标
		CodeMst shareLogo1 = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.APP_LOGO);
		if(shareLogo1 != null){
			map.put("appLogo", shareLogo1.getStr("data2"));
		}else{
			map.put("appLogo", null);
		}
		//客服电话
		CodeMst phone = CodeMst.dao.queryCodestByCode(Constants.PHONE.CUSTOM);
		if(phone != null){
			map.put("phone", phone.getStr("data2"));
		}else{
			map.put("phone", null);
		}
		
		//公司网址
		CodeMst url = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.NET_URL);
		if(url != null){
			map.put("netUrl", url.getStr("netUrl"));
		}else{
			map.put("netUrl", null);
		}
		
		
		//版本号
		if(StringUtil.equals(dto.getPlatForm(), Constants.PLATFORM.ANDROID)){
			SystemVersionControl svc = SystemVersionControl.dao.querySystemVersionControl(Constants.VERSION_TYPE.ANDROID);
			if(svc != null){
				map.put("version", svc.getStr("version"));
				map.put("url", svc.getStr("data1"));
			}else{
				map.put("version", null);
				map.put("url", null);
			}
		}else{
			SystemVersionControl svc = SystemVersionControl.dao.querySystemVersionControl(Constants.VERSION_TYPE.IOS);
			if(svc != null){
				map.put("version", svc.getStr("version"));
				map.put("url", svc.getStr("data2"));
			}else{
				map.put("version", null);
				map.put("url", null);
			}
		}
		map.put("shareAppUrl", "http://www.yibuwangluo.cn/zznj/h5/share.jsp?businessId="+dto.getUserId());
		
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
			nv.setShareUrl(n.getStr("content_url"));
			CodeMst shareLogo = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.APP_LOGO);
			if(shareLogo != null){
				nv.setShareLogo(shareLogo.getStr("data2"));
			}
			
			CodeMst type = CodeMst.dao.queryCodestByCode(n.getStr("news_type_cd"));
			if(type != null){
				nv.setType(type.getStr("name"));
			}
			nv.setNewsId(n.getInt("id"));
			newsVOs.add(nv);
		}
		map.put("carousel", vos);
		map.put("news", newsVOs);
		Member member = Member.dao.queryMember(dto.getMobile());
		if((member != null)&&(StringUtil.isNotBlank(member.getStr("paypwd")))){
			map.put("setPaypwdFlg", 1);
			member.set("userpwd", "");
			member.set("paypwd", "");
		}else{
			map.put("setPaypwdFlg", 0);
		}
		//设置是否是经销商用户
		if((member != null)&&(StringUtil.equals(Constants.ROLE_CD.BUSINESS_USER, member.getStr("role_cd")))){
			map.put("role", Constants.ROLE_CD.BUSINESS_USER);
		}else{
			map.put("role", Constants.ROLE_CD.NORMAL_USER);
		}

		map.put("member", member);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
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
			CodeMst shareLogo = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.APP_LOGO);
			if(shareLogo != null){
				nv.setShareLogo(shareLogo.getStr("data2"));
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
		int retId = ReceiveAddress.dao.saveInfo(ra);
		if(retId != 0){
			if(dto.getFlg()==1){
				//更新默认地址
				ReceiveAddress.dao.updateReceiveAddressDefault(retId, dto.getUserId());
			}
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
			if(dto.getFlg()==1){
				//更新默认地址
				ReceiveAddress.dao.updateReceiveAddressDefault(dto.getId(), dto.getUserId());
			}
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
			Province province = Province.dao.queryProvince(ra.getInt("province_id"));
			if(province != null){
				vo.setProvince(province.getStr("name"));
			}
			City city = City.dao.queryCity(ra.getInt("city_id"));
			if(city != null){
				vo.setCity(city.getStr("name"));
			}
			District district = District.dao.queryDistrict(ra.getInt("district_id"));
			if(district != null){
				vo.setDistrict(district.getStr("name"));
			}
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
		feedBack.set("feedback", StringUtil.checkCode(dto.getFeedBack()));
		feedBack.set("create_time", DateUtil.getNowTimestamp());
		feedBack.set("update_time", DateUtil.getNowTimestamp());
		feedBack.set("readed", 0);
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
				map.put("content", "优化App使用速度，完善部分功能");
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("当前版本已是最新版本");
				map.put("url", null);
				map.put("content", "");
			}
		}
		if(StringUtil.equals(vtc, Constants.VERSION_TYPE.IOS)){
			String version = dto.getVersion();
			if(!StringUtil.equals(dbVersion,version)){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("发现新版本："+svc.getStr("mark"));
				map.put("url", svc.getStr("data2"));
				map.put("content", "优化App使用速度，完善部分功能");
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("当前版本已是最新版本");
				map.put("url", null);
				map.put("content", "");
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
	
	//消息详情
	public ReturnData queryMessageListDetail(LoginDTO dto){
		ReturnData data = new ReturnData();
		int messageId = dto.getMessageId();
		Message message = Message.dao.queryMessageById(messageId);
		if(message == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，消息不存在");
			return data;
		}
		MessageListDetailVO vo = new MessageListDetailVO();
		JSONObject jsonObject = JSONObject.fromObject(message.get("params"));
		int orderId = StringUtil.toInteger(jsonObject.getString("id"));
		String messageTypeCd = message.getStr("message_type_cd");
		if(StringUtil.equals(messageTypeCd, Constants.MESSAGE_TYPE.SALE_TEA)){
			//卖茶记录
			OrderItem orderItem = OrderItem.dao.queryById(orderId);
			if(orderItem != null){
				//Order order = Order.dao.queryById(orderItem.getInt("order_id"));
				/*OrderItem orderItem = OrderItem.dao.queryByOrderId(order.getInt("id"));
				if(orderItem != null){*/
					WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(orderItem.getInt("wtm_item_id"));
					if(wtmItem != null){
						WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
						if(wtm != null){
							Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
							if(tea != null){
								vo.setTitle(tea.getStr("tea_title"));
							}
						}
						CodeMst size = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
						String sizeType = size == null ? "" : "/"+size.getStr("name");
						//成交总额
						vo.setBargainAmount("￥"+StringUtil.toString(orderItem.getBigDecimal("item_amount")));
						vo.setCreateTime(StringUtil.toString(orderItem.getTimestamp("create_time")));
						//支付总价
						vo.setPayAmount("￥"+StringUtil.toString(orderItem.getBigDecimal("item_amount")));
						vo.setPayTime(StringUtil.toString(orderItem.getTimestamp("create_time")));
						vo.setPrice("￥"+StringUtil.toString(wtmItem.getBigDecimal("price"))+sizeType);
						vo.setQuality(StringUtil.toString(orderItem.getInt("quality"))+size.getStr("name"));
					}
				//}
				
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("messageDetail", vo);
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
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(tea.getInt("id"),Constants.USER_TYPE.PLATFORM_USER);
			System.out.println(tea.getInt("id"));
			if(wtm != null){
				WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
				if(wtmItem != null){
					model.setStock(wtmItem.getInt("quality"));
					model.setTeaId(wtmItem.getInt("id"));
					CodeMst unit = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
					if(unit != null){
						model.setUnit(unit.getStr("name"));
					}
				}
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
		//新茶发行列表备注
		Document newTeaSaleMark = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.NEW_TEA_SALE_MARK);
		if(newTeaSaleMark != null){
			map.put("newTeaSaleMark", newTeaSaleMark.getStr("desc_url"));
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
	

	//查询新茶发售详情
	public ReturnData queryNewTeaById(LoginDTO dto){
				
		ReturnData data = new ReturnData();
		WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(dto.getId());
		if(wtmItem == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("查询失败，茶叶数据不存在");
			return data;
		}
		//WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryWarehouseTeaMember(dto.getId(), Constants.USER_TYPE.PLATFORM_USER);
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("查询失败，茶叶数据不存在");
			return data;
		}
		Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
		if(tea == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("查询失败，茶叶数据不存在");
			return data;
		}
		
		TeaDetailModelVO vo = new TeaDetailModelVO();
		CodeMst unit = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
		if(unit != null){
			vo.setUnit(unit.getStr("name"));
		}
		vo.setProductBusiness(tea.getStr("product_business"));
		String coverImgs = tea.getStr("cover_img");
		if(StringUtil.isNoneBlank(coverImgs)){
			String[] imgs = coverImgs.split(",");
			List<String> cList = new ArrayList<>();
			for(String str : imgs){
				cList.add(str);
			}
			vo.setImg(cList);
		}
		vo.setId(wtmItem.getInt("id"));
		vo.setName(tea.getStr("tea_title"));
		vo.setAmount(StringUtil.toString(tea.getInt("total_output"))+"片");
		vo.setBirthday(DateUtil.formatDateYMD((tea.getDate("product_date"))));
		vo.setBrand(tea.getStr("brand"));
		vo.setPrice("￥"+StringUtil.toString(tea.getBigDecimal("tea_price"))+"元");
		vo.setCertificateFlg(tea.getInt("certificate_flg"));
		//正品提示
		CodeMst mst = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.CERTIFICATE_TIP);
		if(mst != null){
			vo.setComment(mst.getStr("data2"));
		}
		CodeMst phoneCodeMst = CodeMst.dao.queryCodestByCode(Constants.PHONE.CUSTOM);
		if(phoneCodeMst != null){
			vo.setCustomPhone(phoneCodeMst.getStr("data2"));
		}
		vo.setDescUrl(tea.getStr("desc_url"));
		
		if(wtmItem != null){
			vo.setStock(StringUtil.toString(wtmItem.getInt("quality")));
		}
		vo.setProductPlace(tea.getStr("product_place"));
		vo.setSaleTime(DateUtil.formatDateYMD(tea.getDate("sale_from_date"))+"至"+DateUtil.formatDateYMD(tea.getDate("sale_to_date")));
		vo.setSize(tea.getInt("weight")+"克/片、"+tea.getInt("size")+"片/件");
		vo.setSize2(tea.getInt("size")+"片/件");
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
		List<OrderItem> list = OrderItem.dao.queryBuyNewTeaRecord(dto.getPageSize()
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
		for(OrderItem item : list){
			model = new RecordListModel();
			model.setType(type);
			model.setId(item.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(item.getTimestamp("create_time")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(item.getInt("wtm_item_id"));
			String status = "";
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						String size = "";
						CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
						if(sizeType != null){
							size = sizeType.getStr("name");
						}
						model.setContent(tea.getStr("tea_title")+"x"+item.getInt("quality")+size);
						model.setTea(tea.getStr("tea_title"));
						String imgs = tea.getStr("cover_img");
						if(StringUtil.isNoneBlank(imgs)){
							String[] sp = imgs.split(",");
							model.setImg(sp[0]);
						}
						CodeMst teaType = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
						if(teaType != null){
							model.setTeaType(teaType.getStr("name"));
						}
						model.setQuality(item.getInt("quality"));
						if(sizeType != null){
							model.setUnit(sizeType.getStr("name"));
						}
						
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
				}
				CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
				if(sCodeMst != null){
					status = sCodeMst.getStr("name");
				}
			}
			model.setMoneys("-"+StringUtil.toString(item.getBigDecimal("item_amount"))+" "+"买茶成功");
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
		List<SaleOrder> list = SaleOrder.dao.queryMemberSaleOrders(dto.getUserId(), dto.getPageSize(), dto.getPageNum(), dto.getDate());
		
		List<RecordListModel> models = new ArrayList<>();
		RecordListModel model = null;
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		for(SaleOrder saleOrder : list){
			model = new RecordListModel();
			model.setType(type);
			model.setId(saleOrder.getInt("id"));
			CodeMst unit = CodeMst.dao.queryCodestByCode(saleOrder.getStr("size_type_cd"));
			String unitStr = "";
			if(unit != null){
				unitStr = unit.getStr("name");
			}
			model.setDate(DateUtil.formatTimestampForDate(saleOrder.getTimestamp("create_time")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(saleOrder.getInt("wtm_item_id"));
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				String content = "";
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					int originStock = wtmItem.getInt("origin_stock") == null ? 0 :wtmItem.getInt("origin_stock");
					if(tea != null){
						content = tea.getStr("tea_title")+"x"+originStock+unitStr;
						model.setTea(tea.getStr("tea_title"));
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
					
					String price = wtmItem.getBigDecimal("price")+"/"+unitStr;
					int onSale = wtmItem.getInt("quality") ==  null ? 0 : wtmItem.getInt("quality");
					int cancleQuality = wtmItem.getInt("cancle_quality") == null ? 0 :wtmItem.getInt("cancle_quality");
					int haveSale = originStock-cancleQuality;
					
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
					if(sCodeMst != null){
						content = sCodeMst.getStr("name")+" "+content;
					}
					model.setContent(content);
					model.setMoneys("单价:￥"+price+" 已售"+haveSale+unitStr);
				}
			}
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
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			String size = "";
			CodeMst sizeType = CodeMst.dao.queryCodestByCode(record.getStr("size_type_cd"));
			if(sizeType != null){
				size = sizeType.getStr("name");
			}
			String status = "";
			CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(sCodeMst != null){
				status = sCodeMst.getStr("name");
			}
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+record.getInt("quality")+size+" "+status);
			}
			
			ReceiveAddress receiveAddress = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
			if(receiveAddress != null){
				String address = "";
				City city = City.dao.queryCity(receiveAddress.getInt("city_id"));
				if(city != null){
					address = city.getStr("name");
				}
				District district = District.dao.queryDistrict(receiveAddress.getInt("district_id"));
				if(district != null){
					address = address + district.getStr("name");
				}
				address = address + receiveAddress.getStr("address");
				model.setMoneys(address);
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
			model.setId(record.getInt("id"));
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
		List<CashJournal> list=CashJournal.dao.queryRecords(dto.getPageSize(), dto.getPageNum(), dto.getUserId(), dto.getDate());
		//List<PayRecord> list = PayRecord.dao.queryRecords(dto.getPageSize(), dto.getPageNum(), dto.getUserId(), dto.getDate());
		/*List<BankCardRecord> list = BankCardRecord.dao.queryRecords(dto.getPageSize()
																   ,dto.getPageNum()
																   ,dto.getUserId()
																   ,Constants.BANK_MANU_TYPE_CD.RECHARGE
																   ,dto.getDate());*/
		List<RecordListModel> models = new ArrayList<>();
		/*CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");*/
		RecordListModel model = null;
		for(CashJournal record : list){
			model = new RecordListModel();
			CodeMst type = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(type != null){
				model.setType(type.getStr("name"));
			}else{
				model.setType("");
			}
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys(record.getStr("remarks"));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			String statusStr = "";
			if(status != null){
				statusStr = status.getStr("name");
			}
			model.setContent("期初:"+record.getBigDecimal("opening_balance")+",余额:"+record.getBigDecimal("closing_balance"));
			models.add(model);
		}
		/*
		 * for(PayRecord record : list){
			model = new RecordListModel();
			CodeMst type = CodeMst.dao.queryCodestByCode(record.getStr("pay_type_cd"));
			if(type != null){
				model.setType(type.getStr("name"));
			}else{
				model.setType("");
			}
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys("+"+StringUtil.toString(record.getBigDecimal("moneys")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			String statusStr = "";
			if(status != null){
				statusStr = status.getStr("name");
			}
			model.setContent(statusStr+" 金额："+StringUtil.toString(record.getBigDecimal("moneys")));
			models.add(model);
		}
		 */
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
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			String st = "";
			if(status != null){
				st = status.getStr("name");
			}
			model.setMoneys("￥"+StringUtil.toString(record.getBigDecimal("moneys"))+" "+st);
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
			model.setId(record.getInt("id"));
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
		if(dto.getQuality() <= 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶数据不能为0");
			return data;
		}
		WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(dto.getTeaId());
		if(wtmItem == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据不存在");
			return data;
		}
		//判断库存
		if((wtmItem.getInt("quality")!=null)&&(dto.getQuality()>wtmItem.getInt("quality"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("添加失败，茶叶存储不足");
			return data;
		}
		BuyCart buyCart = BuyCart.dao.queryBuycart(dto.getUserId(), dto.getTeaId());
		if((buyCart!=null)&&(buyCart.getInt("quality")!=null)&&((buyCart.getInt("quality")+dto.getQuality())>wtmItem.getInt("quality"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("添加失败，已添加的购物车数量大于库存数");
			return data;
		}
		
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据不存在");
			return data;
		}
		int memberId = wtm.getInt("member_id");
		String memberUserCd = wtm.getStr("member_type_cd");
		cart.set("warehouse_tea_member_item_id", dto.getTeaId());
		cart.set("quality", dto.getQuality());
		cart.set("status", Constants.ORDER_STATUS.SHOPPING_CART);
		cart.set("create_time", DateUtil.getNowTimestamp());
		cart.set("update_time", DateUtil.getNowTimestamp());
		cart.set("member_id", dto.getUserId());
		cart.set("sale_id", memberId);
		cart.set("sale_user_type", memberUserCd);
		cart.set("size", wtmItem.getStr("size_type_cd"));
		
		//判断购物中是否有此茶叶
		boolean save = false;
		Long count = BuyCart.dao.queryBuycartExist(dto.getUserId(), dto.getTeaId());
		if(count.intValue() == 0){
			//不存在，就添加
			save = BuyCart.dao.saveInfo(cart);
		}else{
			//存在
			int ret = BuyCart.dao.updateStock(dto.getUserId(), dto.getTeaId(), dto.getQuality());
			if(ret != 0){
				save = true;
			}
		}
		
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
			WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryByKeyId(cart.getInt("warehouse_tea_member_item_id"));
			WarehouseTeaMember wtm = null;
			if(item != null){
				wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			}else{
				continue;
			}
			
			if(wtm != null){
				WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
				if(house != null){
					vo.setWarehouse(house.getStr("warehouse_name"));
				}else{
					vo.setWarehouse(StringUtil.STRING_BLANK);
				}
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if(tea != null){
					vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
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
				
				//WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryById(wtm.getInt("id"));
				if(item != null){
					vo.setPrice(item.getBigDecimal("price"));
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
		List<BuyTeaListVO> list = new ArrayList<>();
		BuyTeaListVO teaVo = null;
		List<Tea> teaList = Tea.dao.queryBuyTeaList(dto.getPageSize(), dto.getPageNum(),dto.getName(),Constants.NEWTEA_STATUS.END);
		for(Tea tea : teaList){
			teaVo = new BuyTeaListVO();
			teaVo.setId(tea.getInt("id"));
			teaVo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
			teaVo.setName(tea.getStr("tea_title"));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				teaVo.setType(type.getStr("name"));
			}
			TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(teaVo.getId());
			if(teaPrice != null){
				teaVo.setPrice(StringUtil.toString(teaPrice.getBigDecimal("reference_price").multiply(new BigDecimal(tea.getInt("size")))));
			}
			teaVo.setSize("件");
			list.add(teaVo);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("data", list);
		data.setData(map);
		return data;
	}
	
	public ReturnData queryTeaByIdList(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		String priceFlg = dto.getPriceType();
		int wareHouseId = dto.getWareHouseId();
		int quality = dto.getQuality();
		
		String size = dto.getSize();
		Tea tea1 = Tea.dao.queryById(dto.getTeaId());
		if(tea1 == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶产品不存在");
			return data;
		}
		
		List<WarehouseTeaMemberItem> list = WarehouseTeaMemberItem.dao.queryTeaByIdList(dto.getTeaId()
																					   ,size
																					   ,priceFlg
																					   ,wareHouseId
																					   ,quality
																					   ,dto.getPageSize()
																					   ,dto.getPageNum()
																					   ,dto.getUserId());
		 
		List<SelectSizeTeaListVO> vos = new ArrayList<>();
		SelectSizeTeaListVO vo = null;
		for(WarehouseTeaMemberItem item : list){
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			if(wtm == null){
				continue;
			}
			vo = new SelectSizeTeaListVO();
			vo.setId(item.getInt("id"));
			Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
			if(tea != null){
				vo.setName(tea.getStr("tea_title"));
				vo.setPrice(StringUtil.toString(item.getBigDecimal("price")));
				vo.setStock(StringUtil.toString(item.getInt("quality")));
				
				CodeMst t = CodeMst.dao.queryCodestByCode(size);
				if(t != null){
					vo.setSize(t.getStr("name"));
				}
				vo.setSizeNum(tea.getInt("size"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					vo.setType(type.getStr("name"));
				}
				WareHouse wareHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
				if(wareHouse != null){
					vo.setWareHouse(wareHouse.getStr("warehouse_name"));
				}
				vos.add(vo);
			}
		}
		
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String, Object> map = new HashMap<>();
		map.put("data", vos);
		//查询详情
		map.put("descUrl", tea1.getStr("desc_url"));
		data.setData(map);
		return data;
	}
	
	//分析
	public ReturnData queryTeaAnalysis(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		//详细数据
		Map<String, OrderAnalysisVO> map1 = new HashMap<>();
		//价格走势,参考价
		Map<String, DataListVO> referencePriceLists = new HashMap<>();
		//成交走势
		Map<String, DataListVO> map3 = new HashMap<>();
		
		List<String> allMonthDays = DateUtil.getMonthFullDayByNum(30);
		String fromDate = allMonthDays.get(0);
		String toDate = allMonthDays.get(allMonthDays.size()-1);
		OrderAnalysisVO nullVO = null;
		DataListVO ckDataListVo = null;
		DataListVO nullItemModel = null;
		Calendar c1 = Calendar.getInstance();
    	c1.setTime(new Date());
		c1.add(c1.DATE, -30);
		String from = DateUtil.format(c1.getTime(),"yyyy-MM-dd");
		String fromTime = from+" 00:00:00";
		
		Calendar c2 = Calendar.getInstance();
    	c2.setTime(new Date());
		c2.add(c2.DATE, 0);
		String to = DateUtil.format(c2.getTime(),"yyyy-MM-dd");
		String toTime = to+" 23:59:59";
		TeaPrice firstTeaPrice = TeaPrice.dao.queryFirstByTeaId(dto.getTeaId(), fromTime);
		BigDecimal firstPrice = new BigDecimal("0");
		if(firstTeaPrice != null){
			firstPrice = firstTeaPrice.getBigDecimal("reference_price") == null ? new BigDecimal("0") : firstTeaPrice.getBigDecimal("reference_price");
		}
		for(String str : allMonthDays){
			nullVO = new OrderAnalysisVO();
			nullVO.setAmount(new BigDecimal("0.00"));
			nullVO.setDate(str);
			nullVO.setQuality(0);
			
			ckDataListVo = new DataListVO();
			ckDataListVo.setKey(str);
			//所有默认起始时间的参考价
			ckDataListVo.setValue(firstPrice);
			
			nullItemModel = new DataListVO();
			nullItemModel.setValue(new BigDecimal("0.00"));
			nullItemModel.setKey(str);
			
			map1.put(str, nullVO);
			referencePriceLists.put(str, ckDataListVo);
			map3.put(str, nullItemModel);
		}
		
		//价格走势，就是参考价
		Tea tea = Tea.dao.queryById(dto.getTeaId());
		List<Record> teaPrices = TeaPrice.dao.queryForDisplay(fromTime, toTime, tea.getInt("id"));
		for(Record record : teaPrices){	
			String createTime = record.getStr("createTime");
			BigDecimal p = record.getBigDecimal("price");
			for(String k:referencePriceLists.keySet()){
				DataListVO dataListVO = referencePriceLists.get(k);
				if((k.compareTo(createTime)>=0)){
					dataListVO.setValue(p);
					referencePriceLists.put(k, dataListVO);
				}
			}
		}
		if(teaPrices.size()!=0){
			//找到最后一个
			Record last = teaPrices.get(teaPrices.size()-1);
			String createTime = last.getStr("createTime");
			BigDecimal p = last.getBigDecimal("price");
			for(String k:referencePriceLists.keySet()){
				if(k.compareTo(createTime)>0){
					DataListVO dataListVO = referencePriceLists.get(k);
					dataListVO.setValue(p);
					referencePriceLists.put(k, dataListVO);
				}
			}
		}
		
		List<DataListVO> list1 = new ArrayList<>();
		for(String k:referencePriceLists.keySet()){
			DataListVO vs = referencePriceLists.get(k);
			list1.add(vs);
		}
		
		//成交走势,详细数据,从t_order_item拿,先算片
		List<Record> records = Order.dao.queryBargainTrendAvg(fromDate,toDate,dto.getTeaId());
		List<OrderAnalysisVO> vos = new ArrayList<>();
		
		List<DataListVO> models = new ArrayList<>();
		Map<String, String> initMap = new HashMap<>();
		for(Record record : records){
			//循环按日期筛选的数据，分组按片按件和按日期
			String sizeTypeCd = record.getStr("sizeType");
			BigDecimal bprice = new BigDecimal("0");
			String dateStr = record.getStr("createTime");
			BigDecimal allPiecequality = new BigDecimal("0");
			if(StringUtil.equals(sizeTypeCd, Constants.TEA_UNIT.PIECE)){
				if(initMap.containsKey(dateStr)){
					continue;
				}
				//如果是片
				BigDecimal allPieceAmount = (record.getBigDecimal("allAmount") == null ? new BigDecimal("0.00") : record.getBigDecimal("allAmount"));
				allPiecequality = (record.getBigDecimal("quality") == null? new BigDecimal("0.00") : record.getBigDecimal("quality"));
				//判断是否还有按件的
				for(Record record1 : records){
					if((StringUtil.equals(dateStr, record1.getStr("createTime")))
							&&(StringUtil.equals(record1.getStr("sizeType"), Constants.TEA_UNIT.ITEM))){
						//如果是件
						BigDecimal	allItemAmount = record1.getBigDecimal("allAmount");
						BigDecimal	allItemQuality = new BigDecimal(tea.getInt("size")).multiply(record1.getBigDecimal("quality"));
						allPieceAmount = allPieceAmount.add(allItemAmount);
						allPiecequality = allPiecequality.add(allItemQuality);
						break;
					}
				}
				if(allPieceAmount != null){
					if(allPiecequality != null){
						bprice = allPieceAmount.divide(allPiecequality,10,BigDecimal.ROUND_HALF_DOWN);
					}
				}
				initMap.put(dateStr, dateStr);
			}
			
			if(StringUtil.equals(sizeTypeCd, Constants.TEA_UNIT.ITEM)){
				if(initMap.containsKey(dateStr)){
					continue;
				}
				//如果是按件
				BigDecimal allPieceAmount = (record.getBigDecimal("allAmount") == null ? new BigDecimal("0.00") : record.getBigDecimal("allAmount"));
				allPiecequality = (record.getBigDecimal("quality") == null? new BigDecimal("0.00") : new BigDecimal(tea.getInt("size")).multiply(record.getBigDecimal("quality")));
				//判断是否还有按片的
				for(Record record1 : records){
					if((StringUtil.equals(dateStr, record1.getStr("createTime")))
							&&(StringUtil.equals(record1.getStr("sizeType"), Constants.TEA_UNIT.PIECE))){
						//如果是件
						BigDecimal	allItemAmount = record1.getBigDecimal("allAmount");
						BigDecimal	allItemQuality = record1.getBigDecimal("quality");
						allPieceAmount = allPieceAmount.add(allItemAmount);
						allPiecequality = allPiecequality.add(allItemQuality);
						break;
					}
				}
				if(allPieceAmount != null){
					if(allPiecequality != null){
						bprice = allPieceAmount.divide(allPiecequality,10,BigDecimal.ROUND_HALF_DOWN);
					}
				}
				initMap.put(dateStr, dateStr);
			}
			
			/*
			String dateStr = record.getStr("createTime");
			BigDecimal allPieceAmount = (record.getBigDecimal("allAmount") == null ? new BigDecimal("0.00") : record.getBigDecimal("allAmount"));
			BigDecimal allPiecequality = (record.getBigDecimal("quality") == null? new BigDecimal("0.00") : record.getBigDecimal("quality"));
			//算件
			List<Record> itemRecords = Order.dao.queryBargainTrendAvgByDate(dateStr,dto.getTeaId(),Constants.TEA_UNIT.ITEM);
			BigDecimal allItemAmount = new BigDecimal("0");
			BigDecimal allItemQuality = new BigDecimal("0");
			if(itemRecords.size() != 0){
				Record itemRecord = itemRecords.get(0);
				allItemAmount = itemRecord.getBigDecimal("allAmount");
				allItemQuality = new BigDecimal(tea.getInt("size")).multiply(itemRecord.getBigDecimal("quality"));
			}
			allPieceAmount = allPieceAmount.add(allItemAmount);
			allPiecequality = allPiecequality.add(allItemQuality);
			
			BigDecimal bprice = new BigDecimal("0");
			if(allPieceAmount != null){
				if(allPiecequality != null){
					bprice = allPieceAmount.divide(allPiecequality,10,BigDecimal.ROUND_HALF_DOWN);
				}
			}*/
			
			DataListVO iModel = map3.get(dateStr);
			if(iModel == null){
				iModel = new DataListVO();
				iModel.setKey(dateStr);
			}
			if(bprice == null){
				iModel.setValue(new BigDecimal("0.00"));
			}else{
				iModel.setValue(bprice.setScale(2,BigDecimal.ROUND_UP));
			}
			
			map3.put(dateStr, iModel);
			
			OrderAnalysisVO vo2 = map1.get(dateStr);
			if(vo2 == null){
				vo2 = new OrderAnalysisVO();
				vo2.setDate(dateStr);
			}
			
			if(bprice == null){
				vo2.setAmount(new BigDecimal("0.00"));
			}else{
				vo2.setAmount(bprice.setScale(2,BigDecimal.ROUND_UP));
			}
			vo2.setQuality(StringUtil.toInteger(StringUtil.toString(allPiecequality)));
			map1.put(dateStr, vo2);
		}
		
		for(String k:map3.keySet()){
			models.add(map3.get(k));
		}
		for(String k:map1.keySet()){
			vos.add(map1.get(k));
		}
		
		//详细数据
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String,Object> map = new HashMap<>();
		Collections.sort(vos);
		map.put("data", vos);
		
		//查询成交总量和成交总额OK
		//片
		List<Record> records2 = Order.dao.queryBargainSum(fromDate,toDate, dto.getTeaId(),Constants.TEA_UNIT.PIECE);
		if((records2 != null)&&(records2.size() != 0)){
			Record record0 = records2.get(0);
			map.put("allQuality", record0.getBigDecimal("quality"));
			map.put("allAmount", record0.getBigDecimal("amount"));
		}
		
		//件
		List<Record> records3 = Order.dao.queryBargainSum(fromDate,toDate, dto.getTeaId(),Constants.TEA_UNIT.ITEM);
		if((records3 != null)&&(records3.size() != 0)){
			Record record0 = records3.get(0);
			
			BigDecimal allQuality = (BigDecimal)map.get("allQuality");
			if(allQuality != null){
				map.put("allQuality", allQuality.add(record0.getBigDecimal("quality").multiply(new BigDecimal(tea.getInt("size")))));
			}else{
				map.put("allQuality", record0.getBigDecimal("quality").multiply(new BigDecimal(tea.getInt("size"))));
			}
			BigDecimal allAmount = (BigDecimal)map.get("allAmount");
			if(allAmount != null){
				map.put("allAmount",allAmount.add(record0.getBigDecimal("amount")));
			}else{
				map.put("allAmount", record0.getBigDecimal("amount"));
			}
		}
		
		Collections.sort(list1);
		Collections.sort(models);
		map.put("priceTrend",list1);
		map.put("bargainTrend", models);
		data.setData(map);
		return data;
	}
	
	//选择规格
	public ReturnData queryTeaSize(LoginDTO dto){
		ReturnData data = new ReturnData();
		int wtmItemId = dto.getTeaId();
		//String sizeTypeCd = dto.getSize();
		
		if(wtmItemId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶不存在");
			return data;
		}
		WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
		if(item == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶不存在");
			return data;
		}
		System.out.println(item.getInt("warehouse_tea_member_id"));
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，数据出错");
			return data;
		}
		Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
		SelectSizeTeaListVO vo = new SelectSizeTeaListVO();
		vo.setId(wtmItemId);
		if(tea != null){
			vo.setName(tea.getStr("tea_title"));
			vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
			vo.setStock(StringUtil.toString(item.getInt("quality")));
			
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				vo.setType(type.getStr("name"));
			}
		}
		String sizeTypeCd = item.getStr("size_type_cd");
		vo.setPrice(StringUtil.toString(item.getBigDecimal("price")));
		if(StringUtil.equals(sizeTypeCd, Constants.TEA_UNIT.PIECE)){
			vo.setSize("片");
		}else{
			vo.setSize("件");
		}
		WareHouse wareHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
		if(wareHouse != null){
			vo.setWareHouse(wareHouse.getStr("warehouse_name"));
		}
		Document document = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.WAREHOUSE_INTRODUCE);
		if(document != null){
			vo.setWareHouseMarkUrl(document.getStr("desc_url"));
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("tea", vo);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//茶资产
	public ReturnData queryTeaProperty(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		//查看有几种茶叶
		List<Integer> list = WarehouseTeaMember.dao.queryPersonTeaId(dto.getUserId()
																	,dto.getPageSize()
																	,dto.getPageNum());
		
		List<TeaPropertyListVO> vos = new ArrayList<>();
		TeaPropertyListVO vo = null;
		for(Integer teaId : list){
			vo = new TeaPropertyListVO();
			Tea tea = Tea.dao.queryById(teaId);
			vo.setTeaId(teaId);
			if(tea != null){
				vo.setName(tea.getStr("tea_title"));
				vo.setImg(tea.getStr("icon"));
				int stock = WarehouseTeaMember.dao.queryTeaStock(dto.getUserId(), teaId,Constants.USER_TYPE.USER_TYPE_CLIENT).intValue();
				vo.setStock(StringUtil.toString(stock));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					vo.setType(type.getStr("name"));
				}
				vo.setSize("片");
			}
			vos.add(vo);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("tea", vos);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data; 
	}
	
	//仓储详情
	public ReturnData queryWareHouseDetail(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		Map<String, Object> map = new HashMap<>();
		int teaId = dto.getTeaId();
		TeaPropertyListVO vo = new TeaPropertyListVO();
		Tea tea = Tea.dao.queryById(teaId);
		vo.setTeaId(teaId);
		if(tea != null){
			vo.setName(tea.getStr("tea_title"));
			vo.setImg(tea.getStr("icon"));
			int stock = WarehouseTeaMember.dao.queryTeaStock(dto.getUserId(), teaId,Constants.USER_TYPE.USER_TYPE_CLIENT).intValue();
			vo.setStock(StringUtil.toString(stock));
			CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
			if(type != null){
				vo.setType(type.getStr("name"));
			}
			vo.setSize("片");
		}
		
		map.put("tea", vo);
		
		List<TeaWarehouseDetailVO> vos = new ArrayList<>();
		TeaWarehouseDetailVO detailVO = null;
		List<WarehouseTeaMember> lists = WarehouseTeaMember.dao.queryPersonWarehouseTea(dto.getUserId(),Constants.USER_TYPE.USER_TYPE_CLIENT,teaId);
		for(WarehouseTeaMember wtm : lists){
			
			detailVO = new TeaWarehouseDetailVO();
			detailVO.setWareHouseTeaId(wtm.getInt("id"));
			int stock = wtm.getInt("stock");
			
			detailVO.setCanGetQuality(stock);
			
			WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
			if(house != null){
				detailVO.setWareHouse(house.getStr("warehouse_name"));
				BigDecimal pieceDecimal = WarehouseTeaMemberItem.dao.queryOnSaleTeaCount(dto.getUserId(), wtm.getInt("warehouse_id"), teaId, Constants.TEA_UNIT.PIECE);
				int piece = 0;
				int item = 0;
				if(pieceDecimal != null){
					piece = pieceDecimal.intValue();
				}
				BigDecimal itemDecimal = WarehouseTeaMemberItem.dao.queryOnSaleTeaCount(dto.getUserId(), wtm.getInt("warehouse_id"), teaId, Constants.TEA_UNIT.ITEM);
				if(itemDecimal != null){
					item = itemDecimal.intValue();
				}
				int size = tea.getInt("size");
				detailVO.setSaleQuality(piece+item*size);
			}
			vos.add(detailVO);
		}
		
		map.put("warehouse", vos);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//我要卖茶
	public ReturnData saleTea(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		List<WarehouseStockVO> vos = new ArrayList<>();
		WarehouseStockVO vo = new WarehouseStockVO();
		WarehouseTeaMember wtmMember = WarehouseTeaMember.dao.queryById(dto.getTeaId());
		if(wtmMember == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错");
			return data;
		}
		Tea tea = Tea.dao.queryById(wtmMember.getInt("tea_id"));
		int size = 0;
		if(tea != null){
			if(StringUtil.equals(tea.getStr("status"), Constants.NEWTEA_STATUS.ON_SALE)){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("新茶发行中，暂停上架销售");
				return data;
			}
			if(StringUtil.equals(tea.getStr("status"), Constants.NEWTEA_STATUS.STAY_SALE)){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("新茶待售中，暂无上架销售");
				return data;
			}
			size = tea.getInt("size");
			vo.setName(tea.getStr("tea_title"));
			vo.setSize(size);
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错");
			return data;
		}
		List<WarehouseTeaMember> lists = WarehouseTeaMember.dao.querysaleTeaWarehouseTea(dto.getUserId(),tea.getInt("id"),Constants.USER_TYPE.USER_TYPE_CLIENT);
		for(WarehouseTeaMember wtm : lists){
			int stock = wtm.getInt("stock");
			vo.setStock(stock);
			vo.setWarehouseTeaId(wtm.getInt("id"));
			WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
			if(house != null){
				vo.setWareHouse(house.getStr("warehouse_name"));
			}
			if(size != 0){
				int pieceCount = stock;
				int itemCount = stock/size;
				vo.setMaxItem(itemCount);
				vo.setMaxPiece(pieceCount);
				vo.setPieceFlg(pieceCount == 0 ? 0 : 1);
				vo.setItemFlg(itemCount == 0 ? 0 : 1);
			}
			vos.add(vo);
		}
		CodeMst serviceFee = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.SALE_SERVICE_FEE);
		String serviceFeeStr = "";
		String serviceFeePoint = "";
		if(serviceFee != null){
			serviceFeeStr = serviceFee.getStr("data2");
			serviceFeePoint = serviceFee.getStr("data3");
		}
		//参考价
		TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(tea.getInt("id"));
		ReferencePriceModel itemModel = new ReferencePriceModel();
		ReferencePriceModel pieceModel = new ReferencePriceModel();
		
		String pointStr = "";
		CodeMst referencePoint = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.REFERENCE_PRICE_DISCOUNT);
		if(referencePoint != null){
			pointStr = referencePoint.getStr("data2");
		}
		
		if(teaPrice != null){
			
			BigDecimal pieceReferencePrice = teaPrice.getBigDecimal("reference_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("reference_price");
			BigDecimal pieceFromPrice = teaPrice.getBigDecimal("from_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("from_price");
			BigDecimal pieceToPrice = teaPrice.getBigDecimal("to_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("to_price");
			
			//pieceModel.setPriceStr(pieceFromPrice+"元/片-"+pieceToPrice+"元/片");
			pieceModel.setPriceStr(pieceReferencePrice+"元/片 "+",不少于参考价"+pointStr);
			pieceModel.setSizeTypeCd(Constants.TEA_UNIT.PIECE);
			
			BigDecimal itemFromPrice = new BigDecimal("0");
			BigDecimal itemToPrice = new BigDecimal("0");
			BigDecimal itemReferencePrice = new BigDecimal("0");
			if(pieceReferencePrice.compareTo(new BigDecimal("0"))==1){
				itemReferencePrice = pieceReferencePrice.multiply(new BigDecimal(tea.getInt("size")));
			}
			if(pieceFromPrice.compareTo(new BigDecimal("0"))==1){
				itemFromPrice = pieceFromPrice.multiply(new BigDecimal(tea.getInt("size")));
			}
			if(pieceToPrice.compareTo(new BigDecimal("0"))==1){
				itemToPrice = pieceToPrice.multiply(new BigDecimal(tea.getInt("size")));
			}
			//itemModel.setPriceStr(itemFromPrice+"元/件-"+itemToPrice+"元/件");
			itemModel.setPriceStr(itemReferencePrice+"元/件 "+",不少于参考价"+pointStr);
			itemModel.setSizeTypeCd(Constants.TEA_UNIT.ITEM);
		}else{
			pieceModel.setPriceStr("暂无参考价");
			pieceModel.setSizeTypeCd(Constants.TEA_UNIT.PIECE);
			itemModel.setPriceStr("暂无参考价");
			itemModel.setSizeTypeCd(Constants.TEA_UNIT.ITEM);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("warehouseTeaStock", vos);
		map.put("serviceFeeStr", serviceFeeStr);
		map.put("itemReferencePrice", itemModel);
		map.put("pieceReferencePrice", pieceModel);
		map.put("serviceFee", serviceFeePoint);
		WarehouseTeaMemberItem platTea = WarehouseTeaMemberItem.dao.queryTeaOnPlatform(Constants.USER_TYPE.PLATFORM_USER, tea.getInt("id"));
		if(platTea == null){
			map.put("price", 0);
		}else{
			map.put("price", platTea.getBigDecimal("price"));
		}
		
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//提交卖茶明细
	public ReturnData confirmSaleTea(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		int warehouseMemberTeaId = dto.getWareHouseId();
		String saleType = dto.getType();
		BigDecimal salePrice = dto.getPrice();
		int saleNum = dto.getQuality();
		int saleAllPiece = 0;
		//判断用户账号金额是否满足1%服务费
		int userId = dto.getUserId();
		Member member = Member.dao.queryById(userId);
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户数据出错");
			return data;
		}
		if(saleNum <= 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，出售数据必须大于0");
			return data;
		}
		if(salePrice.compareTo(new BigDecimal("0"))<1){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，出售价格必须大于0");
			return data;
		}
		BigDecimal userAmount = member.getBigDecimal("moneys");
		BigDecimal onePoint = new BigDecimal("0.01");
		/*BigDecimal serviceFee = onePoint.multiply(salePrice.multiply(new BigDecimal(saleNum)));
		if(userAmount.compareTo(serviceFee)==-1){
			//余额不足
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您的账号余额不足，不足以支付1%服务费，请先充值");
			return data;
		}*/
		
		if(warehouseMemberTeaId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶数据出错");
			return data;
		}
		//判断库存够不够？
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(warehouseMemberTeaId);
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		if(StringUtil.isBlank(saleType)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，请选择出售方式");
			return data;
		}
		//获取茶叶
		Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
		if(tea == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		int size = tea.getInt("size");
		int stock = wtm.getInt("stock");
		if(StringUtil.equals(saleType, Constants.TEA_UNIT.PIECE)){
			//按片
			saleAllPiece = saleNum;
			if (stock<saleNum) {
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，你销售片数大于库存数");
				return data;
			}
			
			//判断出售价格要在参考价范围内
			TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(tea.getInt("id"));
			
			if(teaPrice != null){
				BigDecimal point = new BigDecimal("1");
				BigDecimal maxPoint = new BigDecimal("1");
				CodeMst referencePoint = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.REFERENCE_PRICE_DISCOUNT);
				if(referencePoint != null){
					String pointStr = referencePoint.getStr("data3");
					point = StringUtil.toBigDecimal(pointStr);
				}
				
				CodeMst referenceMaxPoint = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.REFERENCE_PRICE_MAXDISCOUNT);
				if(referenceMaxPoint != null){
					String pointStr = referenceMaxPoint.getStr("data3");
					maxPoint = StringUtil.toBigDecimal(pointStr);
				}
				
				//BigDecimal pieceFromPrice = teaPrice.getBigDecimal("from_price");
				//按件参考价
				BigDecimal referencePrice = teaPrice.getBigDecimal("reference_price");
				/*BigDecimal piecePrice = new BigDecimal("0");
				if(referencePrice != null){
					piecePrice = referencePrice.divide(new BigDecimal(tea.getInt("size")));
				}*/
				//BigDecimal pieceToPrice = teaPrice.getBigDecimal("to_price");
				//if((salePrice.compareTo(pieceFromPrice)==-1)||(salePrice.compareTo(pieceToPrice))==1){
				if((referencePrice != null)&&(salePrice.compareTo(referencePrice.multiply(point))==-1)){
					//最低
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价不能低于参考价的"+referencePoint.getStr("data2"));
					return data;
				}
				if((referencePrice != null)&&(salePrice.compareTo(referencePrice.multiply(maxPoint))==1)){
					//最高
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价不能超过参考价的"+referenceMaxPoint.getStr("data2"));
					return data;
				}
			}
		}
		if(StringUtil.equals(saleType, Constants.TEA_UNIT.ITEM)){
			//按件
			int itemNum = stock/size;
			saleAllPiece = saleNum*size;
			if (itemNum<saleNum){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，你销售件数大于库存数");
				return data;
			}
			
			//判断出售价格必须在参考价范围内
			TeaPrice teaPrice = TeaPrice.dao.queryByTeaId(tea.getInt("id"));
			if(teaPrice != null){
				
				BigDecimal point = new BigDecimal("1");
				BigDecimal maxPoint = new BigDecimal("1");
				CodeMst referencePoint = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.REFERENCE_PRICE_DISCOUNT);
				if(referencePoint != null){
					String pointStr = referencePoint.getStr("data3");
					point = StringUtil.toBigDecimal(pointStr);
				}
				
				CodeMst referenceMaxPoint = CodeMst.dao.queryCodestByCode(Constants.SYSTEM_CONSTANTS.REFERENCE_PRICE_MAXDISCOUNT);
				if(referenceMaxPoint != null){
					String pointStr = referenceMaxPoint.getStr("data3");
					maxPoint = StringUtil.toBigDecimal(pointStr);
				}
				
			//	BigDecimal itemFromPrice = teaPrice.getBigDecimal("from_price").multiply(new BigDecimal(tea.getInt("size")));
				//BigDecimal itemToPrice = teaPrice.getBigDecimal("to_price").multiply(new BigDecimal(tea.getInt("size")));
				BigDecimal itemReferencePrice = teaPrice.getBigDecimal("reference_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("reference_price");
				itemReferencePrice = itemReferencePrice.multiply(new BigDecimal(tea.getInt("size")));
				//if((salePrice.compareTo(itemFromPrice)==-1)||(salePrice.compareTo(itemToPrice))==1){
				if(salePrice.compareTo(itemReferencePrice.multiply(point))==-1){
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价不能低于参考价的"+referencePoint.getStr("data2"));
					return data;
				}
				if(salePrice.compareTo(itemReferencePrice.multiply(maxPoint))==1){
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价不能超过参考价的"+referenceMaxPoint.getStr("data2"));
					return data;
				}
			}
		}
		
			//减少库存
			int ret = WarehouseTeaMember.dao.cutTeaQuality(saleAllPiece, wtm.getInt("warehouse_id"), wtm.getInt("tea_id"), wtm.getInt("member_id"));
			if(ret != 0){
				WarehouseTeaMemberItem wtmItem = new WarehouseTeaMemberItem();
				wtmItem.set("warehouse_tea_member_id", warehouseMemberTeaId);
				wtmItem.set("price", salePrice);
				wtmItem.set("status", Constants.TEA_STATUS.ON_SALE);
				wtmItem.set("quality", saleNum);
				wtmItem.set("size_type_cd", saleType);
				wtmItem.set("create_time", DateUtil.getNowTimestamp());
				wtmItem.set("update_time", DateUtil.getNowTimestamp());
				wtmItem.set("origin_stock", saleNum);
				wtmItem.set("cancle_quality", 0);
				int retId = WarehouseTeaMemberItem.dao.saveItemInfo(wtmItem);
				if(retId != 0){
					SaleOrder order = new SaleOrder();
					order.set("warehouse_tea_member_id", warehouseMemberTeaId);
					order.set("wtm_item_id", retId);
					order.set("quality", saleNum);
					order.set("price", salePrice);
					order.set("size_type_cd", saleType);
					order.set("create_time", DateUtil.getNowTimestamp());
					order.set("update_time", DateUtil.getNowTimestamp());
					order.set("status", Constants.ORDER_STATUS.ON_SALE);
					order.set("order_no", StringUtil.getOrderNo());
					boolean save = SaleOrder.dao.saveInfo(order);
					if(save){
						//扣1%服务费
						/*int rets = Member.dao.updateMoneys(userId, userAmount.subtract(serviceFee));
						if(rets != 0){
							//保存记录
							ServiceFee fee = new ServiceFee();
							fee.set("wtm_id", wtm.getInt("id"));
							fee.set("price", salePrice);
							fee.set("quality", saleNum);
							fee.set("size_type_cd", saleType);
							fee.set("service_fee", serviceFee);
							fee.set("mark", "服务费："+serviceFee);
							fee.set("create_time", DateUtil.getNowTimestamp());
							fee.set("update_time", DateUtil.getNowTimestamp());
							boolean saveFlg = ServiceFee.dao.saveInfo(fee);
							if(saveFlg){
								data.setCode(Constants.STATUS_CODE.SUCCESS);
								data.setMessage("卖茶成功");
							}else{
								data.setCode(Constants.STATUS_CODE.FAIL);
								data.setMessage("卖茶失败");
							}
							
						}else{
							data.setCode(Constants.STATUS_CODE.FAIL);
							data.setMessage("卖茶失败");
						}*/
						data.setCode(Constants.STATUS_CODE.SUCCESS);
						data.setMessage("卖茶成功");
					}else{
						data.setCode(Constants.STATUS_CODE.FAIL);
						data.setMessage("卖茶失败");
					}
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("卖茶失败");
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("卖茶失败");
			}
		
		return data;
	}
	
	//取茶初始化
	public ReturnData takeTeaInit(LoginDTO dto){
			
		ReturnData data = new ReturnData();
		int warehouseMemberTeaId = dto.getTeaId();
		//int quality = dto.getQuality();
		if(warehouseMemberTeaId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶数据出错");
			return data;
		}
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(warehouseMemberTeaId);
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		//获取茶叶
		Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
		if(tea == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		int stock = wtm.getInt("stock");
		Map<String, Object> map = new HashMap<>();
		map.put("stock", stock);
		//获取默认的地址
		ReceiveAddress address = ReceiveAddress.dao.queryByFirstAddress(dto.getUserId(), Constants.COMMON_STATUS.NORMAL);
		ChooseAddressVO vo = new ChooseAddressVO();
		if(address != null){
			vo.setAddress(address.getStr("address"));
			vo.setAddressId(address.getInt("id"));
			vo.setMobile(address.getStr("mobile"));
			vo.setReceiverMan(address.getStr("receiveman_name"));
		}
		map.put("address", vo);
		String url = "";
		Document document = Document.dao.queryByTypeCd(Constants.DOCUMENT_TYPE.TEA_PACKAGE_FEE_STANDARD);
		if(document != null){
			url = document.getStr("desc_url");
		}
		map.put("descUrl", url);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//取茶
	@Transient
	public ReturnData takeTea(LoginDTO dto){
				
		ReturnData data = new ReturnData();
		int warehouseMemberTeaId = dto.getTeaId();
		int quality = dto.getQuality();
		int addressId = dto.getAddressId();
		if(warehouseMemberTeaId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶数据出错");
			return data;
		}
		if(addressId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，邮寄地址不能为空");
			return data;
		}
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(warehouseMemberTeaId);
		if(wtm == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		//获取茶叶
		Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
		if(tea == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶不存在");
			return data;
		}
		int stock = wtm.getInt("stock");
		if(quality>stock){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，库存不足"+quality+"片");
			return data;
		}
		
		GetTeaRecord record = new GetTeaRecord();
		record.set("warehouse_id", wtm.getInt("warehouse_id"));
		record.set("tea_id", wtm.getInt("tea_id"));
		record.set("quality", quality);
		record.set("member_id", wtm.getInt("member_id"));
		record.set("warehouse_fee", new BigDecimal("0"));
		record.set("create_time", DateUtil.getNowTimestamp());
		record.set("update_time", DateUtil.getNowTimestamp());
		record.set("address_id", addressId);
		record.set("status", Constants.TAKE_TEA_STATUS.APPLING);
		record.set("size_type_cd", Constants.TEA_UNIT.PIECE);
		record.set("invoice_status", Constants.INVOICE_STATUS.NOT_INVOICE);
		boolean save = GetTeaRecord.dao.saveInfo(record);
		if(save){
			WarehouseTeaMember warehouseTeaMember = new WarehouseTeaMember();
			warehouseTeaMember.set("id", warehouseMemberTeaId);
			warehouseTeaMember.set("stock", stock-quality);
			warehouseTeaMember.set("update_time", DateUtil.getNowTimestamp());
			boolean update = WarehouseTeaMember.dao.updateInfo(warehouseTeaMember);
			if(update){
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				data.setMessage("取茶成功，等待平台邮寄");
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("取茶失败");
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("取茶失败");
		}
		return data;
	}
	
	//使用帮助、协议及合同
	public ReturnData getDocumentList(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<Document> list = Document.dao.queryDocumentListByTypeCd(dto.getType());
		List<DocumentListVO> documents = new ArrayList<>();
		DocumentListVO vo = null;
		for(Document document : list){
			vo = new DocumentListVO();
			vo.setTitle(document.getStr("title"));
			vo.setDocumentUrl(document.getStr("desc_url"));
			documents.add(vo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("documents", documents);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//撤单
	@Transient
	public ReturnData resetOrder(LoginDTO dto){
		ReturnData data = new ReturnData();
		int wtmItemId = StringUtil.toInteger(dto.getOrderNo());
		if(wtmItemId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，订单不存在");
			return data;
		}
		//增加库存
		WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
		if(wtmItem != null){
			int currentStock = wtmItem.getInt("quality");
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
			if(StringUtil.equals(wtmItem.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						int size = tea.getInt("size") == null ? 0 : tea.getInt("size"); 
						currentStock = currentStock*size;
					}
				}
			}
			boolean ret = WarehouseTeaMember.dao.updateStock(wtm.getInt("id"), currentStock);
			if(ret){
				//更新在售茶叶状态
				int rets = WarehouseTeaMemberItem.dao.updateResetOrderStatus(wtmItemId, Constants.TEA_STATUS.RESET_ORDER,currentStock);
				if(rets != 0){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("撤单成功");
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("撤单失败");
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("撤单失败");
			}
		}
		return data;
	}
	
	public ReturnData queryTeaStoreList(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		double localLongtitude = Double.valueOf(StringUtil.isBlank(dto.getLocalLongtitude()) ? "116.40":dto.getLocalLongtitude());
		double localLatitude = Double.valueOf(StringUtil.isBlank(dto.getLocalLatitude()) ? "39.90" : dto.getLocalLatitude());
		//查找省市
		int province = dto.getProvinceId();
		int city = dto.getCityId();
 		String location = "";
		//只选择省
		if((province != 0)&&(city == 0)){
			Province p = Province.dao.queryProvince(province);
			if(p != null){
				location = StringUtil.formateLike(p.getStr("name"));
				//查找省下面的市
				List<City> citys = City.dao.queryAllCityByPid(p.getInt("id"));
				for(City city2 : citys){
					location = location +" or city_district "+StringUtil.formateLike(city2.getStr("name"));
				}
			}
		}
		//选择省市
		if(city != 0){
			City citys = City.dao.queryCity(city);
			if(citys != null){
				location = location+StringUtil.formateLike(citys.getStr("name"));
			}
		}
		
		
		/*CodeMst distance = CodeMst.dao.queryCodestByCode(Constants.DEFAULT_SETTING.MAP_DISTANCE);
		Long dis = new Long("10000");
		if(distance != null){
			dis = new Long(StringUtil.toString(distance.getInt("data1")));
		}
		double[] location = GeoUtil.getRectangle(localLongtitude, localLatitude, dis);
		Float maxLongtitude = new Float(location[2]);
		Float maxLatitude = new Float(location[3]);
		Float minLongtitude = new Float(location[0]);
		Float minLatitude = new Float(location[1]);
		
		List<Store> stores = Store.dao.queryStoreList(dto.getPageSize()
													 ,dto.getPageNum()
													 ,Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS
													 ,maxLongtitude
													 ,maxLatitude
													 ,minLongtitude
													 ,minLatitude);*/
		
		//查询出所有门店
		List<TeaStoreListVO> resultList = new ArrayList<>();
		List<DistanceModel> sortList = new ArrayList<>();
		TeaStoreListVO v = null;
		List<Store> allStores = Store.dao.queryAllStoreList(Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS,location);
		for(Store store : allStores){
			//循环
			v = new TeaStoreListVO();
			v.setStoreId(store.getInt("id"));
			v.setName(store.getStr("store_name"));
			v.setAddress(store.getStr("city_district"));
			if(store.getInt("member_id") != null){
				v.setBusinessId(store.getInt("member_id"));
			}else{
				v.setBusinessId(0);
			}
			
			v.setBusinessTea(store.getStr("business_tea"));
			double lg = Double.valueOf(String.valueOf(store.getFloat("longitude")));
			double lat = Double.valueOf(String.valueOf(store.getFloat("latitude")));
			double dist = GeoUtil.getDistanceOfMeter(localLatitude, localLongtitude,lat, lg);
			StoreImage storeImage = StoreImage.dao.queryStoreFirstImages(v.getStoreId());
			if(storeImage != null){
				v.setImg(storeImage.getStr("img"));
			}
			BigDecimal decimals = new BigDecimal(dist);
			if(decimals != null){
				DistanceModel model = new DistanceModel();
				BigDecimal km = decimals.divide(new BigDecimal("1000"));
				if(km.compareTo(new BigDecimal("1")) != 1){
					v.setDistance("1Km以内");
					model.setDistance(new BigDecimal("1"));
				}else{
					v.setDistance(StringUtil.toString(km.setScale(2,BigDecimal.ROUND_HALF_DOWN))+"Km");
					model.setDistance(km.setScale(2,BigDecimal.ROUND_HALF_DOWN));
				}
				model.setId(store.getInt("id"));
				model.setVo(v);
				sortList.add(model);
				
			}
		}
		Collections.sort(sortList);
		int fromRow = dto.getPageSize()*(dto.getPageNum()-1);
		int toRow = fromRow+dto.getPageSize();
		if(sortList.size() < fromRow){
			toRow = fromRow = sortList.size();
		}
		if(sortList.size() < toRow){
			toRow = sortList.size()-1;
		}
		//获取fromRow到toRow之间的key值
		for(int i=fromRow;((i<sortList.size())&&(i<=toRow));i++){
			DistanceModel k = sortList.get(i);
			resultList.add(k.getVo());
		}
				
		Map<String, Object> map = new HashMap<>();
		map.put("storeList", resultList);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
		
		/*List<TeaStoreListVO> list = new ArrayList<>();
		TeaStoreListVO vo = null;
		for(Store store : stores){
			vo = new TeaStoreListVO();
			vo.setStoreId(store.getInt("id"));
			vo.setName(store.getStr("store_name"));
			vo.setAddress(store.getStr("city_district"));
			vo.setBusinessTea(store.getStr("business_tea"));
			double lg = Double.valueOf(String.valueOf(store.getFloat("longitude")));
			double lat = Double.valueOf(String.valueOf(store.getFloat("latitude")));
			double dist = GeoUtil.getDistanceOfMeter(localLatitude, localLongtitude,lat, lg);
			BigDecimal decimals = new BigDecimal(dist);
			if(decimals != null){
				BigDecimal km = decimals.divide(new BigDecimal("1000"));
				if(km.compareTo(new BigDecimal("1")) != 1){
					vo.setDistance("1Km以内");
				}else{
					vo.setDistance(StringUtil.toString(km.setScale(2,BigDecimal.ROUND_HALF_DOWN))+"Km");
				}
			}
			StoreImage storeImage = StoreImage.dao.queryStoreFirstImages(vo.getStoreId());
			if(storeImage != null){
				vo.setImg(storeImage.getStr("img"));
			}
			list.add(vo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("storeList", list);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;*/
	}
	
	public ReturnData queryTeaStoreListBackUp(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		double localLongtitude = Double.valueOf(dto.getLocalLongtitude());
		double localLatitude = Double.valueOf(dto.getLocalLatitude());
		/*CodeMst distance = CodeMst.dao.queryCodestByCode(Constants.DEFAULT_SETTING.MAP_DISTANCE);
		Long dis = new Long("10000");
		if(distance != null){
			dis = new Long(StringUtil.toString(distance.getInt("data1")));
		}
		double[] location = GeoUtil.getRectangle(localLongtitude, localLatitude, dis);
		Float maxLongtitude = new Float(location[2]);
		Float maxLatitude = new Float(location[3]);
		Float minLongtitude = new Float(location[0]);
		Float minLatitude = new Float(location[1]);
		
		List<Store> stores = Store.dao.queryStoreList(dto.getPageSize()
													 ,dto.getPageNum()
													 ,Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS
													 ,maxLongtitude
													 ,maxLatitude
													 ,minLongtitude
													 ,minLatitude);*/
		
		//查询出所有门店
		List<TeaStoreListVO> resultList = new ArrayList<>();
		List<DistanceModel> sortList = new ArrayList<>();
		TeaStoreListVO v = null;
		List<Store> allStores = Store.dao.queryAllStoreList(Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS,"");
		for(Store store : allStores){
			//循环
			v = new TeaStoreListVO();
			v.setStoreId(store.getInt("id"));
			v.setName(store.getStr("store_name"));
			v.setAddress(store.getStr("city_district"));
			v.setBusinessTea(store.getStr("business_tea"));
			double lg = Double.valueOf(String.valueOf(store.getFloat("longitude")));
			double lat = Double.valueOf(String.valueOf(store.getFloat("latitude")));
			double dist = GeoUtil.getDistanceOfMeter(localLatitude, localLongtitude,lat, lg);
			StoreImage storeImage = StoreImage.dao.queryStoreFirstImages(v.getStoreId());
			if(storeImage != null){
				v.setImg(storeImage.getStr("img"));
			}
			BigDecimal decimals = new BigDecimal(dist);
			if(decimals != null){
				DistanceModel model = new DistanceModel();
				BigDecimal km = decimals.divide(new BigDecimal("1000"));
				if(km.compareTo(new BigDecimal("1")) != 1){
					v.setDistance("1Km以内");
					model.setDistance(new BigDecimal("1"));
				}else{
					v.setDistance(StringUtil.toString(km.setScale(2,BigDecimal.ROUND_HALF_DOWN))+"Km");
					model.setDistance(km.setScale(2,BigDecimal.ROUND_HALF_DOWN));
				}
				model.setId(store.getInt("id"));
				model.setVo(v);
				sortList.add(model);
				
			}
		}
		Collections.sort(sortList);
		int fromRow = dto.getPageSize()*(dto.getPageNum()-1);
		int toRow = fromRow+dto.getPageSize();
		if(sortList.size() < fromRow){
			toRow = fromRow = sortList.size();
		}
		if(sortList.size() < toRow){
			toRow = sortList.size()-1;
		}
		//获取fromRow到toRow之间的key值
		for(int i=fromRow;((i<sortList.size())&&(i<=toRow));i++){
			DistanceModel k = sortList.get(i);
			resultList.add(k.getVo());
		}
				
		Map<String, Object> map = new HashMap<>();
		map.put("storeList", resultList);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
		
		/*List<TeaStoreListVO> list = new ArrayList<>();
		TeaStoreListVO vo = null;
		for(Store store : stores){
			vo = new TeaStoreListVO();
			vo.setStoreId(store.getInt("id"));
			vo.setName(store.getStr("store_name"));
			vo.setAddress(store.getStr("city_district"));
			vo.setBusinessTea(store.getStr("business_tea"));
			double lg = Double.valueOf(String.valueOf(store.getFloat("longitude")));
			double lat = Double.valueOf(String.valueOf(store.getFloat("latitude")));
			double dist = GeoUtil.getDistanceOfMeter(localLatitude, localLongtitude,lat, lg);
			BigDecimal decimals = new BigDecimal(dist);
			if(decimals != null){
				BigDecimal km = decimals.divide(new BigDecimal("1000"));
				if(km.compareTo(new BigDecimal("1")) != 1){
					vo.setDistance("1Km以内");
				}else{
					vo.setDistance(StringUtil.toString(km.setScale(2,BigDecimal.ROUND_HALF_DOWN))+"Km");
				}
			}
			StoreImage storeImage = StoreImage.dao.queryStoreFirstImages(vo.getStoreId());
			if(storeImage != null){
				vo.setImg(storeImage.getStr("img"));
			}
			list.add(vo);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("storeList", list);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;*/
	}
	
	public ReturnData queryTeaStoreDetail(LoginDTO dto){
		ReturnData data = new ReturnData();
		Store store = Store.dao.queryById(dto.getId());
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错，门店不存在");
			return data;
		}
		StoreDetailListVO vo = new StoreDetailListVO();
		vo.setAddress(store.getStr("store_address"));
		vo.setBusinessFromTime(store.getStr("business_fromtime"));
		vo.setBusinessToTime(store.getStr("business_totime"));
		vo.setLatitude(store.getFloat("latitude"));
		vo.setLongitude(store.getFloat("longitude"));
		vo.setMobile(store.getStr("link_phone"));
		vo.setName(store.getStr("store_name"));
		vo.setStoreDesc(store.getStr("store_desc"));
		List<StoreImage> images = StoreImage.dao.queryStoreImages(store.getInt("id"));
		List<String> imgs = new ArrayList<>();
		for(int i=0;i<images.size();i++){
			StoreImage image = images.get(i);
			imgs.add(image.getStr("img"));
		}
		vo.setImgs(imgs);
		//默认评价
		List<StoreEvaluate> list = StoreEvaluate.dao.queryStoreEvaluateList(5
																		   ,1
																		   ,dto.getId());
		List<EvaluateListModel> models = new ArrayList<>();
		EvaluateListModel model = null;
		for(StoreEvaluate evaluate : list){
			model = new EvaluateListModel();
			model.setComment(evaluate.getStr("mark"));
			model.setCreateDate(DateUtil.formatMD(evaluate.getTimestamp("create_time")));
			model.setPoint(evaluate.getInt("service_point"));
			Member member = Member.dao.queryById(evaluate.getInt("member_id"));
			if(member != null){
				if(StringUtil.isNoneBlank(member.getStr("nick_name"))){
					model.setUserName(member.getStr("nick_name"));
			}else{
				model.setUserName(member.getStr("id_code"));
			}
			if(StringUtil.isNoneBlank(member.getStr("icon"))){
				model.setIcon(member.getStr("icon"));
			}else{
				CodeMst defaultIcon = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.DEFAULT_ICON);
				if(defaultIcon != null){
					model.setIcon(defaultIcon.getStr("data2"));
				}
			}
		}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("store", vo);
		map.put("evaluateList", models);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//绑定银行卡
	public ReturnData bingBankCard(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		MemberBankcard mbc = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		if(mbc != null){
			MemberBankcard bankcard = new MemberBankcard();
			bankcard.set("id", mbc.getInt("id"));
			bankcard.set("card_no", dto.getCardNo());
			bankcard.set("bank_name_cd", dto.getCardTypeCd());
			bankcard.set("owner_name", dto.getName());
			if(StringUtil.isNoneBlank(dto.getIcon())){
				bankcard.set("card_img", dto.getIcon());
			}
			bankcard.set("id_card_no", dto.getIdCardNo());
			bankcard.set("stay_mobile", dto.getMobile());
			bankcard.set("member_id", dto.getUserId());
			bankcard.set("create_time", DateUtil.getNowTimestamp());
			bankcard.set("update_time", DateUtil.getNowTimestamp());
			
			bankcard.set("status",Constants.BIND_BANKCARD_STATUS.APPLING);
			bankcard.set("open_bank_name", dto.getOpenBankName());
			boolean ret = MemberBankcard.dao.updateInfo(bankcard);
			if(ret){
				int retValue = Member.dao.updateIdCardInfo(dto.getUserId(), dto.getIdCardNo(), dto.getIdCardImg(),dto.getName());
				if(retValue != 0){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("绑定成功，待平台审核");
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("绑定失败");
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("绑定失败");
			}
		}else{
			MemberBankcard bankcard = new MemberBankcard();
			bankcard.set("card_no", dto.getCardNo());
			bankcard.set("bank_name_cd", dto.getCardTypeCd());
			bankcard.set("owner_name", dto.getName());
			bankcard.set("id_card_no", dto.getIdCardNo());
			bankcard.set("stay_mobile", dto.getMobile());
			bankcard.set("member_id", dto.getUserId());
			bankcard.set("create_time", DateUtil.getNowTimestamp());
			bankcard.set("update_time", DateUtil.getNowTimestamp());
			bankcard.set("card_img", dto.getIcon());
			bankcard.set("status",Constants.BIND_BANKCARD_STATUS.APPLING);
			bankcard.set("open_bank_name", dto.getOpenBankName());
			boolean ret = MemberBankcard.dao.saveInfo(bankcard);
			if(ret){
				int retValue = Member.dao.updateIdCardInfo(dto.getUserId(), dto.getIdCardNo(), dto.getIdCardImg(),dto.getName());
				if(retValue != 0){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("绑定成功，待平台审核");
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("绑定失败");
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("绑定失败");
			}
		}
		return data;
	}
	
	//提现
	@Transient
	public ReturnData withDraw(LoginDTO dto){
		ReturnData data = new ReturnData();
		int userId = dto.getUserId();
		BigDecimal money = dto.getMoney();
		String payPassword = dto.getPayPwd();
		Member member = Member.dao.queryById(userId);
		//判断用户存在？
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提现失败，用户不存在");
			return data;
		}
		if(StringUtil.isBlank(member.getStr("paypwd"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提现失败，请先设置支付密码");
			return data;
		}
		
		if(!StringUtil.equals(payPassword, member.getStr("paypwd"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提现失败，支付密码错误");
			return data;
		}
		//查看绑定银行卡
		MemberBankcard bankcard = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		if(bankcard == null || StringUtil.isBlank(bankcard.getStr("card_no"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有绑定银行卡");
			return data;
		}
		
		if((bankcard != null) && StringUtil.equals(Constants.BIND_BANKCARD_STATUS.APPLING, bankcard.getStr("status"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您绑定的银行卡正在审核中");
			return data;
		}
		
		if((bankcard != null) && StringUtil.equals(Constants.BIND_BANKCARD_STATUS.APPLY_FAIL, bankcard.getStr("status"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您绑定的银行卡审核失败，请重新提交");
			return data;
		}
		
		BigDecimal moneys = member.getBigDecimal("moneys");
		if(moneys == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，数据出错");
			return data;
		}
		String cardNo = bankcard.getStr("card_no");
		if(moneys.compareTo(money)>=0){
			Member member2 = new Member();
			member2.set("id", dto.getUserId());
			BigDecimal openingMoneys = moneys;
			BigDecimal closingMoneys = moneys.subtract(money);
			
			BankCardRecord record = new BankCardRecord();
			record.set("member_id", dto.getUserId());
			record.set("moneys", money);
			record.set("type_cd", Constants.BANK_MANU_TYPE_CD.WITHDRAW);
			record.set("card_no", cardNo);
			record.set("status", Constants.WITHDRAW_STATUS.APPLYING);
			record.set("create_time", DateUtil.getNowTimestamp());
			record.set("update_time", DateUtil.getNowTimestamp());
			record.set("balance", closingMoneys);
			boolean ret = BankCardRecord.dao.saveInfo(record);
			if(ret){
				member2.set("moneys", closingMoneys);
				boolean ret1 = Member.dao.updateInfo(member2);
				if(ret1){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("提现申请成功，请等待平台打款");
					//提现
					CashJournal cash = new CashJournal();
					cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
					cash.set("member_id", userId);
					cash.set("pi_type", Constants.PI_TYPE.GET_CASH);
					cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLING);
					cash.set("occur_date", new Date());
					cash.set("act_rev_amount", moneys);
					cash.set("act_pay_amount", moneys);
					cash.set("opening_balance", openingMoneys);
					cash.set("closing_balance", closingMoneys);
					cash.set("remarks", "申请提现："+money);
					cash.set("create_time", DateUtil.getNowTimestamp());
					cash.set("update_time", DateUtil.getNowTimestamp());
					CashJournal.dao.saveInfo(cash);
					return data;
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("提现申请失败");
					return data;
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("提现申请失败");
				return data;
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提现申请失败，余额不足");
			return data;
		}
	}
	
	//在售列表
	public ReturnData querySaleOrderList(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<WarehouseTeaMemberItem> list = WarehouseTeaMemberItem.dao.queryMemberWtmItems(dto.getUserId()
																						  ,dto.getPageSize()
																						  ,dto.getPageNum()
																						  ,Constants.TEA_STATUS.ON_SALE
																						  ,Constants.USER_TYPE.USER_TYPE_CLIENT);
		
		List<SaleOrderListVO> vos = new ArrayList<>();
		SaleOrderListVO vo = null;
		for(WarehouseTeaMemberItem item : list){
			vo = new SaleOrderListVO();
			vo.setOrderNo(StringUtil.toString(item.getInt("id")));
			vo.setAmount(item.getBigDecimal("price").multiply(new BigDecimal(item.getInt("origin_stock"))));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			if(wtm!=null){
				int teaId = wtm.getInt("tea_id");
				Tea tea = Tea.dao.queryById(teaId);
				if(tea != null){
					vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
					vo.setName(tea.getStr("tea_title"));
				}
			}
			vo.setPrice(item.getBigDecimal("price"));
			vo.setQuality(item.getInt("origin_stock"));
			if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.PIECE)){
				vo.setSize("片");
			}else{
				vo.setSize("件");
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
	
	//我要卖茶列表
	public ReturnData queryIWantSaleTeaList(LoginDTO dto){
		ReturnData data = new ReturnData();
		List<WarehouseTeaMember> list = WarehouseTeaMember.dao.queryWantSaleTeaList(Constants.USER_TYPE.USER_TYPE_CLIENT
																						   ,dto.getUserId()
																						   ,dto.getPageSize()
																						   ,dto.getPageNum());
		List<WantSaleTeaListVO> vos = new ArrayList<>();
		WantSaleTeaListVO vo = null;
		for(WarehouseTeaMember wtm : list){
			vo = new WantSaleTeaListVO();
			vo.setTeaId(wtm.getInt("id"));
			Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
			if(tea == null){
				continue;
			}
			vo.setName(tea.getStr("tea_title"));
			vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
			vo.setQuality(wtm.getInt("stock"));
			vo.setSize("片");
			vos.add(vo);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("list",vos);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	//客户保存支付密码
	public ReturnData saveUserPayPwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(dto.getMobile());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}
		
		//保存密码
		int ret = Member.dao.updatePay(dto.getMobile(), dto.getPayPwd());
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
			return data;
		}
	}
		
	//客户修改支付密码
	public ReturnData modifyUserPayPwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(dto.getMobile());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，用户不存在");
			return data;
		}
		if(!StringUtil.equals(member.getStr("paypwd"), dto.getOldPwd())){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，旧支付密码错误");
			return data;
		}
		//保存密码
		int ret = Member.dao.updatePay(dto.getMobile(), dto.getPayPwd());
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("修改成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("修改失败");
			return data;
		}
	}
	
	//绑定会员
	public ReturnData bindMember(LoginDTO dto){
		ReturnData data = new ReturnData();
		//商家id
		int businessId = dto.getBusinessId();
		int userId = dto.getUserId();
		if(businessId == userId){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，不能绑定自己的门店会员");
			return data;
		}
		if(businessId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店不存在");
			return data;
		}
		Store store = Store.dao.queryMemberStore(businessId);
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店不存在");
			return data;
		}
		
		int storeId = store.getInt("id");
		if(userId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，用户数据有误");
			return data;
		}
		
		Member member = Member.dao.queryById(userId);
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，用户数据有误");
			return data;
		}
		
		if((member.getInt("store_id")!=null)&&(member.getInt("store_id")!=0) && (member.getInt("store_id")!=storeId)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您已绑定过其他门店，不能重复绑定");
			return data;
		}
		
		if((member.getInt("store_id")!=null)&&(member.getInt("store_id")!=0) && (member.getInt("store_id")==storeId)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您已经绑定过此门店了，无需重复绑定");
			return data;
		}
		
		String status = store.getStr("status");
		if(!StringUtil.equals(status, Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店暂未通过审核");
			return data;
		}
		
		int ret = Member.dao.bindStore(dto.getUserId(), storeId);
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("绑定成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败");
			return data;
		}
	}
	
	//查询会员账号余额
	public ReturnData queryMemberMoney(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryById(dto.getUserId());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("用户数据有误");
			return data;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("money", StringUtil.toString(member.getBigDecimal("moneys")));
		data.setData(map);
		data.setMessage("查询成功");
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		return data;
	}
	
	//提现画面初始化
	public ReturnData withDrawInit(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		MemberBankcard bankcard = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		if(bankcard == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有绑定银行卡");
			return data;
		}
		WithDrawInitVO vo = new WithDrawInitVO();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(bankcard.getStr("bank_name_cd"));
		if(codeMst != null){
			vo.setCardImg(codeMst.getStr("data2"));
			vo.setBankName(codeMst.getStr("name"));
		}
		
		String bankNo = bankcard.getStr("card_no");
		if(StringUtil.isNoneBlank(bankNo)){
			int size = bankNo.length();
			vo.setBankNo("尾号"+bankNo.substring(size-4, size));
		}
		Member member = Member.dao.queryById(dto.getUserId());
		Map<String, Object> map = new HashMap<>();
		map.put("bankCard", vo);
		map.put("money", member.getBigDecimal("moneys"));
		data.setData(map);
		data.setMessage("查询成功");
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		return data;
	}
	
	//获取忘记支付密码验证码
	public ReturnData getForgetPayCode(LoginDTO dto){
		Member member = Member.dao.queryMember(dto.getMobile());
		String code = VertifyUtil.getVertifyCode();
		ReturnData data = new ReturnData();
		VertifyCode vc = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
		if((member != null)&&(StringUtil.isBlank(member.getStr("paypwd")))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有支付密码，请先设置");
			return data;
		}
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
			VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
			if(vCode == null){
				boolean isSave = VertifyCode.dao.saveVertifyCode(dto.getMobile(), dto.getUserTypeCd(), code,new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
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
				VertifyCode.dao.updateVertifyCode(dto.getMobile(), code,Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
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
	
	//保存忘记支付密码
	public ReturnData saveForgetPayPwd(LoginDTO dto){
		ReturnData data = new ReturnData();
		String payPwd = dto.getPayPwd();
		Member member = Member.dao.queryById(dto.getUserId());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("用户数据出错");
			return data;
		}
		if(StringUtil.isNoneBlank(payPwd)&&StringUtil.equals(payPwd, member.getStr("userpwd"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，支付密码和登录密码不能一样");
			return data;
		}
		VertifyCode vCode = VertifyCode.dao.queryVertifyCode(dto.getMobile(),Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
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
			VertifyCode.dao.updateVertifyCodeExpire(dto.getMobile(), now,Constants.SHORT_MESSAGE_TYPE.FORGET_PAY_PWD);
			//保存密码
			Member.dao.updatePay(dto.getMobile(), dto.getPayPwd());
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("密码修改成功");
			return data;
		 }
	}
	
	//查询银行卡
	public ReturnData queryBankCard(LoginDTO dto){
		ReturnData data = new ReturnData();
		MemberBankcard bankcard = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		if(bankcard == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有绑定银行卡");
			return data;
		}
		
		if(StringUtil.isBlank(bankcard.getStr("card_no"))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有绑定银行卡");
			return data;
		}
		
		BankCardDetailVO vo = new BankCardDetailVO();
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(bankcard.getStr("bank_name_cd"));
		if(codeMst != null){
			vo.setBankName(codeMst.getStr("name"));
			vo.setCardImg(codeMst.getStr("data3"));
		}
		vo.setStatus(bankcard.getStr("status"));
		CodeMst status = CodeMst.dao.queryCodestByCode(bankcard.getStr("status"));
		if(status != null){
			vo.setStatus(status.getStr("code"));
			vo.setStatusName(status.getStr("name"));
		}
		CodeMst phone = CodeMst.dao.queryCodestByCode(Constants.PHONE.CUSTOM);
		String cardNo = bankcard.getStr("card_no");
		if(StringUtil.isNoneBlank(cardNo)){
			int size = StringUtil.isBlank(cardNo) ? 0 : cardNo.length();
			vo.setCardNo("**** **** **** "+cardNo.substring(size-4, size));
		}
		
		//查询银行卡详情
		MemberBankcard bankcard1 = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		BankCardVO bankCardVO = new BankCardVO();
		Member member = Member.dao.queryById(dto.getUserId());
		if(bankcard1 != null){
			bankCardVO.setBankCardNo(bankcard1.getStr("card_no"));
			bankCardVO.setBankImg(bankcard1.getStr("card_img"));
			if(member != null){
				bankCardVO.setIdCardNo(member.getStr("id_card_no"));
				bankCardVO.setIdCardImg(member.getStr("id_card_img"));
			}
			bankCardVO.setMobile(bankcard1.getStr("stay_mobile"));
			bankCardVO.setOpenBankCd(bankcard1.getStr("bank_name_cd"));
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankcard1.getStr("bank_name_cd"));
			if(bank != null){
				bankCardVO.setOpenBankName(bank.getStr("name"));
			}
			bankCardVO.setOpenBrunchBank(bankcard1.getStr("open_bank_name"));
			bankCardVO.setUserName(bankcard1.getStr("owner_name"));
		}
		Map<String, Object> map = new HashMap<>();
		map.put("bankCard", bankCardVO);
		map.put("card", vo);
		if(phone !=null){
			map.put("phone", phone.getStr("data2"));
		}
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//查询门店
	public ReturnData queryStore(LoginDTO dto){
		ReturnData data = new ReturnData();
		Store store = Store.dao.queryMemberStore(dto.getSellerId());
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您扫码的商家暂未绑定门店");
			return data;
		}
		StoreDetailVO vo = new StoreDetailVO();
		vo.setStoreId(store.getInt("id"));
		vo.setName(store.getStr("store_name"));
		vo.setBusinessTea(store.getStr("business_tea"));
		vo.setMobile(store.getStr("link_phone"));
		vo.setTime(store.getStr("business_fromtime")+"-"+store.getStr("business_totime"));
		Province p = Province.dao.queryProvince(store.getInt("province_id"));
		City c = City.dao.queryCity(store.getInt("city_id"));
		District d = District.dao.queryDistrict(store.getInt("district_id"));
		String address = "";
		/*if(p != null){
			address = address + p.getStr("name");
		}*/
		if(c != null){
			address = c.getStr("name")+"市";
		}
		if(d != null){
			address = address + d.getStr("name");
		}
		vo.setAddress(address+store.getStr("store_address"));
		Map<String, Object> map = new HashMap<>();
		map.put("store", vo);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//下单
	public ReturnData pay(LoginDTO dto){
		ReturnData data = new ReturnData();
		int wtmItemId = dto.getTeaId();
		int quality = dto.getQuality();
		
		Member buyUserMember = Member.dao.queryById(dto.getUserId());
		if(!StringUtil.equals(StringUtil.checkCode(dto.getPayPwd()), buyUserMember.getStr("paypwd"))){
			data.setCode(Constants.STATUS_CODE.PAYPWD_ERROR);
			data.setMessage("对不起，支付密码错误");
			return data;
		}
		
		WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
		if(item == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，茶叶数据出错");
			return data;
		}
		if(quality <= 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，购买数量不能为0");
			return data;
		}
		int itemStock = item.getInt("quality");
		CodeMst sizeType = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
		if(quality > itemStock){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶库存不足"+quality+sizeType.getStr("name"));
			return data;
		}
		System.out.println(wtmItemId+"---"+item.getStr("status"));
		if(StringUtil.isNoneBlank(item.getStr("status"))
				&&(!StringUtil.equals(item.getStr("status"), Constants.TEA_STATUS.ON_SALE))){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，此茶叶已停售");
			return data;
		}
		
		//判断账号金额够不够
		BigDecimal all = item.getBigDecimal("price").multiply(new BigDecimal(quality));
		if(all.compareTo(buyUserMember.getBigDecimal("moneys"))==1){
			//余额不够
			data.setCode(Constants.STATUS_CODE.ACCOUNT_MONEY_NOT_ENOUGH);
			data.setMessage("对不起，账户余额不足");
			return data;
		}
		//添加订单
		Order order = new Order();
		order.set("order_no", StringUtil.getOrderNo());
		order.set("pay_amount", all);
		order.set("create_time", DateUtil.getNowTimestamp());
		order.set("update_time", DateUtil.getNowTimestamp());
		order.set("pay_time", DateUtil.getNowTimestamp());
		order.set("order_status",Constants.ORDER_STATUS.PAY_SUCCESS);
		order.set("member_id", dto.getUserId());
		Order order2 = Order.dao.addInfo(order);
		int orderId = order2.getInt("id");
		if(orderId != 0){
			OrderItem item2 = new OrderItem();
			item2.set("wtm_item_id", wtmItemId);
			item2.set("quality", quality);
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			if(wtm != null){
				item2.set("sale_id", wtm.getInt("member_id"));
				item2.set("sale_user_type", wtm.getStr("member_type_cd"));
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("下单失败，卖家茶叶不存在");
				return data;
			}
			
			item2.set("order_id", orderId);
			item2.set("item_amount", all);
			item2.set("member_id", dto.getUserId());
			item2.set("create_time", DateUtil.getNowTimestamp());
			item2.set("update_time", DateUtil.getNowTimestamp());
			int retId = OrderItem.dao.saveInfo(item2);
			if(retId != 0){
				int saleUser = wtm.getInt("member_id");
				int ret = 0;
				//保存成功，买家扣款，卖家
				if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
					//用户卖家账号加钱
					Member saleUserMember = Member.dao.queryById(saleUser);
					BigDecimal open = new BigDecimal("0");
					BigDecimal close = new BigDecimal("0");
					int saleUserId = 0;
					if(saleUserMember != null){
						open = saleUserMember.getBigDecimal("moneys");
						close = all.add(saleUserMember.getBigDecimal("moneys"));
						saleUserId = saleUserMember.getInt("id");
					}
					
					ret = Member.dao.updateMoneys(saleUser, close);
					CashJournal cash = new CashJournal();
					cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
					cash.set("member_id", saleUserId);
					cash.set("pi_type", Constants.PI_TYPE.SALE_TEA);
					cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
					cash.set("occur_date", new Date());
					cash.set("act_rev_amount", all);
					cash.set("member_type_cd", Constants.USER_TYPE.USER_TYPE_CLIENT);
					cash.set("act_pay_amount", all);
					cash.set("opening_balance",open);
					cash.set("closing_balance", close);
					cash.set("remarks", "卖茶"+all);
					cash.set("create_time", DateUtil.getNowTimestamp());
					cash.set("update_time", DateUtil.getNowTimestamp());
					CashJournal.dao.saveInfo(cash);
				}else{
					//平台卖家加钱
					User user = User.dao.queryById(saleUser);
					BigDecimal open = new BigDecimal("0");
					BigDecimal close = new BigDecimal("0");
					int saleUserId = 0;
					if(user != null){
						open = user.getBigDecimal("moneys");
						close = all.add(user.getBigDecimal("moneys"));
						saleUserId = user.getInt("user_id");
					}
					
					ret = User.dao.updateMoneys(saleUser, close);
					CashJournal cash = new CashJournal();
					cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
					cash.set("member_id", saleUserId);
					cash.set("pi_type", Constants.PI_TYPE.SALE_TEA);
					cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
					cash.set("occur_date", new Date());
					cash.set("member_type_cd", Constants.USER_TYPE.PLATFORM_USER);
					cash.set("act_rev_amount", all);
					cash.set("act_pay_amount", all);
					cash.set("opening_balance",open);
					cash.set("closing_balance", close);
					cash.set("remarks", "卖茶"+all);
					cash.set("create_time", DateUtil.getNowTimestamp());
					cash.set("update_time", DateUtil.getNowTimestamp());
					CashJournal.dao.saveInfo(cash);
				}
				int allQuality = 0;
				if(ret != 0){
					//买家扣款
					BigDecimal openBalance = buyUserMember.getBigDecimal("moneys");
					int rt = Member.dao.updateMoneys(dto.getUserId(), buyUserMember.getBigDecimal("moneys").subtract(all));
					if(rt != 0){
						//下单记录
						CashJournal cash = new CashJournal();
						cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
						cash.set("member_id", dto.getUserId());
						cash.set("pi_type", Constants.PI_TYPE.ADD_ORDER);
						cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
						cash.set("occur_date", new Date());
						cash.set("act_rev_amount", all);
						cash.set("act_pay_amount", all);
						Member member = Member.dao.queryById(dto.getUserId());
						cash.set("opening_balance",openBalance);
						cash.set("closing_balance", member.getBigDecimal("moneys"));
						cash.set("remarks", "下单"+all);
						cash.set("member_type_cd", Constants.USER_TYPE.USER_TYPE_CLIENT);
						cash.set("create_time", DateUtil.getNowTimestamp());
						cash.set("update_time", DateUtil.getNowTimestamp());
						CashJournal.dao.saveInfo(cash);
						
						//减少卖家库存
						int update = WarehouseTeaMemberItem.dao.cutTeaQuality(quality, wtmItemId);
						//20171121如果库存变为0，更新卖茶成功
						WarehouseTeaMemberItem currentWtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
						if(currentWtmItem != null){
							int currentQuality = currentWtmItem.getInt("quality")==null? 0 : currentWtmItem.getInt("quality");
							if(currentQuality == 0){
								//没有库存了，更新为卖茶成功
								WarehouseTeaMemberItem.dao.updateOnlyStatus(wtmItemId, Constants.TEA_STATUS.SALE_SUCCESS);
								if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
									//如果是平台卖茶结束后，改变状态为发行结束
									int teaId = wtm.getInt("tea_id") == null ? 0 : wtm.getInt("tea_id");
									Tea.dao.updateStatus(teaId, Constants.NEWTEA_STATUS.END);
								}
							}
						}
						 
						//如果是卖家是平台,减少WarehouseTeaMem的库存
						boolean cutFlg = true;
						if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
							int cutQuality = quality;
							if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
								int teaId = wtm.getInt("tea_id");
								Tea t = Tea.dao.queryById(teaId);
								cutQuality = quality*t.getInt("size");
							}
							cutFlg = WarehouseTeaMember.dao.cutStock(item.getInt("warehouse_tea_member_id"), cutQuality);
						}
						
						if((update != 0)&&(cutFlg)){
							//增加买家库存
							//判断这件茶叶，买家是否买过
							int teaId = wtm.getInt("tea_id");
							int houseId = wtm.getInt("warehouse_id");
							WarehouseTeaMember buyWtm = WarehouseTeaMember.dao.queryByUserInfo(teaId, dto.getUserId(), houseId,Constants.USER_TYPE.USER_TYPE_CLIENT);
							if(buyWtm == null){
								Tea teaInfo = Tea.dao.queryById(teaId);
								//库存不存在这种茶
								WarehouseTeaMember wtmsMember = new WarehouseTeaMember();
								wtmsMember.set("warehouse_id", houseId);
								wtmsMember.set("tea_id", teaId);
								wtmsMember.set("member_id", dto.getUserId());
								if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
									//按件购买
									if(teaInfo != null){
										allQuality = quality*teaInfo.getInt("size");
										wtmsMember.set("stock", allQuality);
										wtmsMember.set("origin_stock", allQuality);
									}
								}else{
									wtmsMember.set("stock", quality);
									wtmsMember.set("origin_stock", quality);
								}
								wtmsMember.set("create_time", DateUtil.getNowTimestamp());
								wtmsMember.set("update_time", DateUtil.getNowTimestamp());
								wtmsMember.set("member_type_cd", Constants.USER_TYPE.USER_TYPE_CLIENT);
								boolean saveFlg = WarehouseTeaMember.dao.saveInfo(wtmsMember);
								if(saveFlg){
									//减少库存
									data.setCode(Constants.STATUS_CODE.SUCCESS);
									data.setMessage("下单成功");
									//插入卖家卖茶消息
									Message message = new Message();
									message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
									CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
									String unitStr = "";
									if(unit != null){
										unitStr = unit.getStr("name");
									}
									message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
									message.set("params", "{id:"+retId+"}");
									message.set("title","出售茶叶");
									message.set("create_time", DateUtil.getNowTimestamp());
									message.set("update_time", DateUtil.getNowTimestamp());
									message.set("user_id", wtm.getInt("member_id"));
									boolean messageSave = Message.dao.saveInfo(message);
									
									return data;
								}else{
									data.setCode(Constants.STATUS_CODE.FAIL);
									data.setMessage("下单失败");
									return data;
								}
							}else{
								//库存已经有这种茶
								Tea teaInfo = Tea.dao.queryById(teaId);
								if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
									//按件购买
									if(teaInfo != null){
										int updateWTM = WarehouseTeaMember.dao.addTeaQuality(quality*teaInfo.getInt("size"), houseId, teaId, dto.getUserId());
										if(updateWTM != 0){
											data.setCode(Constants.STATUS_CODE.SUCCESS);
											data.setMessage("下单成功");
											//插入卖家卖茶消息
											Message message = new Message();
											message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
											CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
											String unitStr = "";
											if(unit != null){
												unitStr = unit.getStr("name");
											}
											message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
											message.set("params", "{id:"+retId+"}");
											message.set("title","出售茶叶");
											message.set("create_time", DateUtil.getNowTimestamp());
											message.set("update_time", DateUtil.getNowTimestamp());
											message.set("user_id", wtm.getInt("member_id"));
											boolean messageSave = Message.dao.saveInfo(message);
											
											return data;
										}else{
											data.setCode(Constants.STATUS_CODE.FAIL);
											data.setMessage("下单失败");
											return data;
										}
									}
								}else{
									int updateWTM = WarehouseTeaMember.dao.addTeaQuality(quality, houseId, teaId, dto.getUserId());
									if(updateWTM != 0){
										data.setCode(Constants.STATUS_CODE.SUCCESS);
										data.setMessage("下单成功");
										
										//插入卖家卖茶消息
										Message message = new Message();
										message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
										CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
										String unitStr = "";
										if(unit != null){
											unitStr = unit.getStr("name");
										}
										message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
										message.set("params", "{id:"+retId+"}");
										message.set("title","出售茶叶");
										message.set("create_time", DateUtil.getNowTimestamp());
										message.set("update_time", DateUtil.getNowTimestamp());
										message.set("user_id", wtm.getInt("member_id"));
										boolean messageSave = Message.dao.saveInfo(message);
										
										return data;
									}else{
										data.setCode(Constants.STATUS_CODE.FAIL);
										data.setMessage("下单失败");
										return data;
									}
								}
							}
						}else{
							data.setCode(Constants.STATUS_CODE.FAIL);
							data.setMessage("下单失败");
							return data;
						}
					}else{
						data.setCode(Constants.STATUS_CODE.FAIL);
						data.setMessage("下单失败");
						return data;
					}
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("下单失败");
					return data;
				}
			}else{
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("下单失败");
				return data;
			}
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("下单失败");
			return data;
		}
		return data;
	}
	
	//购物车付款
	public ReturnData addOrder(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		String str[] = dto.getTeas().split(",");
		int iSize = str.length;
		
		Member buyUserMembers = Member.dao.queryById(dto.getUserId());
		if(!StringUtil.equals(StringUtil.checkCode(dto.getPayPwd()), buyUserMembers.getStr("paypwd"))){
			data.setCode(Constants.STATUS_CODE.PAYPWD_ERROR);
			data.setMessage("对不起，支付密码错误");
			return data;
		}
		
		//总价
		BigDecimal amount = new BigDecimal("0");
		for (int i = 0; i < iSize; i++) {
			
			BuyCart cart = BuyCart.dao.queryById(StringUtil.toInteger(str[i]));
			int wtmItemId = cart.getInt("warehouse_tea_member_item_id");
			int quality = (int)cart.getInt("quality");
			WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
			if(item == null){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，你所选中的第"+(i+1)+"种茶叶已不存在，请重新选择要购买的产品");
				return data;
			}
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			if(wtm == null){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，你所选中的第"+(i+1)+"种茶叶已不存在，请重新选择要购买的产品");
				return data;
			}
			Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
			if(tea == null){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，你所选中的第"+(i+1)+"种茶叶已不存在，请重新选择要购买的产品");
				return data;
			}
			String teaName = tea.getStr("tea_title");
			
			int itemStock = item.getInt("quality");
			CodeMst sizeType = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
			if(quality > itemStock){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，"+teaName+"库存不足"+quality+sizeType.getStr("name"));
				return data;
			}
			if(StringUtil.isNoneBlank(item.getStr("status"))
					&&(!StringUtil.equals(item.getStr("status"), Constants.TEA_STATUS.ON_SALE))){
				data.setCode(Constants.STATUS_CODE.FAIL);
				data.setMessage("对不起，"+teaName+"已停售");
				return data;
			}
			
			amount = amount.add(item.getBigDecimal("price").multiply(new BigDecimal(quality)));
		}
		Member buyUserMember = Member.dao.queryById(dto.getUserId());
		if(amount.compareTo(buyUserMember.getBigDecimal("moneys"))==1){
			//余额不够
			data.setCode(Constants.STATUS_CODE.ACCOUNT_MONEY_NOT_ENOUGH);
			data.setMessage("对不起，账户余额不足");
			return data;
		}
		
		//添加订单
		String orderNo = StringUtil.getOrderNo();
		Order order = new Order();
		order.set("order_no", orderNo);
		order.set("pay_amount", amount);
		order.set("create_time", DateUtil.getNowTimestamp());
		order.set("update_time", DateUtil.getNowTimestamp());
		order.set("pay_time", DateUtil.getNowTimestamp());
		order.set("order_status",Constants.ORDER_STATUS.PAY_SUCCESS);
		order.set("member_id", dto.getUserId());
		Order order2 = Order.dao.addInfo(order);
		int orderId = order2.getInt("id");
		for (int i = 0; i < iSize; i++) {
			BuyCart cart = BuyCart.dao.queryById(StringUtil.toInteger(str[i]));
			int wtmItemId = cart.getInt("warehouse_tea_member_item_id");
			int quality = (int)cart.getInt("quality");
			WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(item.getInt("warehouse_tea_member_id"));
			if(orderId != 0){
				OrderItem item2 = new OrderItem();
				item2.set("wtm_item_id", wtmItemId);
				item2.set("quality", quality);
				if(wtm != null){
					item2.set("sale_id", wtm.getInt("member_id"));
					item2.set("sale_user_type", wtm.getStr("member_type_cd"));
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("下单失败，卖家茶叶不存在");
					return data;
				}
				
				item2.set("order_id", orderId);
				BigDecimal itemAmount = item.getBigDecimal("price").multiply(new BigDecimal(quality));
				item2.set("item_amount", itemAmount);
				item2.set("member_id", dto.getUserId());
				item2.set("create_time", DateUtil.getNowTimestamp());
				item2.set("update_time", DateUtil.getNowTimestamp());
				int retId = OrderItem.dao.saveInfo(item2);
					int saleUser = wtm.getInt("member_id");
					int ret = 0;
					//保存成功，买家扣款，卖家
					if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
						//用户卖家
						Member saleUserMember = Member.dao.queryById(saleUser);
						BigDecimal open = new BigDecimal("0");
						BigDecimal close = new BigDecimal("0");
						int saleUserId = 0;
						if(saleUserMember != null){
							open = saleUserMember.getBigDecimal("moneys");
							close = itemAmount.add(saleUserMember.getBigDecimal("moneys"));
							saleUserId = saleUserMember.getInt("id");
						}
						
						ret = Member.dao.updateMoneys(saleUser, close);
						CashJournal cash = new CashJournal();
						cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
						cash.set("member_id", saleUserId);
						cash.set("pi_type", Constants.PI_TYPE.SALE_TEA);
						cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
						cash.set("occur_date", new Date());
						cash.set("act_rev_amount", itemAmount);
						cash.set("member_type_cd", Constants.USER_TYPE.USER_TYPE_CLIENT);
						cash.set("act_pay_amount", itemAmount);
						cash.set("opening_balance",open);
						cash.set("closing_balance", close);
						cash.set("remarks", "卖茶"+itemAmount);
						cash.set("create_time", DateUtil.getNowTimestamp());
						cash.set("update_time", DateUtil.getNowTimestamp());
						CashJournal.dao.saveInfo(cash);
					}else{
						//平台卖家
						User user = User.dao.queryById(saleUser);
						
						BigDecimal open = new BigDecimal("0");
						BigDecimal close = new BigDecimal("0");
						int saleUserId = 0;
						if(user != null){
							open = user.getBigDecimal("moneys");
							close = itemAmount.add(user.getBigDecimal("moneys"));
							saleUserId = user.getInt("user_id");
						}
						
						ret = User.dao.updateMoneys(saleUser, close);
						CashJournal cash = new CashJournal();
						cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
						cash.set("member_id", saleUserId);
						cash.set("pi_type", Constants.PI_TYPE.SALE_TEA);
						cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
						cash.set("occur_date", new Date());
						cash.set("member_type_cd", Constants.USER_TYPE.PLATFORM_USER);
						cash.set("act_rev_amount", itemAmount);
						cash.set("act_pay_amount", itemAmount);
						cash.set("opening_balance",open);
						cash.set("closing_balance", close);
						cash.set("remarks", "卖茶"+itemAmount);
						cash.set("create_time", DateUtil.getNowTimestamp());
						cash.set("update_time", DateUtil.getNowTimestamp());
						CashJournal.dao.saveInfo(cash);
					}
					
					if(ret != 0){
						//买家扣款
						Member m1 = Member.dao.queryById(dto.getUserId());
						int rt = Member.dao.cutMoneys(dto.getUserId(), itemAmount);
						if(rt != 0){
							//成功充值记录
							CashJournal cash = new CashJournal();
							cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
							cash.set("member_id", dto.getUserId());
							cash.set("pi_type", Constants.PI_TYPE.ADD_ORDER);
							cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
							cash.set("occur_date", new Date());
							cash.set("act_rev_amount", itemAmount);
							cash.set("act_pay_amount", itemAmount);
							Member member = Member.dao.queryById(dto.getUserId());
							cash.set("opening_balance", m1.getBigDecimal("moneys"));
							cash.set("closing_balance", member.getBigDecimal("moneys"));
							cash.set("remarks", "下单"+itemAmount);
							cash.set("create_time", DateUtil.getNowTimestamp());
							cash.set("update_time", DateUtil.getNowTimestamp());
							CashJournal.dao.saveInfo(cash);
							
							//如果是卖家是平台,减少WarehouseTeaMem的库存
							boolean cutFlg = true;
							if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
								int cutQuality = quality;
								if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
									int teaId = wtm.getInt("tea_id");
									Tea t = Tea.dao.queryById(teaId);
									cutQuality = quality*t.getInt("size");
								}
								cutFlg = WarehouseTeaMember.dao.cutStock(item.getInt("warehouse_tea_member_id"), cutQuality);
							}
							
							//减少卖家库存
							int update = WarehouseTeaMemberItem.dao.cutTeaQuality(quality, wtmItemId);
							//20171121如果库存变为0，更新卖茶成功
							WarehouseTeaMemberItem currentWtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(wtmItemId);
							if(currentWtmItem != null){
								int currentQuality = currentWtmItem.getInt("quality")==null? 0 : currentWtmItem.getInt("quality");
								if(currentQuality == 0){
									//没有库存了，更新为卖茶成功
									WarehouseTeaMemberItem.dao.updateOnlyStatus(wtmItemId, Constants.TEA_STATUS.SALE_SUCCESS);
									if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
										//如果是平台卖茶结束后，改变状态为发行结束
										int teaId = wtm.getInt("tea_id") == null ? 0 : wtm.getInt("tea_id");
										Tea.dao.updateStatus(teaId, Constants.NEWTEA_STATUS.END);
									}
								}
							}
							
							if((update != 0)&&(cutFlg)){
								//增加买家库存
								//判断这件茶叶，买家是否买过
								int teaId = wtm.getInt("tea_id");
								int houseId = wtm.getInt("warehouse_id");
								WarehouseTeaMember buyWtm = WarehouseTeaMember.dao.queryByUserInfo(teaId, dto.getUserId(), houseId,Constants.USER_TYPE.USER_TYPE_CLIENT);
								if(buyWtm == null){
									//库存不存在这种茶
									WarehouseTeaMember wtmsMember = new WarehouseTeaMember();
									wtmsMember.set("warehouse_id", houseId);
									wtmsMember.set("tea_id", teaId);
									wtmsMember.set("member_id", dto.getUserId());
									if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
										//按件购买
										Tea teaInfo = Tea.dao.queryById(teaId);
										if(teaInfo != null){
											wtmsMember.set("stock", quality*teaInfo.getInt("size"));
											wtmsMember.set("origin_stock", quality*teaInfo.getInt("size"));
										}
									}else{
										wtmsMember.set("stock", quality);
										wtmsMember.set("origin_stock", quality);
									}
									wtmsMember.set("create_time", DateUtil.getNowTimestamp());
									wtmsMember.set("update_time", DateUtil.getNowTimestamp());
									wtmsMember.set("member_type_cd", Constants.USER_TYPE.USER_TYPE_CLIENT);
									boolean saveFlg = WarehouseTeaMember.dao.saveInfo(wtmsMember);
									if(saveFlg){
										//插入卖家卖茶消息
										Message message = new Message();
										message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
										CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
										String unitStr = "";
										if(unit != null){
											unitStr = unit.getStr("name");
										}
										Tea tea = Tea.dao.queryById(teaId);
										message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
										message.set("title","出售茶叶");
										message.set("params", "{id:"+retId+"}");
										message.set("create_time", DateUtil.getNowTimestamp());
										message.set("update_time", DateUtil.getNowTimestamp());
										message.set("user_id", wtm.getInt("member_id"));
										boolean messageSave = Message.dao.saveInfo(message);
										
										continue;
									}else{
										data.setCode(Constants.STATUS_CODE.FAIL);
										data.setMessage("下单失败");
										return data;
									}
								}else{
									//库存已经有这种茶
									if(StringUtil.equals(item.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
										//按件购买
										Tea teaInfo = Tea.dao.queryById(teaId);
										if(teaInfo != null){
											int updateWTM = WarehouseTeaMember.dao.addTeaQuality(quality*teaInfo.getInt("size"), houseId, teaId, dto.getUserId());
											if(updateWTM != 0){
												//插入卖家卖茶消息
												Message message = new Message();
												message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
												CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
												String unitStr = "";
												if(unit != null){
													unitStr = unit.getStr("name");
												}
												Tea tea = Tea.dao.queryById(teaId);
												message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
												message.set("params", "{id:"+retId+"}");
												message.set("title","出售茶叶");
												message.set("create_time", DateUtil.getNowTimestamp());
												message.set("update_time", DateUtil.getNowTimestamp());
												message.set("user_id", wtm.getInt("member_id"));
												boolean messageSave = Message.dao.saveInfo(message);
												continue;
											}else{
												data.setCode(Constants.STATUS_CODE.FAIL);
												data.setMessage("下单失败");
												return data;
											}
										}
									}else{
										int updateWTM = WarehouseTeaMember.dao.addTeaQuality(quality, houseId, teaId, dto.getUserId());
										if(updateWTM != 0){
											//插入卖家卖茶消息
											Message message = new Message();
											message.set("message_type_cd", Constants.MESSAGE_TYPE.SALE_TEA);
											CodeMst unit = CodeMst.dao.queryCodestByCode(item.getStr("size_type_cd"));
											String unitStr = "";
											if(unit != null){
												unitStr = unit.getStr("name");
											}
											Tea tea = Tea.dao.queryById(teaId);
											message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
											message.set("params", "{id:"+retId+"}");
											message.set("title","出售茶叶");
											message.set("create_time", DateUtil.getNowTimestamp());
											message.set("update_time", DateUtil.getNowTimestamp());
											message.set("user_id", wtm.getInt("member_id"));
											boolean messageSave = Message.dao.saveInfo(message);
											continue;
										}else{
											data.setCode(Constants.STATUS_CODE.FAIL);
											data.setMessage("下单失败");
											return data;
										}
									}
								}
							}else{
								data.setCode(Constants.STATUS_CODE.FAIL);
								data.setMessage("下单失败");
								return data;
							}
						}else{
							data.setCode(Constants.STATUS_CODE.FAIL);
							data.setMessage("下单失败");
							return data;
						}
					}else{
						data.setCode(Constants.STATUS_CODE.FAIL);
						data.setMessage("下单失败");
						return data;
					}
			}
		}
		//更新购物车
		int ret3 = BuyCart.dao.updateStatus(dto.getTeas(), Constants.ORDER_STATUS.PAY_SUCCESS);
		if(ret3 != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("下单成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("下单失败");
			return data;
		}
	}
	
	public ReturnData queryCodeMst(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		List<CodeMst> list = CodeMst.dao.queryCodestByPcode(dto.getCode());
		List<CodeMstVO> vList = new ArrayList<>();
		CodeMstVO vo = null;
		for(CodeMst mst : list){
			vo = new CodeMstVO();
			vo.setCode(mst.getStr("code"));
			vo.setName(mst.getStr("name"));
			vList.add(vo);
		}
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(vList);
		return data;
	}
	
	public ReturnData queryPersonData(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryById(dto.getUserId());
		if(member != null){
			MemberDataVO vo = new MemberDataVO();
			vo.setIcon(member.getStr("icon"));
			vo.setMobile(member.getStr("mobile"));
			vo.setNickName(member.getStr("nick_name"));
			vo.setQqNo(member.getStr("qq"));
			vo.setWxNo(member.getStr("wx"));
			if(member.getInt("sex") != null){
				vo.setSex(member.getInt("sex"));
			}
			
			Store store = Store.dao.queryMemberStore(dto.getUserId());
			if(store != null){
				if(StringUtil.equals(store.getStr("status"), "110003")){
					vo.setStoreFlg(1);
				}else{
					vo.setStoreFlg(0);
				}
			}else{
				vo.setStoreFlg(0);
			}
			
			Map<String, Object> map = new HashMap<>();
			map.put("member", vo);
			data.setData(map);
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("查询成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("查询失败");
			return data;
		}
	}
	
	public ReturnData queryMemberBankCard(LoginDTO dto) throws Exception{
		ReturnData data = new ReturnData();
		MemberBankcard bankcard = MemberBankcard.dao.queryByMemberId(dto.getUserId());
		BankCardVO bankCardVO = new BankCardVO();
		Member member = Member.dao.queryById(dto.getUserId());
		if(bankcard != null){
			bankCardVO.setBankCardNo(bankcard.getStr("card_no"));
			bankCardVO.setBankImg(bankcard.getStr("card_img"));
			if(member != null){
				bankCardVO.setIdCardNo(member.getStr("id_card_no"));
				bankCardVO.setIdCardImg(member.getStr("id_card_img"));
			}
			bankCardVO.setMobile(bankcard.getStr("stay_mobile"));
			bankCardVO.setOpenBankCd(bankcard.getStr("bank_name_cd"));
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankcard.getStr("bank_name_cd"));
			if(bank != null){
				bankCardVO.setOpenBankName(bank.getStr("name"));
			}
			bankCardVO.setOpenBrunchBank(bankcard.getStr("open_bank_name"));
			bankCardVO.setUserName(bankcard.getStr("owner_name"));
			Map<String, Object> map = new HashMap<>();
			map.put("bankCard", bankCardVO);
			data.setData(map);
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("查询成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您还没有绑定银行卡");
			return data;
		}
	}
	
	public ReturnData evaluateStore(LoginDTO dto) throws Exception{
		
		ReturnData data = new ReturnData();
		StoreEvaluate evaluate = new StoreEvaluate();
		evaluate.set("member_id", dto.getUserId());
		evaluate.set("store_id", dto.getStoreId());
		evaluate.set("service_point", dto.getServicePoint());
		evaluate.set("tea_point", dto.getTeaPoint());
		evaluate.set("senitation_point", dto.getSenitationPoint());
		evaluate.set("mark", dto.getMark());
		evaluate.set("create_time", DateUtil.getNowTimestamp());
		evaluate.set("update_time", DateUtil.getNowTimestamp());
		evaluate.set("flg", 1);
		//判断一天评价1次，一个月5次
		CodeMst evaluateNum = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.EVALUATE_NUM);
		if(evaluateNum != null){
			int dayNum = StringUtil.toInteger(evaluateNum.getStr("data2"));
			int monthNum = StringUtil.toInteger(evaluateNum.getStr("data3"));
			
			//当天
			String day = DateUtil.getDate();
			//判断月
			int monthComment = StoreEvaluate.dao.sumStoreEvaluateNum(dto.getUserId(), dto.getStoreId(), day+" 00:00:00", DateUtil.getFirstDayByMonth()+" 23:59:59");
			if(monthComment>=monthNum){
				evaluate.set("flg", 0);
			}else{
				int dayComment = StoreEvaluate.dao.sumStoreEvaluateNum(dto.getUserId(), dto.getStoreId(), day+" 00:00:00", day+" 23:59:59");
				//判断天
				if(dayComment>=dayNum){
					evaluate.set("flg", 0);
				}
			}
		}
		
		int ret = StoreEvaluate.dao.saveInfos(evaluate);
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("提交成功，谢谢您的评价");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败，请重新提交您的评价");
			return data;
		}
	}
	
	public ReturnData bindStoreByMobile(LoginDTO dto){
		ReturnData data = new ReturnData();
		//商家id
		int businessId = dto.getBusinessId();
		Member member = Member.dao.queryMember(dto.getMobile());
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您的手机还未注册");
			return data;
		}
		//用户ID
		int userId = member.getInt("id");
		if(businessId == userId){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，不能绑定自己的门店会员");
			return data;
		}
		if(businessId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店不存在");
			return data;
		}
		Store store = Store.dao.queryMemberStore(businessId);
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店不存在");
			return data;
		}
		
		int storeId = store.getInt("id");
		if(userId == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，用户数据有误");
			return data;
		}
		
		if(member == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，用户数据有误");
			return data;
		}
		
		if((member.getInt("store_id")!=null)&&(member.getInt("store_id")!=0) && (member.getInt("store_id")!=storeId)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您已绑定过其他门店，不能重复绑定");
			return data;
		}
		
		if((member.getInt("store_id")!=null)&&(member.getInt("store_id")!=0) && (member.getInt("store_id")==storeId)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您已经绑定过此门店了，无需重复绑定");
			return data;
		}
		
		String status = store.getStr("status");
		if(!StringUtil.equals(status, Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败，您绑定门店暂未通过审核");
			return data;
		}
		
		int ret = Member.dao.bindStore(member.getInt("id"), storeId);
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("绑定成功");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("绑定失败");
			return data;
		}
	}
	
	public ReturnData queryEvaluateList(LoginDTO dto) throws Exception{
		
		ReturnData data = new ReturnData();
		List<StoreEvaluate> list = StoreEvaluate.dao.queryStoreEvaluateList(dto.getPageSize()
																		   ,dto.getPageNum()
																		   ,dto.getStoreId());
		List<EvaluateListModel> models = new ArrayList<>();
		EvaluateListModel model = null;
		for(StoreEvaluate evaluate : list){
			model = new EvaluateListModel();
			model.setComment(evaluate.getStr("mark"));
			model.setCreateDate(DateUtil.formatMD(evaluate.getTimestamp("create_time")));
			model.setPoint(evaluate.getInt("service_point"));
			Member member = Member.dao.queryById(evaluate.getInt("member_id"));
			if(member != null){
				if(StringUtil.isNoneBlank(member.getStr("nick_name"))){
					model.setUserName(member.getStr("nick_name"));
				}else{
					model.setUserName(member.getStr("id_code"));
				}
				if(StringUtil.isNoneBlank(member.getStr("icon"))){
					model.setIcon(member.getStr("icon"));
				}else{
					CodeMst defaultIcon = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.DEFAULT_ICON);
					if(defaultIcon != null){
						model.setIcon(defaultIcon.getStr("data2"));
					}
				}
			}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("evaluateList", models);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	//提交开票
	public ReturnData saveInvoice(LoginDTO dto) throws Exception{
		
		ReturnData data = new ReturnData();
		Invoice invoice = new Invoice();
		invoice.set("invoice_type_cd", dto.getType());
		invoice.set("user_id", dto.getUserId());
		invoice.set("title", dto.getTitle());
		invoice.set("title_type_cd", dto.getTitleTypeCd());
		invoice.set("tax_no", dto.getTaxNo());
		invoice.set("content", dto.getContent());
		invoice.set("moneys", dto.getMoney());
		invoice.set("mark", dto.getMark());
		invoice.set("address", dto.getAddress());
		invoice.set("mobile", dto.getMobile());
		invoice.set("bank", dto.getBank());
		invoice.set("account", dto.getAccount());
		invoice.set("mail", dto.getMail());
		invoice.set("status",Constants.INVOICE_STATUS.STAY_HANDLE);
		invoice.set("address_id", dto.getAddressId());
		invoice.set("create_time", DateUtil.getNowTimestamp());
		invoice.set("update_time", DateUtil.getNowTimestamp());
		
		int ret = Invoice.dao.saveInfos(invoice);
		if(ret != 0){
			String[] invoiceIds = StringUtil.split(dto.getInvoiceIds(),",");
			for(String id : invoiceIds){
				int idInt = StringUtil.toInteger(id);
				InvoiceGetteaRecord record = new InvoiceGetteaRecord();
				record.set("invoice_id", ret);
				record.set("gettea_record_id", idInt);
				record.set("create_time", DateUtil.getNowTimestamp());
				record.set("update_time", DateUtil.getNowTimestamp());
				InvoiceGetteaRecord.dao.saveInfos(record);
				GetTeaRecord.dao.updateInvoice(idInt, Constants.INVOICE_STATUS.STAY_HANDLE);
			}
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("提交成功，待平台处理");
			return data;
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("提交失败，请重新提交您的开票申请");
			return data;
		}
	}
	
	public ReturnData queryOpenInvoiceList(LoginDTO dto){
		ReturnData data = new ReturnData();
		CodeMst daysCodeMst = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.INVOICE_DATE);
		int days = 30;
		if(daysCodeMst != null){
			days = daysCodeMst.getInt("data1");
		}
		String date = DateUtil.getDate()+" 23:59:59";
		String date2 = DateUtil.getLastDayByNum(days)+" 00:00:00";
		List<GetTeaRecord> list = new ArrayList<>();
		if(dto.getFlg() == 0){
			//开票列表
			list = GetTeaRecord.dao.queryRecordByTime(dto.getPageSize()
													 ,dto.getPageNum()
													 ,dto.getUserId()
													 ,date2
													 ,date);
		}
		if(dto.getFlg() == 1){
			//开票历史
			list = GetTeaRecord.dao.queryRecordByTime2(dto.getPageSize()
													  ,dto.getPageNum()
													  ,dto.getUserId()
													  ,date2
													  ,date);
		}
		List<InvoiceListModel> models = new ArrayList<>();
		InvoiceListModel model = null;
		for(GetTeaRecord record : list){
			model = new InvoiceListModel();
			model.setCreateTime(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setId(record.getInt("id"));
			
			model.setStatus(record.getStr("invoice_status"));
			CodeMst statusMst = CodeMst.dao.queryCodestByCode(model.getStatus());
			if(statusMst != null){
				model.setStatusName(statusMst.getStr("name"));
			}
			//开票内容
			CodeMst invoiceMst = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.INVOICE_CONTENT);
			if(invoiceMst != null){
				model.setInvoiceContent(invoiceMst.getStr("name"));
			}
			
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
				//获取茶叶发行价
				BigDecimal teaItemPrice = tea.getBigDecimal("tea_price");
				if(teaItemPrice != null){
					if(StringUtil.equals(record.getStr("size_type_cd"), Constants.TEA_UNIT.ITEM)){
						model.setContent(teaItemPrice+"元/件x"+record.getInt("quality")+"件");
						model.setMoneys(teaItemPrice.multiply(new BigDecimal(record.getInt("quality"))));
					}
					if(StringUtil.equals(record.getStr("size_type_cd"), Constants.TEA_UNIT.PIECE)){
						BigDecimal piecePrice = teaItemPrice.divide(new BigDecimal(tea.getInt("size")));
						if(piecePrice != null){
							model.setContent(piecePrice+"元/片x"+record.getInt("quality")+"片");
							model.setMoneys(piecePrice.multiply(new BigDecimal(record.getInt("quality"))));
						}
					}
				}
			}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		//查询默认邮寄地址
		ReceiveAddress address = ReceiveAddress.dao.queryByFirstAddress(dto.getUserId()
																			  ,Constants.COMMON_STATUS.NORMAL);
		
		ChooseAddressVO vo = new ChooseAddressVO();
		if(address != null){
			vo.setAddress(address.getStr("address"));
			vo.setAddressId(address.getInt("id"));
			vo.setMobile(address.getStr("mobile"));
			vo.setReceiverMan(address.getStr("receiveman_name"));
			map.put("defaultAddress", vo);
		}else{
			map.put("defaultAddress", null);
		}
		
		map.put("models", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	public ReturnData queryStoreMemberList(LoginDTO dto){
		ReturnData data = new ReturnData();
		int userId = dto.getUserId();
		Store store = Store.dao.queryMemberStore(userId);
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您还没有提交绑定门店数据");
			return data;
		}
		List<Member> members = Member.dao.queryStoreMember(store.getInt("id"),dto.getPageSize(),dto.getPageNum(),dto.getType());
		List<StoreMemberListModel> list = new ArrayList<>();
		StoreMemberListModel model = null;
		CodeMst defaultIconCodeMst = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.DEFAULT_ICON);
		String defaultIcon = "";
		if(defaultIconCodeMst == null){
			defaultIcon = defaultIconCodeMst.getStr("data2");
		}
		for(Member member : members){
			model = new StoreMemberListModel();
			model.setId(member.getInt("id"));
			model.setCreateTime(DateUtil.format(member.getTimestamp("create_time")));
			if(StringUtil.isBlank(member.getStr("icon"))){
				model.setIcon(defaultIcon);
			}else{
				model.setIcon(member.getStr("icon"));
			}
			
			model.setMobile(member.getStr("mobile"));
			model.setSex(member.getInt("sex"));
			CodeMst role = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
			if(role != null){
				model.setRole(role.getStr("name"));
			}
			list.add(model);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberList", list);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	public ReturnData queryMemberOrderList(LoginDTO dto){
		ReturnData data = new ReturnData();
		String day = dto.getDate();
		List<OrderItem> list = new ArrayList<>();
		List<MemberOrderListModel> models = new ArrayList<>();
		MemberOrderListModel model = null;
		//商家ID
		int userId = dto.getUserId();
		int storeId = 0;
		if(userId != 0){
			Store store = Store.dao.queryMemberStore(userId);
			if(store != null){
				storeId = store.getInt("id");
			}
		}
		if(dto.getFlg()==0){
			//查询门店所有会员订单
			list = OrderItem.dao.queryAllOrderItemList(dto.getPageSize(), dto.getPageNum(), day,storeId);
		}
		if(dto.getFlg()==1){
			//查询某个会员订单
			list = OrderItem.dao.queryOrderItemList(dto.getPageSize(), dto.getPageNum(), dto.getMemberId(), day);
		}
		for(OrderItem item : list){
			model = new MemberOrderListModel();
			model.setId(item.getInt("id"));
			model.setCreateTime(DateUtil.formatTimestampForDate(item.getTimestamp("create_time")));
			model.setAmount(StringUtil.toString(item.getBigDecimal("item_amount")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(item.getInt("wtm_item_id"));
			if(wtmItem != null){
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						model.setTeaName(tea.getStr("tea_title"));
					}
				}
				String content = "￥" + wtmItem.getBigDecimal("price")+"元";
				CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
				if(sizeType != null){
					content = content +"/"+sizeType.getStr("name");
					content = content +"x"+ item.getInt("quality")+sizeType.getStr("name");
				}
				model.setContent(content);
			}
			Member member = Member.dao.queryById(item.getInt("member_id"));
			if(member != null){
				if(StringUtil.isNoneBlank(member.getStr("nick_name"))){
					model.setBuyUserName(member.getStr("nick_name"));
				}else{
					model.setBuyUserName(member.getStr("mobile"));
				}
			}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("orders", models);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		data.setData(map);
		return data;
	}
	
	public ReturnData queryMemberStoreDetail(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member m = Member.dao.queryById(dto.getUserId());
		if((m.getInt("store_id")==null)||(m.getInt("store_id")==0)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有绑定门店");
			return data;
		}
		Store store = Store.dao.queryById(m.getInt("store_id"));
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错，门店不存在");
			return data;
		}
		StoreDetailListVO vo = new StoreDetailListVO();
		vo.setAddress(store.getStr("store_address"));
		vo.setBusinessFromTime(store.getStr("business_fromtime"));
		vo.setBusinessToTime(store.getStr("business_totime"));
		vo.setLatitude(store.getFloat("latitude"));
		vo.setLongitude(store.getFloat("longitude"));
		vo.setMobile(store.getStr("link_phone"));
		vo.setName(store.getStr("store_name"));
		vo.setStoreDesc(store.getStr("store_desc"));
		List<StoreImage> images = StoreImage.dao.queryStoreImages(store.getInt("id"));
		List<String> imgs = new ArrayList<>();
		for(int i=0;i<images.size();i++){
			StoreImage image = images.get(i);
			imgs.add(image.getStr("img"));
		}
		vo.setImgs(imgs);
		//默认评价
		List<StoreEvaluate> list = StoreEvaluate.dao.queryStoreEvaluateList(5
																		   ,1
																		   ,dto.getId());
		List<EvaluateListModel> models = new ArrayList<>();
		EvaluateListModel model = null;
		for(StoreEvaluate evaluate : list){
			model = new EvaluateListModel();
			model.setComment(evaluate.getStr("mark"));
			model.setCreateDate(DateUtil.formatMD(evaluate.getTimestamp("create_time")));
			model.setPoint(evaluate.getInt("service_point"));
			Member member = Member.dao.queryById(evaluate.getInt("member_id"));
			if(member != null){
				if(StringUtil.isNoneBlank(member.getStr("nick_name"))){
					model.setUserName(member.getStr("nick_name"));
			}else{
				model.setUserName(member.getStr("id_code"));
			}
			if(StringUtil.isNoneBlank(member.getStr("icon"))){
				model.setIcon(member.getStr("icon"));
			}else{
				CodeMst defaultIcon = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.DEFAULT_ICON);
				if(defaultIcon != null){
					model.setIcon(defaultIcon.getStr("data2"));
				}
			}
		}
			models.add(model);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("store", vo);
		map.put("evaluateList", models);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
	
	public ReturnData updateSex(int userId,int sexFlg){

		ReturnData data = new ReturnData();
		int ret = Member.dao.updateSex(userId, sexFlg);
		if(ret == 0){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("保存失败");
		}else{
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("保存成功");
		}
			return data;
	}
	
	public ReturnData queryBusinessStore(LoginDTO dto){
		ReturnData data = new ReturnData();
		//商家id
		int businessId = dto.getBusinessId();
		Store store = Store.dao.queryMemberStore(businessId);
		if(store == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			return data;
		}else{
			if(!StringUtil.equals(store.getStr("status"), "110003")){
				data.setCode(Constants.STATUS_CODE.FAIL);
				return data;
			}else{
				data.setCode(Constants.STATUS_CODE.SUCCESS);
				return data;
			}
		}
	}
	
	//买茶记录账单详情
	public RecordListModel queryBuyNewTeaRecordDetail(LoginDTO dto){
		OrderItem item = OrderItem.dao.queryById(dto.getId());
		RecordListModel model = null;
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		if(item != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(item.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(item.getTimestamp("create_time")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(item.getInt("wtm_item_id"));
			String status = "";
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						String size = "";
						CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
						if(sizeType != null){
							size = sizeType.getStr("name");
						}
						model.setContent(tea.getStr("tea_title")+"x"+item.getInt("quality")+size);
						model.setTea(tea.getStr("tea_title"));
						String imgs = tea.getStr("cover_img");
						if(StringUtil.isNoneBlank(imgs)){
							String[] sp = imgs.split(",");
							model.setImg(sp[0]);
						}
						CodeMst teaType = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
						if(teaType != null){
							model.setTeaType(teaType.getStr("name"));
						}
						model.setQuality(item.getInt("quality"));
						if(sizeType != null){
							model.setUnit(sizeType.getStr("name"));
						}
						
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
				}
				CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
				if(sCodeMst != null){
					status = sCodeMst.getStr("name");
				}
			}
			model.setMoneys("-"+StringUtil.toString(item.getBigDecimal("item_amount"))+" "+"买茶成功");
		}
		return model;
	}
	
	//卖茶记录账单详情
	public RecordListModel querySaleTeaRecordDetail(LoginDTO dto){
		SaleOrder saleOrder = SaleOrder.dao.queryById(dto.getId());
		RecordListModel model = null;
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		if(saleOrder != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(saleOrder.getInt("id"));
			CodeMst unit = CodeMst.dao.queryCodestByCode(saleOrder.getStr("size_type_cd"));
			String unitStr = "";
			if(unit != null){
				unitStr = unit.getStr("name");
			}
			model.setDate(DateUtil.formatTimestampForDate(saleOrder.getTimestamp("create_time")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(saleOrder.getInt("wtm_item_id"));
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				String content = "";
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					int originStock = wtmItem.getInt("origin_stock") == null ? 0 :wtmItem.getInt("origin_stock");
					if(tea != null){
						content = tea.getStr("tea_title")+"x"+originStock+unitStr;
						model.setTea(tea.getStr("tea_title"));
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
					
					String price = wtmItem.getBigDecimal("price")+"/"+unitStr;
					int onSale = wtmItem.getInt("quality") ==  null ? 0 : wtmItem.getInt("quality");
					int cancleQuality = wtmItem.getInt("cancle_quality") == null ? 0 :wtmItem.getInt("cancle_quality");
					int haveSale = originStock-cancleQuality;
					
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(wtmItem.getStr("status"));
					if(sCodeMst != null){
						content = sCodeMst.getStr("name")+" "+content;
					}
					model.setContent(content);
					model.setMoneys("单价:￥"+price+" 已售"+haveSale+unitStr);
				}
			}
		}
		return model;
	}
	
	//仓储费记录账单详情
	public RecordListModel queryWareHouseRecordsDetail(LoginDTO dto){
		GetTeaRecord record = GetTeaRecord.dao.queryById(dto.getId());
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		if(record != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+record.getInt("quality")+"片");
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("warehouse_fee")));
		}
		return model;
	}
	
	//取茶记录账单详情
	public RecordListModel queryGetTeaRecordsModel(LoginDTO dto){
		
		GetTeaRecord record = GetTeaRecord.dao.queryById(dto.getId());
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		if(record != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			String size = "";
			CodeMst sizeType = CodeMst.dao.queryCodestByCode(record.getStr("size_type_cd"));
			if(sizeType != null){
				size = sizeType.getStr("name");
			}
			String status = "";
			CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(sCodeMst != null){
				status = sCodeMst.getStr("name");
			}
			if(tea != null){
				model.setContent(tea.getStr("tea_title")+"x"+record.getInt("quality")+size+" "+status);
			}
			
			ReceiveAddress receiveAddress = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
			if(receiveAddress != null){
				String address = "";
				City city = City.dao.queryCity(receiveAddress.getInt("city_id"));
				if(city != null){
					address = city.getStr("name");
				}
				District district = District.dao.queryDistrict(receiveAddress.getInt("district_id"));
				if(district != null){
					address = address + district.getStr("name");
				}
				address = address + receiveAddress.getStr("address");
				model.setMoneys(address);
			}
		}
		return model;
	}
	
	//充值记录账单详情
	public RecordListModel queryRechargeRecordsDetail(LoginDTO dto){
		CashJournal record = CashJournal.dao.queryById(dto.getId());
		RecordListModel model = null;
		if(record != null){
			model = new RecordListModel();
			CodeMst type = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(type != null){
				model.setType(type.getStr("name"));
			}else{
				model.setType("");
			}
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys(record.getStr("remarks"));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			String statusStr = "";
			if(status != null){
				statusStr = status.getStr("name");
			}
			model.setContent("期初:"+record.getBigDecimal("opening_balance")+",余额:"+record.getBigDecimal("closing_balance"));
		}
		return model;
	}
	
	//提现记录账单详情
	public RecordListModel queryWithDrawRecordsDetail(LoginDTO dto){
		
		BankCardRecord record = BankCardRecord.dao.queryById(dto.getId());
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		if(record != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			String st = "";
			if(status != null){
				st = status.getStr("name");
			}
			model.setMoneys("￥"+StringUtil.toString(record.getBigDecimal("moneys"))+" "+st);
			model.setContent("账号提现："+StringUtil.toString(record.getBigDecimal("moneys")));
		}
		return model;
	}

	//退款记录账单详情
	public RecordListModel queryRefundRecordsDetail(LoginDTO dto){
		
		BankCardRecord record = BankCardRecord.dao.queryById(dto.getId());
		CodeMst codeMst = CodeMst.dao.queryCodestByCode(dto.getType());
		if(codeMst == null){
			return null;
		}
		String type = codeMst.getStr("data2");
		RecordListModel model = null;
		if(record != null){
			model = new RecordListModel();
			model.setType(type);
			model.setId(record.getInt("id"));
			model.setDate(DateUtil.formatTimestampForDate(record.getTimestamp("create_time")));
			model.setMoneys("+"+StringUtil.toString(record.getBigDecimal("moneys")));
			model.setContent("账号退款："+StringUtil.toString(record.getBigDecimal("moneys")));
		}
		return model;
	}
}
