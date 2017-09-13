package my.core.vo;

import java.io.Serializable;

public class SaleTeaDetailVO implements Serializable{

	private int pieceSaleFlg;
	private int itemSaleFlg;
	private String serviceFee;
	public int getPieceSaleFlg() {
		return pieceSaleFlg;
	}
	public int getItemSaleFlg() {
		return itemSaleFlg;
	}
	public String getServiceFee() {
		return serviceFee;
	}
	public void setPieceSaleFlg(int pieceSaleFlg) {
		this.pieceSaleFlg = pieceSaleFlg;
	}
	public void setItemSaleFlg(int itemSaleFlg) {
		this.itemSaleFlg = itemSaleFlg;
	}
	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee;
	}
	
	
}
