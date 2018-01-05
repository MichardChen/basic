package my.core.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class FeeModel implements Serializable{

	private BigDecimal applying;
	private BigDecimal applySuccess;
	private BigDecimal paySuccess;
	public BigDecimal getApplying() {
		return applying;
	}
	public BigDecimal getApplySuccess() {
		return applySuccess;
	}
	public BigDecimal getPaySuccess() {
		return paySuccess;
	}
	public void setApplying(BigDecimal applying) {
		this.applying = applying;
	}
	public void setApplySuccess(BigDecimal applySuccess) {
		this.applySuccess = applySuccess;
	}
	public void setPaySuccess(BigDecimal paySuccess) {
		this.paySuccess = paySuccess;
	}
	
	
}
