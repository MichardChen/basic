package my.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class OrderItemModel implements Serializable{

	private BigDecimal amount;
	private String date;
	public BigDecimal getAmount() {
		return amount;
	}
	public String getDate() {
		return date;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
