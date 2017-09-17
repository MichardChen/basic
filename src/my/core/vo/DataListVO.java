package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class DataListVO implements Serializable{

	private String key;
	private BigDecimal value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	
}
