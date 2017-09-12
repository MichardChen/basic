package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderAnalysisVO implements Serializable{

	private String date;
	private BigDecimal amount;
	private int quality;
	public String getDate() {
		return date;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public int getQuality() {
		return quality;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	
	
}
