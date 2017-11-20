package my.core.vo;

import java.io.Serializable;

import com.sun.org.apache.xml.internal.serializer.utils.StringToIntTable;

public class BankCardVO implements Serializable{

	private String bankCardNo;
	private String openBankName;
	private String openBankCd;
	private String openBrunchBank;
	private String mobile;
	private String bankImg;
	private String userName;
	private String idCardNo;
	private String idCardImg;
	
	public String getOpenBankCd() {
		return openBankCd;
	}
	public void setOpenBankCd(String openBankCd) {
		this.openBankCd = openBankCd;
	}
	public String getBankCardNo() {
		return bankCardNo;
	}
	public String getOpenBankName() {
		return openBankName;
	}
	public String getOpenBrunchBank() {
		return openBrunchBank;
	}
	public String getMobile() {
		return mobile;
	}
	public String getBankImg() {
		return bankImg;
	}
	public String getUserName() {
		return userName;
	}
	public String getIdCardNo() {
		return idCardNo;
	}
	public String getIdCardImg() {
		return idCardImg;
	}
	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}
	public void setOpenBrunchBank(String openBrunchBank) {
		this.openBrunchBank = openBrunchBank;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setBankImg(String bankImg) {
		this.bankImg = bankImg;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}
	public void setIdCardImg(String idCardImg) {
		this.idCardImg = idCardImg;
	}
	
	
}
