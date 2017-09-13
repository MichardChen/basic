package my.core.vo;

import java.io.Serializable;

public class WarehouseStockVO implements Serializable{

	private int warehouseTeaId;
	private String wareHouse;
	private int stock;
	private int pieceFlg;
	private int itemFlg;
	private int maxPiece;
	private int maxItem;
	
	public int getMaxPiece() {
		return maxPiece;
	}
	public int getMaxItem() {
		return maxItem;
	}
	public void setMaxPiece(int maxPiece) {
		this.maxPiece = maxPiece;
	}
	public void setMaxItem(int maxItem) {
		this.maxItem = maxItem;
	}
	public int getPieceFlg() {
		return pieceFlg;
	}
	public int getItemFlg() {
		return itemFlg;
	}
	public void setPieceFlg(int pieceFlg) {
		this.pieceFlg = pieceFlg;
	}
	public void setItemFlg(int itemFlg) {
		this.itemFlg = itemFlg;
	}
	public int getWarehouseTeaId() {
		return warehouseTeaId;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public int getStock() {
		return stock;
	}
	public void setWarehouseTeaId(int warehouseTeaId) {
		this.warehouseTeaId = warehouseTeaId;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
}
