package my.core.model;

import java.io.Serializable;

/**
 * 页面分页参数模型
 * @author Chen Dang
 * @date 2016年7月22日 上午10:27:23 
 * @version 1.0
 * @Description:
 */
public class PageData implements Serializable{

	private int pageIndex = 1;
	private int pageMax;
	private String status;
	 
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageMax() {
		return pageMax;
	}
	public void setPageMax(int pageMax) {
		this.pageMax = pageMax;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
