package my.core.vo;

import java.io.Serializable;

public class DocumentListVO implements Serializable{

	private String title;
	private String documentUrl;
	public String getTitle() {
		return title;
	}
	public String getDocumentUrl() {
		return documentUrl;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
}
