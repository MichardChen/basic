package my.core.vo;

import java.io.Serializable;

public class AdminEvaluateListModel implements Serializable{

	private int id;
	private String commentUser;
	private String store;
	private String point;
	private String createTime;
	private String commentUserMobile;
	private String storeMobile;
	private String comment;
	private int flg;
	
	
	
	public int getFlg() {
		return flg;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getId() {
		return id;
	}
	public String getCommentUser() {
		return commentUser;
	}
	public String getStore() {
		return store;
	}
	public String getPoint() {
		return point;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getCommentUserMobile() {
		return commentUserMobile;
	}
	public String getStoreMobile() {
		return storeMobile;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setCommentUser(String commentUser) {
		this.commentUser = commentUser;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setCommentUserMobile(String commentUserMobile) {
		this.commentUserMobile = commentUserMobile;
	}
	public void setStoreMobile(String storeMobile) {
		this.storeMobile = storeMobile;
	}
	
	
	
}
