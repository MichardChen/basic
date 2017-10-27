package my.core.vo;

import java.math.BigDecimal;

public class DataListVO implements Comparable<DataListVO>{

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
	
	public int compareTo(DataListVO arg0) {
        return this.getKey().compareTo(arg0.getKey());
    }
	
}
