package my.pvcloud.dto;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import my.pvcloud.util.StringUtil;

public class LoginDTO extends BaseDTO{

	public LoginDTO(){}
	
	public static LoginDTO getInstance(HttpServletRequest request){
		LoginDTO dto = new LoginDTO();
		dto.setMobile(request.getParameter("mobile"));
		dto.setUserTypeCd(request.getParameter("userTypeCd"));
		dto.setUserPwd(request.getParameter("userPwd"));
		dto.setUserId(StringUtil.toInteger(request.getParameter("userId")));
		dto.setToken(request.getParameter("token"));
		dto.setAccessToken(request.getParameter("accessToken"));
		dto.setCode(request.getParameter("code"));
		dto.setOldPwd(request.getParameter("oldPwd"));
		dto.setNewPwd(request.getParameter("newPwd"));
		dto.setUserName(request.getParameter("userName"));
		dto.setUserPwd(request.getParameter("userPwd"));
		dto.setRememberMe(StringUtil.toInteger(request.getParameter("rememberMe")));
		dto.setNickName(request.getParameter("nickName"));
		dto.setSex(StringUtil.toInteger(request.getParameter("sex")));
		dto.setStore(StringUtil.toInteger(request.getParameter("store")));
		dto.setPageNum(StringUtil.toInteger(request.getParameter("pageNum")));
		dto.setPageSize(StringUtil.toInteger(request.getParameter("pageSize")));
		dto.setNewsId(StringUtil.toInteger(request.getParameter("newsId")));
		dto.setDeviceToken(request.getParameter("deviceToken"));
		dto.setQq(request.getParameter("qq"));
		dto.setWx(request.getParameter("wx"));
		dto.setCardNo(request.getParameter("cardNo"));
		dto.setProvinceId(StringUtil.toInteger(request.getParameter("provinceId")));
		dto.setCityId(StringUtil.toInteger(request.getParameter("cityId")));
		dto.setDistrictId(StringUtil.toInteger(request.getParameter("districtId")));
		dto.setReceiveMan(request.getParameter("receiveMan"));
		dto.setLinkMan(request.getParameter("linkMan"));
		dto.setAddress(request.getParameter("address"));
		dto.setId(StringUtil.toInteger(request.getParameter("id")));
		dto.setFlg(StringUtil.toInteger(request.getParameter("flg")));
		dto.setFeedBack(request.getParameter("feedBack"));
		dto.setVersion(request.getParameter("version"));
		dto.setPlatForm(request.getParameter("platForm"));
		dto.setVersionTypeCd(request.getParameter("versionTypeCd"));
		dto.setType(request.getParameter("typeCd"));
		dto.setQuality(StringUtil.toInteger(request.getParameter("quality")));
		dto.setTeaId(StringUtil.toInteger(request.getParameter("teaId")));
		dto.setCartId(StringUtil.toInteger(request.getParameter("cartId")));
		dto.setBuyCartIds(request.getParameter("buyCartIds"));
		dto.setDate(request.getParameter("date"));
		dto.setSize(request.getParameter("size"));
		dto.setName(request.getParameter("name"));
		dto.setWareHouseId(StringUtil.toInteger(request.getParameter("wareHouseId")));
		dto.setPriceType(request.getParameter("priceFlg"));
		dto.setPrice(StringUtil.toBigDecimal(request.getParameter("price")));
		
		return dto;
	}
	
	private BigDecimal price;
	private int wareHouseId;
	private String priceType;
	private String name;
	private String size;
	private int cartId;
	private String type;
	private String versionTypeCd;
	private String version;
	private String feedBack;
	private int sex;
	private String userName;
	private String userPwd;
	private int rememberMe;
	private String code;
	private String oldPwd;
	private String newPwd;
	private String nickName;
	private int adminId;
	private int store;
	private int newsId;
	private String deviceToken;
	private String icon;
	private String qq;
	private String wx;
	private String cardNo;
	private int provinceId;
	private int cityId;
	private int districtId;
	private String receiveMan;
	private String linkMan;
	private String address;
	private int id;
	private int quality;
	private int teaId; 
	private String buyCartIds;
	private String date;
	
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(int wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBuyCartIds() {
		return buyCartIds;
	}

	public void setBuyCartIds(String buyCartIds) {
		this.buyCartIds = buyCartIds;
	}

	public int getCartId() {
		return cartId;
	}

	public void setCartId(int cartId) {
		this.cartId = cartId;
	}

	public int getQuality() {
		return quality;
	}

	public int getTeaId() {
		return teaId;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersionTypeCd() {
		return versionTypeCd;
	}

	public void setVersionTypeCd(String versionTypeCd) {
		this.versionTypeCd = versionTypeCd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getReceiveMan() {
		return receiveMan;
	}

	public void setReceiveMan(String receiveMan) {
		this.receiveMan = receiveMan;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public int getCityId() {
		return cityId;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public String getQq() {
		return qq;
	}

	public String getWx() {
		return wx;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public void setWx(String wx) {
		this.wx = wx;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public int getStore() {
		return store;
	}

	public void setStore(int store) {
		this.store = store;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public int getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(int rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
