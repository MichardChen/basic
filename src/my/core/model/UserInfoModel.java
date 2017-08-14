package my.core.model;

import java.io.File;
import java.io.Serializable;

import com.jfinal.plugin.activerecord.Model;

public class UserInfoModel extends Model<UserInfoModel>{

	private String sexHid;
	private String userNameHid;
	private File uploadFile;
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
	public File getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}
	
}
