package my.pvcloud.controller;

import java.util.Date;

import my.pvcloud.model.CustInfo;
import my.pvcloud.service.CustInfoService;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/custInfo", path = "/pvcloud")
public class CustInfoController extends Controller {

	CustInfoService cuService = Enhancer.enhance(CustInfoService.class);
	
	int page=1;
	int size=10;
	/**
	 * 用户建档
	 */
	public void index(){
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<CustInfo> custInfoList = cuService.queryByPage(page, size,null,null);
		setAttr("custInfoList", custInfoList);
		render("custInfo.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByCondition(){
		try {
			String ccustInfo = getSessionAttr("custInfo");
			String ccustValue = getSessionAttr("custValue");
			
			
			Page<CustInfo> custInfoList = new Page<CustInfo>(null, 0, 0, 0, 0);
			
			String custInfo = getPara("cInfo");
			String custValue = getPara("cValue");
			
			if(("").equals(custInfo) || custInfo==null){
				custInfo = ccustInfo;
			}
			if(("").equals(custValue) || custValue==null){
				custInfo = ccustValue;
			}
			
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
			//用户名称
			if(("addrName").equals(custInfo)){
				custInfoList = cuService.queryByPage(page, size, custInfo,custValue);
			//用户地址
			}else if(("phoneNum").equals(custInfo)){
				custInfoList = cuService.queryByPage(page, size,custInfo, custValue);
			}else{
				custInfoList = cuService.queryByPage(page, size,null,null);
			}
			setAttr("custInfoList", custInfoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("custInfo.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		try {
			
			String custInfo=getSessionAttr("custInfo");
			String custValue=getSessionAttr("custValue");
				
			Page<CustInfo> custInfoList = new Page<CustInfo>(null, 0, 0, 0, 0);	
				
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
			if(custInfo!=null){
				if(("addrName").equals(custInfo)){
					custInfoList = cuService.queryByPage(page, size,custInfo, custValue);
				}else if(("phoneNum").equals(custInfo)){
					custInfoList = cuService.queryByPage(page, size,custInfo, custValue);
				}else{
					custInfoList = cuService.queryByPage(page, size,null,null);
				}
			}else{
				custInfoList = cuService.queryByPage(page, size,null,null);
			}
			setAttr("custInfoList", custInfoList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("custInfo.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("custId");
		int custId = 0;
		if(!("").equals(id) && id!=null){
			custId = getParaToInt("custId");
		}
		CustInfo custInfo = cuService.queryById(custId);
		setAttr("custInfo", custInfo);
		render("custInfoAlter.jsp");
	}
	
	/**
	 * 修改（保存）
	 */
	public void update(){
		String id = getPara("custId");
		int integral = getParaToInt("integral");
		String phoneNum = getPara("phoneNum");
		String addrname = getPara("addrname");
		CustInfo custInfo = new CustInfo();
		int custId = 0;
		if(!("").equals(id) && id!=null){
			custId = getParaToInt("custId");
			custInfo = cuService.queryById(custId);
		}
		custInfo.set("integral", integral);
		custInfo.set("phonenum", phoneNum);
		custInfo.set("addrname", addrname);
		if(custId==0){
			custInfo.set("register_date", new Date());
			if(cuService.saveInfo(custInfo)){
				setAttr("message","新增成功");
			}else{
				setAttr("message", "新增失败");
			}
		}else{
			custInfo.set("update_date", new Date());
			if(cuService.updateInfo(custInfo)){
				setAttr("message","修改成功");
			}else{
				setAttr("message", "修改失败");
			}
		}
		
		index();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int custId = getParaToInt("custId");
			if(cuService.del(custId)){
				setAttr("message", "删除成功");
			}else{
				setAttr("message", "删除失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}

}
