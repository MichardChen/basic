package my.pvcloud.dto;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import my.pvcloud.util.StringUtil;


public class IndexDTO extends BaseDTO implements Serializable{

	private String status;
	private String orderNo;
	private String comment;
	private String commentCode;
	private String adminTypeCd;
	private String appointTime;
	private String appointTypeCd;
	private int adminId;
	private String mark;
	private String sexHid;
	private String userNameHid;
	private String icon;
	private String payModeCd;
	private String discount;
	private String hairdressName;
	private String physicalerName;
	private String endTime;
	private String assistantName;
	private String barberName;
	private int storeId;
	private int productId;
	private int barber;
	private int assistant;
	private int hairdress;
	private int physicaler;
	private String cardNo;
	
	public IndexDTO(){}
		
	public static IndexDTO getInstance(HttpServletRequest request){
		IndexDTO dto = new IndexDTO();
		dto.setHairdress(StringUtil.toInteger(request.getParameter("hairdress")));
		dto.setPhysicaler(StringUtil.toInteger(request.getParameter("physicaler")));
		dto.setProductId(StringUtil.toInteger(request.getParameter("productId")));
		dto.setBarber(StringUtil.toInteger(request.getParameter("barber")));
		dto.setAssistant(StringUtil.toInteger(request.getParameter("assistant")));
		dto.setPageNum(StringUtil.toInteger(request.getParameter("pageNum")));
		dto.setPageSize(StringUtil.toInteger(request.getParameter("pageSize")));
		dto.setUserId(StringUtil.toInteger(request.getParameter("userId")));
		dto.setStatus(request.getParameter("status"));
		dto.setOrderNo(request.getParameter("orderNo"));
		dto.setComment(request.getParameter("comment"));
		dto.setCommentCode(request.getParameter("commentCode"));
		dto.setAdminTypeCd(request.getParameter("adminTypeCd"));
		dto.setAppointTypeCd(request.getParameter("appointTypeCd"));
		dto.setAppointTime(request.getParameter("appointTime"));
		dto.setAdminId(StringUtil.toInteger(request.getParameter("adminId")));
		dto.setUserTypeCd(request.getParameter("userTypeCd"));
		dto.setMark(request.getParameter("mark"));
		dto.setSexHid(request.getParameter("sexHid"));
		dto.setUserNameHid(request.getParameter("userNameHid"));
		dto.setPayModeCd(request.getParameter("payModeCd"));
		dto.setDiscount(request.getParameter("discount"));
		dto.setHairdressName(request.getParameter("hairdressName"));
		dto.setPhysicalerName(request.getParameter("physicalerName"));
		dto.setEndTime(request.getParameter("endTime"));
		dto.setAssistantName(request.getParameter("assistantName"));
		dto.setBarberName(request.getParameter("barberName"));
		dto.setStoreId(StringUtil.toInteger(request.getParameter("storeId")));
		dto.setFlg(StringUtil.toInteger(request.getParameter("flg")));
		dto.setCardNo(request.getParameter("cardNo"));
		return dto;
	}
	
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public int getHairdress() {
		return hairdress;
	}

	public void setHairdress(int hairdress) {
		this.hairdress = hairdress;
	}

	public int getPhysicaler() {
		return physicaler;
	}

	public void setPhysicaler(int physicaler) {
		this.physicaler = physicaler;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getBarber() {
		return barber;
	}

	public void setBarber(int barber) {
		this.barber = barber;
	}

	public int getAssistant() {
		return assistant;
	}

	public void setAssistant(int assistant) {
		this.assistant = assistant;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getAssistantName() {
		return assistantName;
	}

	public void setAssistantName(String assistantName) {
		this.assistantName = assistantName;
	}

	public String getBarberName() {
		return barberName;
	}

	public void setBarberName(String barberName) {
		this.barberName = barberName;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getHairdressName() {
		return hairdressName;
	}

	public void setHairdressName(String hairdressName) {
		this.hairdressName = hairdressName;
	}

	public String getPhysicalerName() {
		return physicalerName;
	}

	public void setPhysicalerName(String physicalerName) {
		this.physicalerName = physicalerName;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getPayModeCd() {
		return payModeCd;
	}

	public void setPayModeCd(String payModeCd) {
		this.payModeCd = payModeCd;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSexHid() {
		return sexHid;
	}

	public void setSexHid(String sexHid) {
		this.sexHid = sexHid;
	}

	public String getUserNameHid() {
		return userNameHid;
	}

	public void setUserNameHid(String userNameHid) {
		this.userNameHid = userNameHid;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(String appointTime) {
		this.appointTime = appointTime;
	}

	public String getAppointTypeCd() {
		return appointTypeCd;
	}

	public void setAppointTypeCd(String appointTypeCd) {
		this.appointTypeCd = appointTypeCd;
	}

	public String getAdminTypeCd() {
		return adminTypeCd;
	}

	public void setAdminTypeCd(String adminTypeCd) {
		this.adminTypeCd = adminTypeCd;
	}

	public String getCommentCode() {
		return commentCode;
	}

	public void setCommentCode(String commentCode) {
		this.commentCode = commentCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
