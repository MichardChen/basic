package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceDetailVO implements Serializable{

	private String invoiceStatus;
	private String expressName;
	private String expressNo;
	private int updateBy;
	private String type;
	private String titleType;
	private String title;
	private String invoiceNo;
	private String taxNo;
	private String content;
	private BigDecimal moneys;
	private String mark;
	private String bank;
	private String account;
	private String mail;
	
	public String getInvoiceStatus() {
		return invoiceStatus;
	}
	public String getExpressName() {
		return expressName;
	}
	public String getExpressNo() {
		return expressNo;
	}
	public int getUpdateBy() {
		return updateBy;
	}
	public String getType() {
		return type;
	}
	public String getTitleType() {
		return titleType;
	}
	public String getTitle() {
		return title;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public String getContent() {
		return content;
	}
	public BigDecimal getMoneys() {
		return moneys;
	}
	public String getMark() {
		return mark;
	}
	public String getBank() {
		return bank;
	}
	public String getAccount() {
		return account;
	}
	public String getMail() {
		return mail;
	}
	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setTitleType(String titleType) {
		this.titleType = titleType;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setMoneys(BigDecimal moneys) {
		this.moneys = moneys;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
}
