package my.core.vo;

import java.io.Serializable;

public class ReferencePriceModel implements Serializable{
	
	private String sizeTypeCd;
	
	private String priceStr;

	public String getSizeTypeCd() {
		return sizeTypeCd;
	}

	public String getPriceStr() {
		return priceStr;
	}

	public void setSizeTypeCd(String sizeTypeCd) {
		this.sizeTypeCd = sizeTypeCd;
	}

	public void setPriceStr(String priceStr) {
		this.priceStr = priceStr;
	}
}
