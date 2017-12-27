package my.pvcloud.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.sun.org.apache.bcel.internal.classfile.Code;

import my.core.constants.Constants;
import my.core.model.City;
import my.core.model.CodeMst;
import my.core.model.District;
import my.core.model.GetTeaRecord;
import my.core.model.Invoice;
import my.core.model.InvoiceGetteaRecord;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WarehouseTeaMember;
import my.core.vo.AdminInvoiceListModel;
import my.pvcloud.model.GetTeaRecordModel;
import my.pvcloud.service.InvoiceService;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/invoiceInfo", path = "/pvcloud")
public class InvoiceController extends Controller {

	InvoiceService service = Enhancer.enhance(InvoiceService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		removeSessionAttr("mobile");
		removeSessionAttr("status");
		Page<Invoice> list = service.queryByPage(page, size);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			int updateById = data.getInt("update_by") == null ? 0 : data.getInt("update_by");
			User user = User.dao.queryById(updateById);
			if(user != null){
				model.setUpdateBy(user.getStr("username"));
			}
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("invoice_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			model.setTitle(data.getStr("title"));
			CodeMst titleTypeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(titleTypeMst != null){
				model.setTitleType(titleTypeMst.getStr("name"));
			}
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst status = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		String mobile = getSessionAttr("mobile");
		this.setSessionAttr("mobile", mobile);
		
		String status = getSessionAttr("status");
		this.setSessionAttr("status", status);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Invoice> list = service.queryByPageParams(page, size,mobile,title,status);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			int updateById = data.getInt("update_by") == null ? 0 : data.getInt("update_by");
			User user = User.dao.queryById(updateById);
			if(user != null){
				model.setUpdateBy(user.getStr("username"));
			}
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("invoice_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			
			CodeMst titleTypeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(titleTypeMst != null){
				model.setTitleType(titleTypeMst.getStr("name"));
			}
			
			
			model.setTitle(data.getStr("title"));
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst st = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(st != null){
				model.setStatus(st.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		
		String status = getSessionAttr("status");
		String pstatus = getPara("status");
		status = pstatus;
		this.setSessionAttr("title",title);
		this.setSessionAttr("status",status);
		this.setSessionAttr("mobile", mobile);
		Integer page = getParaToInt(1);
		if (page==null || page==0) {
			page = 1;
		}
		    
		Page<Invoice> list = service.queryByPageParams(page, size, mobile, title,status);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			int updateById = data.getInt("update_by") == null ? 0 : data.getInt("update_by");
			User user = User.dao.queryById(updateById);
			if(user != null){
				model.setUpdateBy(user.getStr("username"));
			}
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("invoice_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			
			CodeMst titleTypeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(titleTypeMst != null){
				model.setTitleType(titleTypeMst.getStr("name"));
			}
			
			
			model.setTitle(data.getStr("title"));
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst st = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(st != null){
				model.setStatus(st.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	public void editInt(){
		int id = StringUtil.toInteger(getPara("id"));
		Invoice record = Invoice.dao.queryInvoiceById(id);
		setAttr("model", record);
		ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
		if(address != null){
			String addressDetail = "";
			Province province = Province.dao.queryProvince(address.getInt("province_id"));
			City city = City.dao.queryCity(address.getInt("city_id"));
			District district = District.dao.queryDistrict(address.getInt("district_id"));
			if(province != null){
				addressDetail = addressDetail + province.getStr("name");
			}
			if(city != null){
				addressDetail = addressDetail + city.getStr("name");
			}
			if(district != null){
				addressDetail = addressDetail + district.getStr("name");
			}
			addressDetail = addressDetail+address.getStr("address");
			addressDetail = address.getStr("receiveman_name")+" "+address.getStr("mobile")+" "+addressDetail;
			setAttr("address", addressDetail);
		}
		List<CodeMst> express = CodeMst.dao.queryCodestByPcode(Constants.EXPRESS.EXPRESS);
		setAttr("express", express);
		render("editinvoice.jsp");
	}
	
	public void updateInvoice(){
		try{
			int recordId = getParaToInt("id");
			String expressName = StringUtil.checkCode(getPara("expressName"));
			String expressNo = StringUtil.checkCode(getPara("expressNo"));
			String status = StringUtil.checkCode(getPara("status"));
			//新增
			String typeCd = StringUtil.checkCode(getPara("typeCd"));
			String titleTypeCd = StringUtil.checkCode(getPara("titleTypeCd"));
			String title = StringUtil.checkCode(getPara("title"));
			String invoiceNo = StringUtil.checkCode(getPara("invoiceNo"));
			String taxNo = StringUtil.checkCode(getPara("taxNo"));
			String content = StringUtil.checkCode(getPara("content"));
			BigDecimal moneys = StringUtil.toBigDecimal(StringUtil.checkCode(getPara("moneys")));
			String mark = StringUtil.checkCode(getPara("mark"));
			String bank = StringUtil.checkCode(getPara("bank"));
			String account = StringUtil.checkCode(getPara("account"));
			String mail = StringUtil.checkCode(getPara("mail"));
			
			int ret = Invoice.dao.updateInvoice(recordId
											   ,status
											   ,expressName
											   ,expressNo
											   ,(Integer)getSessionAttr("agentId")
											   ,typeCd
											   ,titleTypeCd
											   ,title
											   ,invoiceNo
											   ,taxNo
											   ,content
											   ,moneys
											   ,mark
											   ,bank
											   ,account
											   ,mail);
			if(ret!=0){
				//更新取茶记录状态
				InvoiceGetteaRecord invoiceGetteaRecord = InvoiceGetteaRecord.dao.queryByInvoiceId(recordId);
				if(invoiceGetteaRecord != null){
					int id = invoiceGetteaRecord.getInt("gettea_record_id");
					if(StringUtil.equals(status, Constants.INVOICE_STATUS.INVOICED)){
						//已开票
						GetTeaRecord.dao.updateInvoice(id, Constants.INVOICE_STATUS.INVOICED);
					}else if(StringUtil.equals(status, Constants.INVOICE_STATUS.NOT_INVOICE)){
						//未开票
						GetTeaRecord.dao.updateInvoice(id, Constants.INVOICE_STATUS.STAY_HANDLE);
					}else if(StringUtil.equals(status, Constants.INVOICE_STATUS.STAY_HANDLE)){
						//待处理
						GetTeaRecord.dao.updateInvoice(id, Constants.INVOICE_STATUS.STAY_HANDLE);
					}
				}
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新开票信息id:"+recordId);
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	public void exportData(){
		 String path = "//home//data//images//excel//开票数据.xls";
		 try {  
			
		 FileOutputStream os = new FileOutputStream(new File(path));  
		// 创建一个workbook 对应一个excel应用文件  
	        XSSFWorkbook workBook = new XSSFWorkbook();  
	        // 在workbook中添加一个sheet,对应Excel文件中的sheet  
	        XSSFSheet sheet = workBook.createSheet("营业报表明细");  
	       ExportUtil exportUtil = new ExportUtil(workBook, sheet);
	       XSSFCellStyle headStyle = exportUtil.getHeadStyle();  
	        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle(); 
	        
	        XSSFRow headRow = sheet.createRow(0);  
	        XSSFCell cell = null;  
	        String[] titles = new String[]{"用户名","注册手机号码","开票金额","发票抬头","抬头类型","发票类型","税务单号","备注","处理者","申请状态","申请时间"};
	        for (int i = 0; i < titles.length; i++){  
	            cell = headRow.createCell(i);  
	            cell.setCellStyle(headStyle);  
	            cell.setCellValue(titles[i]);  
	        }  
	        
			String title = getPara("title");
			String mobile = getPara("mobile");
			String status = getPara("status");
			
			List<Invoice> list = Invoice.dao.queryByPageParams(mobile, title, status);
	        if (list != null && list.size() > 0){  
	            for (int j = 0; j < list.size(); j++){  
	            	XSSFRow bodyRow = sheet.createRow(j + 1);  
	            	Invoice model = list.get(j);
	                //日期
	            	int memberId = model.getInt("user_id");
					Member member = Member.dao.queryById(memberId);
					if(member != null){
						 cell = bodyRow.createCell(0);  
			             cell.setCellStyle(bodyStyle);  
			             cell.setCellValue(member.getStr("nick_name"));
			                
			             cell = bodyRow.createCell(1);  
			             cell.setCellStyle(bodyStyle);  
			             cell.setCellValue(member.getStr("mobile"));
					}
					
					cell = bodyRow.createCell(2);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(model.getBigDecimal("moneys")));
		            
		            cell = bodyRow.createCell(3);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(model.getStr("title"));
		             
					CodeMst typeMst = CodeMst.dao.queryCodestByCode(model.getStr("title_type_cd"));
					if(typeMst != null){
						cell = bodyRow.createCell(4);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue(typeMst.getStr("name"));
					}else{
						cell = bodyRow.createCell(4);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue("");
					}
					
					CodeMst invoicetypeMst = CodeMst.dao.queryCodestByCode(model.getStr("invoice_type_cd"));
					if(invoicetypeMst != null){
						cell = bodyRow.createCell(5);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue(invoicetypeMst.getStr("name"));
					}else{
						cell = bodyRow.createCell(5);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue("");
					}
					
					cell = bodyRow.createCell(6);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(model.getStr("tax_no"));
		            
		            cell = bodyRow.createCell(7);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(model.getStr("mark"));
		            
		            int updateById = model.getInt("update_by") == null ? 0 : model.getInt("update_by");
					User user = User.dao.queryById(updateById);
					if(user != null){
						cell = bodyRow.createCell(8);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue(user.getStr("username"));
					}else{
						cell = bodyRow.createCell(8);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue("");
					}
					
					CodeMst st = CodeMst.dao.queryCodestByCode(model.getStr("status"));
					if(st != null){
						cell = bodyRow.createCell(9);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue(st.getStr("name"));
					}else{
						cell = bodyRow.createCell(9);  
			            cell.setCellStyle(bodyStyle);  
			            cell.setCellValue("");
					}
					
	                cell = bodyRow.createCell(10);  
	                cell.setCellStyle(bodyStyle);  
	                cell.setCellValue(StringUtil.toString(model.getTimestamp("create_time")));
	            }
	        }
	        workBook.write(os);  
	       }catch(Exception e){  
	        e.printStackTrace();  
	       }  
	      //判断路径是否存在  
	     if(new File(path).isFile()){  
	        renderFile(new File(path));  
	      }else{  
	        renderNull();  
	      }  
	}
}
