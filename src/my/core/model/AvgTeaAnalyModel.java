package my.core.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class AvgTeaAnalyModel implements Serializable{

	private BigDecimal quality;
	private String createTime;
	public BigDecimal getQuality() {
		return quality;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setQuality(BigDecimal quality) {
		this.quality = quality;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
