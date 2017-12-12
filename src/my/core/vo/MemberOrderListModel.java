package my.core.vo;

import java.io.Serializable;

public class MemberOrderListModel implements Serializable{

	private int id;
	private String teaName;
	private String buyUserName;
	private String content;
	private String createTime;
	private String amount;
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
	public String getBuyUserName() {
		return buyUserName;
	}
	public void setBuyUserName(String buyUserName) {
		this.buyUserName = buyUserName;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
