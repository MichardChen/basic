package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class WithDrawInitVO implements Serializable{

	private String cardImg;
	private String bankName;
	private String bankNo;
	
	public String getCardImg() {
		return cardImg;
	}
	public void setCardImg(String cardImg) {
		this.cardImg = cardImg;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	
}
