package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceListModel implements Serializable{

	private int id;
	private String teaName;
	private String content;
	private String createTime;
	private BigDecimal moneys;
	private String status;
	private String statusName;
	private String invoiceContent;
	
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTeaName() {
		return teaName;
	}
	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public BigDecimal getMoneys() {
		return moneys;
	}
	public void setMoneys(BigDecimal moneys) {
		this.moneys = moneys;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
}
