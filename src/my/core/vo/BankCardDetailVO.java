package my.core.vo;

import java.io.Serializable;

public class BankCardDetailVO implements Serializable{

	private String cardImg;
	private String bankName;
	private String cardNo;
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