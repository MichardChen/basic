package my.core.vo;

import java.io.Serializable;

public class BankCardDetailVO implements Serializable{

	private String cardImg;
	private String bankName;
	private String cardNo;
	private String status;
	private String statusName;
	
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCardImg() {
		return cardImg;
	}
	public String getBankName() {
		return bankName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardImg(String cardImg) {
		this.cardImg = cardImg;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
}
