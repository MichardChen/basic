package my.core.vo;

import java.io.Serializable;

public class WXPrepayModel implements Serializable{

	private String resultCode;
	private String sign;
	private String mchId;
	private String prepayId;
	private String returnMsg;
	private String appId;
	private String nonceStr;
	private String returnCode;
	private String tradeType;
	public String getResultCode() {
		return resultCode;
	}
	public String getSign() {
		return sign;
	}
	public String getMchId() {
		return mchId;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public String getAppId() {
		return appId;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	
}
