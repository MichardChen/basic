package my.core.vo;

import java.io.Serializable;
import java.util.List;

public class TeaDetailModelVO implements Serializable{

	private List<String> img;
	private String name;
	private String stock;
	private String price;
	private int certificateFlg;
	private String comment;
	private String brand;
	private String productPlace;
	private String type;
	private String birthday;
	private String size2;
	private String size;
	private String amount;
	private String saleTime;
	private String customPhone;
	private String descUrl;
	private int id;
	private String status;
	private String unit;
	private String productBusiness;
	private String makeBusiness;
	
	public String getMakeBusiness() {
		return makeBusiness;
	}
	public void setMakeBusiness(String makeBusiness) {
		this.makeBusiness = makeBusiness;
	}
	public String getProductBusiness() {
		return productBusiness;
	}
	public void setProductBusiness(String productBusiness) {
		this.productBusiness = productBusiness;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getImg() {
		return img;
	}
	public void setImg(List<String> img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getCertificateFlg() {
		return certificateFlg;
	}
	public void setCertificateFlg(int certificateFlg) {
		this.certificateFlg = certificateFlg;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getProductPlace() {
		return productPlace;
	}
	public void setProductPlace(String productPlace) {
		this.productPlace = productPlace;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSize2() {
		return size2;
	}
	public void setSize2(String size2) {
		this.size2 = size2;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSaleTime() {
		return saleTime;
	}
	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
	public String getCustomPhone() {
		return customPhone;
	}
	public void setCustomPhone(String customPhone) {
		this.customPhone = customPhone;
	}
	public String getDescUrl() {
		return descUrl;
	}
	public void setDescUrl(String descUrl) {
		this.descUrl = descUrl;
	}
}
