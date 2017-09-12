package my.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class OrderItemModel implements Serializable{

	private BigDecimal amount;
	private Date date;
	public BigDecimal getAmount() {
		return amount;
	}
	public Date getDate() {
		return date;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
