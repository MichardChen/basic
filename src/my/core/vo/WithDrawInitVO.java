package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class WithDrawInitVO implements Serializable{

	private String bankIcon;
	private String bankName;
	private String bankNo;
	
	public String getBankIcon() {
		return bankIcon;
	}
	public String getBankName() {
		return bankName;
	}
	public String getBankNo() {
		return bankNo;
	}
	
	public void setBankIcon(String bankIcon) {
		this.bankIcon = bankIcon;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	
}
