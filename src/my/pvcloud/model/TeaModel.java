package my.pvcloud.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TeaModel implements Serializable{

	private String name;
	private int id;
	private String price;
	private String type;
	private String createTime;
	private String url;
	private String status;
	private int flg;
	private String brand;
	private String productPlace;
	private String size;
	private String amount;
	private String stock;
	private String saleStatus;
	private String statusCd;
	private BigDecimal fromPrice;
	private BigDecimal toPrice;
	private String expireDate;
	private String keyCode;
	private String referencePrice;
	//四个
	//发行件数
	private String saleItems;
	private String syPiece;
	private String originStock;
	private String productBusiness;
	
	
	public String getProductBusiness() {
		return productBusiness;
	}
	public void setProductBusiness(String productBusiness) {
		this.productBusiness = productBusiness;
	}
	public String getSaleItems() {
		return saleItems;
	}
	public String getSyPiece() {
		return syPiece;
	}
	public void setSaleItems(String saleItems) {
		this.saleItems = saleItems;
	}
	public void setSyPiece(String syPiece) {
		this.syPiece = syPiece;
	}
	public String getOriginStock() {
		return originStock;
	}
	public void setOriginStock(String originStock) {
		this.originStock = originStock;
	}
	public String getReferencePrice() {
		return referencePrice;
	}
	public void setReferencePrice(String referencePrice) {
		this.referencePrice = referencePrice;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public BigDecimal getFromPrice() {
		return fromPrice;
	}
	public void setFromPrice(BigDecimal fromPrice) {
		this.fromPrice = fromPrice;
	}
	public BigDecimal getToPrice() {
		return toPrice;
	}
	public void setToPrice(BigDecimal toPrice) {
		this.toPrice = toPrice;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getStatusCd() {
		return statusCd;
	}
	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
	public String getBrand() {
		return brand;
	}
	public String getProductPlace() {
		return productPlace;
	}
	public String getSize() {
		return size;
	}
	public String getAmount() {
		return amount;
	}
	public String getStock() {
		return stock;
	}
	public String getSaleStatus() {
		return saleStatus;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public void setProductPlace(String productPlace) {
		this.productPlace = productPlace;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public void setSaleStatus(String saleStatus) {
		this.saleStatus = saleStatus;
	}
	public int getFlg() {
		return flg;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	
	public String getPrice() {
		return price;
	}
	public String getType() {
		return type;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getUrl() {
		return url;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
