package my.core.vo;

import java.io.Serializable;

public class WXPrepayModel implements Serializable{

	private String appId;
	private String partnerId;
	private String prepayId;
	private String packageValue;
	private String nonceStr;
	private String timeStamp;
	private String sign;
	public String getAppId() {
		return appId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public String getSign() {
		return sign;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
}
