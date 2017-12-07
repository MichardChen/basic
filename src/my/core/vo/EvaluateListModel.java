package my.core.vo;

import java.io.Serializable;

public class EvaluateListModel implements Serializable{

	private String icon;
	private int point;
	private String comment;
	private String createDate;
	private String userName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIcon(){
		return icon;
	}
	public int getPoint() {
		return point;
	}
	public String getComment() {
		return comment;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
