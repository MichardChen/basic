package my.pvcloud.model;

public class PayModel {

	//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
	private String tradeNo=""; //支付宝交易号
	private String outTradeNo=""; //获取订单号
	private String totalFee; //获取总金额
	private String tradeStatus=""; //交易状态 
	private String buyerEmail;
	private String createDate;
	
	public String getTradeNo() {
		return tradeNo;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public String getBuyerEmail() {
		return buyerEmail;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
