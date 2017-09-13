package my.core.vo;

import java.io.Serializable;

public class TeaWarehouseDetailVO implements Serializable{

	private int wareHouseTeaId;
	private String wareHouse;
	private int saleQuality;
	private int canGetQuality;
	public int getWareHouseTeaId() {
		return wareHouseTeaId;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public int getSaleQuality() {
		return saleQuality;
	}
	public int getCanGetQuality() {
		return canGetQuality;
	}
	public void setWareHouseTeaId(int wareHouseTeaId) {
		this.wareHouseTeaId = wareHouseTeaId;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setSaleQuality(int saleQuality) {
		this.saleQuality = saleQuality;
	}
	public void setCanGetQuality(int canGetQuality) {
		this.canGetQuality = canGetQuality;
	}
	
	
}
