package my.pvcloud.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TeaPriceModel implements Serializable{

	private BigDecimal fromPrice;
	private BigDecimal toPrice;
	private String date;
	public BigDecimal getFromPrice() {
		return fromPrice;
	}
	public BigDecimal getToPrice() {
		return toPrice;
	}
	public String getDate() {
		return date;
	}
	public void setFromPrice(BigDecimal fromPrice) {
		this.fromPrice = fromPrice;
	}
	public void setToPrice(BigDecimal toPrice) {
		this.toPrice = toPrice;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
