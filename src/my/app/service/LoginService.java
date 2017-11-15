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
import com.sun.org.apache.bcel.internal.generic.Select;

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
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.Message;
import my.core.model.News;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.OrderItemModel;
import my.core.model.PayRecord;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.RecordListModel;
import my.core.model.ReturnData;
import my.core.model.SaleOrder;
import my.core.model.ServiceFee;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.model.SystemVersionControl;
import my.core.model.Tea;
import my.core.model.TeaPrice;
import my.core.model.TeapriceLog;
import my.core.model.User;
import my.core.model.VertifyCode;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.tx.TxProxy;
import my.core.vo.AddressDetailVO;
import my.core.vo.AddressVO;
import my.core.vo.BankCardDetailVO;
import my.core.vo.BuyCartListVO;
import my.core.vo.BuyTeaListVO;
import my.core.vo.CarouselVO;
import my.core.vo.ChooseAddressVO;
import my.core.vo.CodeMstVO;
import my.core.vo.DataListVO;
import my.core.vo.DocumentListVO;
import my.core.vo.MemberDataVO;
import my.core.vo.MessageListDetailVO;
import my.core.vo.MessageListVO;
import my.core.vo.NewTeaSaleListModel;
import my.core.vo.NewsVO;
import my.core.vo.OrderAnalysisVO;
import my.core.vo.ReferencePriceModel;
import my.core.vo.SaleOrderListVO;
import my.core.vo.SelectSizeTeaListVO;
import my.core.vo.StoreDetailListVO;
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
import my.pvcloud.util.SortUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.TextUtil;
import my.pvcloud.util.VertifyUtil;
import my.pvcloud.vo.StoreDetailVO;
import net.sf.json.JSONObject;
import sun.org.mozilla.javascript.internal.ast.NewExpression;

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
		Member businessMember = Member.dao.queryMemberByInviteCode(invateCode);
		if(businessMember != null){
			Store store = Store.dao.queryMemberStore(businessMember.getInt("id"));
			if(store != null){
				storeId = store.getInt("id");
			}
		}
		int id = Member.dao.saveMember(mobile, userPwd,sex,dto.getUserTypeCd(),Constants.MEMBER_STATUS.NOT_CERTIFICATED,storeId);
		if(id != 0){
			Member m = Member.dao.queryMemberById(id);
			Map<String, Object> map = new HashMap<>();
			map.put("member", m);
			map.put("accessToken", token);
			VertifyCode.dao.updateVertifyCodeExpire(mobile, now,Constants.SHORT_MESSAGE_TYPE.REGISTER);
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
		if(!StringUtil.equals(userPwd, member.getStr("userpwd"))){
			data.setMessage("对不起，密码错误");
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
		
		if(!StringUtil.equals(token.getStr("token"), dto.getAccessToken())){
			data.setCode("5701");
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
		
		//判断是否绑定门店
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
		//版本号
		if(StringUtil.equals(dto.getPlatForm(), Constants.PLATFORM.ANDROID)){
			SystemVersionControl svc = SystemVersionControl.dao.querySystemVersionControl(Constants.VERSION_TYPE.ANDROID);
			if(svc != null){
				map.put("version", svc.getStr("version"));
				map.put("url", svc.getStr("data1"));
				map.put("shareAppUrl", svc.getStr("data2"));
			}else{
				map.put("version", null);
				map.put("url", null);
				map.put("shareAppUrl", null);
			}
		}else{
			SystemVersionControl svc = SystemVersionControl.dao.querySystemVersionControl(Constants.VERSION_TYPE.IOS);
			if(svc != null){
				map.put("version", svc.getStr("version"));
				map.put("url", svc.getStr("data2"));
				map.put("shareAppUrl", svc.getStr("data2"));
			}else{
				map.put("version", null);
				map.put("url", null);
				map.put("shareAppUrl", null);
			}
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
			Province province = Province.dao.queryProvince(ra.getInt("province_id"));
			if(province != null){
				vo.setProvince(province.getStr("name"));
			}
			City city = City.dao.queryCity(ra.getInt("city_id"));
			if(city != null){
				vo.setCity(city.getStr("name"));
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
			Order order = Order.dao.queryById(orderId);
			if(order != null){
				OrderItem orderItem = OrderItem.dao.queryById(orderId);
				if(orderItem != null){
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
						vo.setBargainAmount("￥"+StringUtil.toString(order.getBigDecimal("pay_amount")));
						vo.setCreateTime(StringUtil.toString(orderItem.getTimestamp("create_time")));
						vo.setPayAmount("￥"+StringUtil.toString(order.getBigDecimal("pay_amount")));
						vo.setPayTime(StringUtil.toString(order.getTimestamp("pay_time")));
						vo.setPrice("￥"+StringUtil.toString(wtmItem.getBigDecimal("price"))+sizeType);
						vo.setQuality(StringUtil.toString(orderItem.getInt("quality"))+size.getStr("name"));
					}
				}
				
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
		vo.setPrice("￥"+StringUtil.toString(tea.getBigDecimal("tea_price")));
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
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						model.setContent(tea.getStr("tea_title")+"x"+item.getInt("quality")+"片");
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
						CodeMst sizeType = CodeMst.dao.queryCodestByCode(wtmItem.getStr("size_type_cd"));
						if(sizeType != null){
							model.setUnit(sizeType.getStr("name"));
						}
						
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
				}
			}
			model.setMoneys("-"+StringUtil.toString(item.getBigDecimal("item_amount")));
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
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(saleOrder.getInt("wtm_item_id") == null?0:saleOrder.getInt("wtm_item_id"));
			if(wtmItem != null){
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						model.setContent(tea.getStr("tea_title")+"x"+saleOrder.getInt("quality")+unitStr);
						model.setTea(tea.getStr("tea_title"));
						WareHouse wHouse = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if(wHouse != null){
							model.setWareHouse(wHouse.getStr("warehouse_name"));
						}
					}
				}
			}
			int quality = saleOrder.getInt("quality") == null ? 0 : saleOrder.getInt("quality");
			BigDecimal  price = saleOrder.getBigDecimal("price") == null ? new BigDecimal("0") : saleOrder.getBigDecimal("price");
			String sum = StringUtil.toString(price.multiply(new BigDecimal(quality)));
			model.setMoneys("售价："+saleOrder.getBigDecimal("price")+"元/"+unitStr+" 总价：￥"+sum);
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
		List<PayRecord> list = PayRecord.dao.queryRecords(dto.getPageSize(), dto.getPageNum(), dto.getUserId(), dto.getDate());
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
		for(PayRecord record : list){
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
		cart.set("warehouse_tea_member_item_id", dto.getTeaId());
		cart.set("quality", dto.getQuality());
		cart.set("status", Constants.ORDER_STATUS.SHOPPING_CART);
		cart.set("create_time", DateUtil.getNowTimestamp());
		cart.set("update_time", DateUtil.getNowTimestamp());
		
		WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(dto.getTeaId());
		if(wtmItem == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据不存在");
			return data;
		}
		cart.set("size", wtmItem.getStr("size_type_cd"));
		WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmItem.getInt("warehouse_tea_member_id"));
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
		//List<Tea> teas = Tea.dao.queryBuyTeaList(dto.getPageSize(), dto.getPageNum(),dto.getName());
		List<WarehouseTeaMemberItem> teas = WarehouseTeaMemberItem.dao.queryBuyTeaList(dto.getPageSize(), dto.getPageNum(),dto.getName(),dto.getUserId());
		List<BuyTeaListVO> vos = new ArrayList<>();
		BuyTeaListVO vo = null;
		for(WarehouseTeaMemberItem wtm : teas){
			vo = new BuyTeaListVO();
			Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
			vo.setId(tea.getInt("id"));
			vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
			vo.setName(tea.getStr("tea_title"));
			/*WarehouseTeaMemberItem item = WarehouseTeaMemberItem.dao.queryTeaOnPlatform(Constants.USER_TYPE.PLATFORM_USER
																					   ,tea.getInt("id"));*/
			vo.setPrice(StringUtil.toString(wtm.getBigDecimal("price")));
			
		/*	if(wtm != null){
				vo.setPrice(wtm.getBigDecimal("piece_price"));
			}*/
			if(StringUtil.equals(wtm.getStr("size_type_cd"), Constants.TEA_UNIT.PIECE)){
				vo.setSize("片");
			}else{
				vo.setSize("件");
			}
			
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
		
		String date = DateUtil.format(new Date(), "yyyy-MM");
		
		Calendar calendar = Calendar.getInstance();
		//详细数据
		Map<String, OrderAnalysisVO> map1 = new HashMap<>();
		//价格走势
		Map<String, DataListVO> map2 = new HashMap<>();
		//成交走势
		Map<String, DataListVO> map3 = new HashMap<>();
		
		List<String> allMonthDays = DateUtil.getMonthFullDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1);
		OrderAnalysisVO nullVO = null;
		DataListVO nullDataListVo = null;
		DataListVO nullItemModel = null;
		for(String str : allMonthDays){
			nullVO = new OrderAnalysisVO();
			nullVO.setAmount(new BigDecimal("0.00"));
			nullVO.setDate(str);
			nullVO.setQuality(0);
			
			nullDataListVo = new DataListVO();
			nullDataListVo.setKey(str);
			nullDataListVo.setValue(new BigDecimal("0.00"));
			
			nullItemModel = new DataListVO();
			nullItemModel.setValue(new BigDecimal("0.00"));
			nullItemModel.setKey(str);
			
			map1.put(str, nullVO);
			map2.put(str, nullDataListVo);
			map3.put(str, nullItemModel);
		}
		//根据月份筛选出本月成交订单
		//List<Order> orders = Order.dao.queryOrderByTime(date, Constants.ORDER_STATUS.PAY_SUCCESS);
		//成交总额
		//成交总量
		//List<OrderAnalysisVO> vos = new ArrayList<>();
		//OrderAnalysisVO vo = null;
		/*for(Order order : orders){
			vo = new OrderAnalysisVO();
			Timestamp time = order.getTimestamp("create_time");
			int month = time.getMonth()+1;
			int day = time.getDate();
			vo.setDate(month+"月"+day+"日");
			int quality = OrderItem.dao.sumOrderQuality(order.getInt("id")).intValue();
			BigDecimal payAmount = OrderItem.dao.sumOrderAmount(order.getInt("id"));
			if(payAmount != null){
				allAmount = allAmount.add(payAmount);
			}
			vo.setAmount(payAmount);
			allQuality = allQuality + quality;
			vo.setQuality(quality);
			vos.add(vo);
		}*/
		//价格走势，卖
		Calendar now =Calendar.getInstance();  
		now.setTime(new Date());  
		now.set(Calendar.DATE,now.get(Calendar.DATE)-20);
		String n = DateUtil.format(now.getTime(),"yyyy-MM");
		
		List<Record> datas = SaleOrder.dao.queryPriceTrendAvg(n,dto.getTeaId());
		List<DataListVO> list1 = new ArrayList<>();
		
		for(Record record : datas){
			DataListVO vo = map2.get(record.getStr("createTime"));
			if(vo == null){
				vo = new DataListVO();
				vo.setKey(record.getStr("createTime"));
			}
			
			vo.setValue(record.getBigDecimal("quality") == null ? new BigDecimal("0.00") : record.getBigDecimal("quality"));
			map2.put(record.getStr("createTime"), vo);
		}
		for(String k:map2.keySet()){
			DataListVO vs = map2.get(k);
			//System.out.println(vs.getKey()+"=="+vs.getValue());
			list1.add(map2.get(k));
		}
		/*Map<String, BigDecimal> trends = new HashMap<>();
		int size = logs.size();
		if(size != 0){
			TeapriceLog log = logs.get(0);
			BigDecimal price = log.getBigDecimal("price");
			Calendar today =Calendar.getInstance();  
			today.setTime(new Date());  
			
			for(int j=1;j<=20;j++){
				String todayStr = DateUtil.format(today.getTime());
				trends.put(todayStr, price);
				today.set(Calendar.DATE,today.get(Calendar.DATE)-1);
			}
			
			for(TeapriceLog log2 : logs){
				Timestamp timestamp = log2.getTimestamp("create_time");
				String dt = DateUtil.formatTimestampForDate(timestamp);
				BigDecimal p = log2.getBigDecimal("price");
				
				if(p != price){
					for(String k:trends.keySet()){
						 if(k.compareTo(dt) >= 0){
							 //key小于当前时间，重置为当前值
							 trends.put(k, p);
						 }
					}
				}
			}
		}*/
		
		//成交走势
		List<Record> records = Order.dao.queryBargainTrendAvg(n,dto.getTeaId());
		
		List<OrderAnalysisVO> vos = new ArrayList<>();
		
		List<DataListVO> models = new ArrayList<>();
		for(Record record : records){
			String dateStr = record.getStr("createTime");
			BigDecimal bprice = (record.getBigDecimal("price") == null ? new BigDecimal("0.00") : record.getBigDecimal("price"));
			BigDecimal bquality = (record.getBigDecimal("quality") == null? new BigDecimal("0.00") : record.getBigDecimal("quality"));
			
			DataListVO iModel = map3.get(dateStr);
			if(iModel == null){
				iModel = new DataListVO();
				iModel.setKey(dateStr);
			}
			if(bprice == null){
				iModel.setValue(new BigDecimal("0.00"));
			}else{
				iModel.setValue(bprice);
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
				vo2.setAmount(bprice);
			}
			vo2.setQuality(StringUtil.toInteger(StringUtil.toString(bquality)));
			map1.put(dateStr, vo2);
		}
		
		for(String k:map3.keySet()){
			models.add(map3.get(k));
		}
		for(String k:map1.keySet()){
			vos.add(map1.get(k));
		}
		
		//成交走势，买
		/*List<OrderItemModel> items = OrderItem.dao.queryPriceAnalysis(dto.getTeaId()
														  ,DateUtil.format(now.getTime())+" 00:00:00"
														  ,DateUtil.format(new Date())+" 23:59:59");
		
		
		
		List<OrderItemModel> models = new ArrayList<>();
		OrderItemModel itemModel = null;
		for(Object item : items){	
			Object[] obj = (Object[])item;
			itemModel = new OrderItemModel();
			itemModel.setAmount((BigDecimal)obj[0]);
			itemModel.setDate((String)obj[1]);
			models.add(itemModel);
		}
		
		Map<String, BigDecimal> bargainTrend = new HashMap<>();
		int size2 = models.size();
		if(size2 != 0){
			OrderItemModel model = models.get(0);
			BigDecimal price = model.getAmount();
			Calendar today =Calendar.getInstance();  
			today.setTime(new Date());  
			
			for(int j=1;j<=20;j++){
				String todayStr = DateUtil.format(today.getTime());
				bargainTrend.put(todayStr, price);
				today.set(Calendar.DATE,today.get(Calendar.DATE)-1);
			}
			
			for(OrderItemModel m : models){
				String dt = m.getDate();
				BigDecimal p = m.getAmount();
				if(p != price){
					for(String k:bargainTrend.keySet()){
						 if(k.compareTo(dt) >= 0){
							 //key小于当前时间，重置为当前值
							 bargainTrend.put(k, p);
						 }
					}
				}
			}
		}*/
		
		//详细数据
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		Map<String,Object> map = new HashMap<>();
		Collections.sort(vos);
		map.put("data", vos);
		
		//查询成交总量和成交总额
		List<Record> records2 = Order.dao.queryBargainSum(date, dto.getTeaId());
		if((records2 != null)&&(records2.size() != 0)){
			Record record0 = records2.get(0);
			map.put("allQuality", record0.getBigDecimal("quality"));
			map.put("allAmount", record0.getBigDecimal("amount"));
		}
		
		/*DataListVO v = null;
		for(String k:trends.keySet()){
			v = new DataListVO();
			v.setKey(k);
			v.setValue(trends.get(k));
			list1.add(v);
		}*/
		
		/*List<DataListVO> list2 = new ArrayList<>();
		DataListVO v2 = null;
		for(String k:bargainTrend.keySet()){
			v2 = new DataListVO();
			v2.setKey(k);
			v2.setValue(bargainTrend.get(k));
			list2.add(v2);
		}*/
		
		/*KeyValueComparator mc = new KeyValueComparator() ; 
		Collections.sort(list1, mc) ; */
		/*
		KeyValueComparator mc2 = new KeyValueComparator() ; 
		Collections.sort(list2, mc2) ; 
		*/
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
			size = tea.getInt("size");
			vo.setName(tea.getStr("tea_title"));
			vo.setSize(size);
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
		if(teaPrice != null){
			
			BigDecimal pieceFromPrice = teaPrice.getBigDecimal("from_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("from_price");
			BigDecimal pieceToPrice = teaPrice.getBigDecimal("to_price") == null ? new BigDecimal("0") : teaPrice.getBigDecimal("to_price");
			
			pieceModel.setPriceStr(pieceFromPrice+"元/片-"+pieceToPrice+"元/片");
			pieceModel.setSizeTypeCd(Constants.TEA_UNIT.PIECE);
			
			BigDecimal itemFromPrice = new BigDecimal("0");
			BigDecimal itemToPrice = new BigDecimal("0");
			if(pieceFromPrice.compareTo(new BigDecimal("0"))==1){
				itemFromPrice = pieceFromPrice.multiply(new BigDecimal(tea.getInt("size")));
			}
			if(pieceToPrice.compareTo(new BigDecimal("0"))==1){
				itemToPrice = pieceToPrice.multiply(new BigDecimal(tea.getInt("size")));
			}
			itemModel.setPriceStr(itemFromPrice+"元/件-"+itemToPrice+"元/件");
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
		BigDecimal serviceFee = onePoint.multiply(salePrice.multiply(new BigDecimal(saleNum)));
		if(userAmount.compareTo(serviceFee)==-1){
			//余额不足
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("您的账号余额不足，不足以支付1%服务费，请先充值");
			return data;
		}
		
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
				BigDecimal pieceFromPrice = teaPrice.getBigDecimal("from_price");
				BigDecimal pieceToPrice = teaPrice.getBigDecimal("to_price");
				System.out.println(pieceFromPrice+","+pieceToPrice+",售价"+salePrice);
				if((salePrice.compareTo(pieceFromPrice)==-1)||(salePrice.compareTo(pieceToPrice))==1){
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价须在参考价范围内");
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
				BigDecimal itemFromPrice = teaPrice.getBigDecimal("from_price").multiply(new BigDecimal(tea.getInt("size")));
				BigDecimal itemToPrice = teaPrice.getBigDecimal("to_price").multiply(new BigDecimal(tea.getInt("size")));
				System.out.println(itemFromPrice+","+itemToPrice+",售价"+salePrice);
				if((salePrice.compareTo(itemFromPrice)==-1)||(salePrice.compareTo(itemToPrice))==1){
					data.setCode(Constants.STATUS_CODE.FAIL);
					data.setMessage("对不起，出售单价须在参考价范围内");
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
						int rets = Member.dao.updateMoneys(userId, userAmount.subtract(serviceFee));
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
						}
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
		String orderNo = dto.getOrderNo();
		if(StringUtil.isBlank(orderNo)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，订单不存在");
			return data;
		}
		SaleOrder saleOrder = SaleOrder.dao.queryByOrderNo(orderNo);
		if(saleOrder == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，订单不存在");
			return data;
		}
		int orderId = saleOrder.getInt("id");
		SaleOrder order = new SaleOrder();
		order.set("id", orderId);
		order.set("status", Constants.ORDER_STATUS.RESET_ORDER);
		order.set("update_time", DateUtil.getNowTimestamp());
		boolean update = SaleOrder.dao.updateInfo(order);
		if(update){
			//增加库存
			int wtmId = saleOrder.getInt("warehouse_tea_member_id");
			int stock = saleOrder.getInt("quality");
			boolean ret = WarehouseTeaMember.dao.updateStock(wtmId, stock);
			if(ret){
				//更新在售茶叶状态
				int wtmItemId = saleOrder.getInt("wtm_item_id");
				int rets = WarehouseTeaMemberItem.dao.updateStatus(wtmItemId, Constants.TEA_STATUS.STOP_SALE);
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
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("撤单失败");
		}
		return data;
	}
	
	public ReturnData queryTeaStoreList(LoginDTO dto){
		
		ReturnData data = new ReturnData();
		
		double localLongtitude = Double.valueOf(dto.getLocalLongtitude());
		double localLatitude = Double.valueOf(dto.getLocalLatitude());
		CodeMst distance = CodeMst.dao.queryCodestByCode(Constants.DEFAULT_SETTING.MAP_DISTANCE);
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
													 ,minLatitude);
		
		
		List<TeaStoreListVO> list = new ArrayList<>();
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
		return data;
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
		Map<String, Object> map = new HashMap<>();
		map.put("store", vo);
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
			bankcard.set("id_card_no", dto.getIdCardNo());
			bankcard.set("stay_mobile", dto.getMobile());
			bankcard.set("member_id", dto.getUserId());
			bankcard.set("create_time", DateUtil.getNowTimestamp());
			bankcard.set("update_time", DateUtil.getNowTimestamp());
			bankcard.set("card_img", dto.getIcon());
			bankcard.set("status",Constants.BIND_BANKCARD_STATUS.APPLING);
			bankcard.set("open_bank_name", dto.getOpenBankName());
			boolean ret = MemberBankcard.dao.updateInfo(bankcard);
			if(ret){
				int retValue = Member.dao.updateIdCardInfo(dto.getUserId(), dto.getIdCardNo(), dto.getIdCardImg());
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
				int retValue = Member.dao.updateIdCardInfo(dto.getUserId(), dto.getIdCardNo(), dto.getIdCardImg());
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
			BankCardRecord record = new BankCardRecord();
			record.set("member_id", dto.getUserId());
			record.set("moneys", money);
			record.set("type_cd", Constants.BANK_MANU_TYPE_CD.WITHDRAW);
			record.set("card_no", cardNo);
			record.set("status", Constants.WITHDRAW_STATUS.APPLYING);
			record.set("create_time", DateUtil.getNowTimestamp());
			record.set("update_time", DateUtil.getNowTimestamp());
			
			boolean ret = BankCardRecord.dao.saveInfo(record);
			if(ret){
				Member member2 = new Member();
				member2.set("id", dto.getUserId());
				BigDecimal openingMoneys = moneys;
				BigDecimal closingMoneys = moneys.subtract(money);
				member2.set("moneys", closingMoneys);
				boolean ret1 = Member.dao.updateInfo(member2);
				if(ret1){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
					data.setMessage("提现申请成功，请等待平台打款");
					//提现
					CashJournal cash = new CashJournal();
					cash.set("cash_journal_no", StringUtil.getOrderNo());
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
		List<SaleOrder> list = SaleOrder.dao.queryMemberOrders(dto.getUserId()
															  ,dto.getPageSize()
															  ,dto.getPageNum()
															  ,Constants.ORDER_STATUS.ON_SALE);
		
		List<SaleOrderListVO> vos = new ArrayList<>();
		SaleOrderListVO vo = null;
		for(SaleOrder order : list){
			vo = new SaleOrderListVO();
			vo.setOrderNo(order.getStr("order_no"));
			vo.setAmount(order.getBigDecimal("price").multiply(new BigDecimal(order.getInt("quality"))));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			if(wtm!=null){
				int teaId = wtm.getInt("tea_id");
				Tea tea = Tea.dao.queryById(teaId);
				if(tea != null){
					vo.setImg(StringUtil.getTeaIcon(tea.getStr("cover_img")));
					vo.setName(tea.getStr("tea_title"));
				}
			}
			vo.setPrice(order.getBigDecimal("price"));
			vo.setQuality(order.getInt("quality"));
			if(StringUtil.equals(order.getStr("size_type_cd"), Constants.TEA_UNIT.PIECE)){
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
		CodeMst phone = CodeMst.dao.queryCodestByCode(Constants.PHONE.CUSTOM);
		String cardNo = bankcard.getStr("card_no");
		if(StringUtil.isNoneBlank(cardNo)){
			int size = StringUtil.isBlank(cardNo) ? 0 : cardNo.length();
			vo.setCardNo("**** **** **** "+cardNo.substring(size-4, size));
		}
		
		Map<String, Object> map = new HashMap<>();
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
			data.setMessage("对不起，门店不存在");
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
		Member buyUserMember = Member.dao.queryById(dto.getUserId());
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
			boolean save = OrderItem.dao.saveInfo(item2);
			if(save){
				int saleUser = wtm.getInt("member_id");
				int ret = 0;
				//保存成功，买家扣款，卖家
				if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
					//平台卖家账号加钱
					Member saleUserMember = Member.dao.queryById(saleUser);
					ret = Member.dao.updateMoneys(saleUser, all.add(saleUserMember.getBigDecimal("moneys")));
				}else{
					//用户卖家加钱
					User user = User.dao.queryById(saleUser);
					ret = User.dao.updateMoneys(saleUser, all.add(user.getBigDecimal("moneys")));
				}
				int allQuality = 0;
				if(ret != 0){
					//买家扣款
					int rt = Member.dao.updateMoneys(dto.getUserId(), buyUserMember.getBigDecimal("moneys").subtract(all));
					if(rt != 0){
						//下单记录
						CashJournal cash = new CashJournal();
						cash.set("cash_journal_no", StringUtil.getOrderNo());
						cash.set("member_id", dto.getUserId());
						cash.set("pi_type", Constants.PI_TYPE.ADD_ORDER);
						cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
						cash.set("occur_date", new Date());
						cash.set("act_rev_amount", all);
						cash.set("act_pay_amount", all);
						Member member = Member.dao.queryById(dto.getUserId());
						cash.set("opening_balance", member.getBigDecimal("moneys"));
						cash.set("closing_balance", member.getBigDecimal("moneys").subtract(all));
						cash.set("remarks", "下单"+all);
						cash.set("create_time", DateUtil.getNowTimestamp());
						cash.set("update_time", DateUtil.getNowTimestamp());
						CashJournal.dao.saveInfo(cash);
						
						//减少卖家库存
						int update = WarehouseTeaMemberItem.dao.cutTeaQuality(quality, wtmItemId);
						if(update != 0){
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
									}
								}else{
									wtmsMember.set("stock", quality);
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
									message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
									message.set("params", "{id:"+orderId+"}");
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
											message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
											message.set("params", "{id:"+orderId+"}");
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
										message.set("message", "出售"+teaInfo.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
										message.set("params", "{id:"+orderId+"}");
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
				OrderItem.dao.saveInfo(item2);
					int saleUser = wtm.getInt("member_id");
					int ret = 0;
					//保存成功，买家扣款，卖家
					if(StringUtil.equals(wtm.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
						//用户卖家
						Member saleUserMember = Member.dao.queryById(saleUser);
						ret = Member.dao.updateMoneys(saleUser, itemAmount.add(saleUserMember.getBigDecimal("moneys")));
					}else{
						//平台卖家
						User user = User.dao.queryById(saleUser);
						ret = User.dao.updateMoneys(saleUser, itemAmount.add(user.getBigDecimal("moneys")));
					}
					
					if(ret != 0){
						//买家扣款
						int rt = Member.dao.updateMoneys(dto.getUserId(), buyUserMember.getBigDecimal("moneys").subtract(itemAmount));
						if(rt != 0){
							//成功充值记录
							CashJournal cash = new CashJournal();
							cash.set("cash_journal_no", orderNo);
							cash.set("member_id", dto.getUserId());
							cash.set("pi_type", Constants.PI_TYPE.ADD_ORDER);
							cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
							cash.set("occur_date", new Date());
							cash.set("act_rev_amount", itemAmount);
							cash.set("act_pay_amount", itemAmount);
							Member member = Member.dao.queryById(dto.getUserId());
							cash.set("opening_balance", member.getBigDecimal("moneys"));
							cash.set("closing_balance", member.getBigDecimal("moneys").subtract(itemAmount));
							cash.set("remarks", "下单"+itemAmount);
							cash.set("create_time", DateUtil.getNowTimestamp());
							cash.set("update_time", DateUtil.getNowTimestamp());
							CashJournal.dao.saveInfo(cash);
							
							//减少卖家库存
							int update = WarehouseTeaMemberItem.dao.cutTeaQuality(quality, wtmItemId);
							if(update != 0){
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
										}
									}else{
										wtmsMember.set("stock", quality);
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
										message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
										message.set("params", "{id:"+orderId+"}");
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
												message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
												message.set("params", "{id:"+orderId+"}");
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
											message.set("message", "出售"+tea.getStr("tea_title")+"，"+quality+unitStr+"，单价："+item.getBigDecimal("price")+"元");
											message.set("params", "{id:"+orderId+"}");
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
}
