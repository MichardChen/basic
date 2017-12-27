package my.pvcloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CashJournal;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.pvcloud.model.CashListModel;
import my.pvcloud.service.CashJournalService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.StringUtil;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/cashJournalInfo", path = "/pvcloud")
public class CashJournalController extends Controller {

	CashJournalService service = Enhancer.enhance(CashJournalService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("status");
		removeSessionAttr("type");
		removeSessionAttr("time");
		removeSessionAttr("mobile");
		removeSessionAttr("name");
		Page<CashJournal> list = service.queryByPage(page, size);
		ArrayList<CashListModel> models = new ArrayList<>();
		CashListModel model = null;
		for(CashJournal record : list.getList()){
			model = new CashListModel();
			model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
			if(StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				model.setCreateBy(record.getStr("name"));
				model.setMemberName("平台");
				model.setMobile("4006-119-529");
			}else{
				Member member = Member.dao.queryById(record.getInt("member_id"));
				if(member != null){
					model.setCreateBy(record.getStr("name"));
					model.setMemberName(member.getStr("name"));
					model.setMobile(member.getStr("mobile"));
				}
			}
			CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			if(feeStatus != null){
				model.setFeeStatus(feeStatus.getStr("name"));
			}
			//
			CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(piType != null){
				model.setPiType(piType.getStr("name"));
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("act_rev_amount")));
			model.setRemark(record.getStr("remarks"));
			model.setOrderNo(record.getStr("cash_journal_no"));
			model.setTradeNo(record.getStr("trade_no"));
			model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("cash.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String s=getSessionAttr("status");
		this.setSessionAttr("status",s);
		String type=getSessionAttr("type");
		this.setSessionAttr("type",type);
		String time=getSessionAttr("time");
		this.setSessionAttr("time",time);
		String mobile=getSessionAttr("mobile");
		this.setSessionAttr("mobile",mobile);
		String name=getSessionAttr("name");
		this.setSessionAttr("name",name);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<CashJournal> list = service.queryByPageParams(page, size,type,s,time,mobile,name);
		ArrayList<CashListModel> models = new ArrayList<>();
		CashListModel model = null;
		for(CashJournal record : list.getList()){
			model = new CashListModel();
			model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
			model.setOrderNo(record.getStr("cash_journal_no"));
			model.setTradeNo(record.getStr("trade_no"));
			if(StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				model.setCreateBy(record.getStr("name"));
				model.setMemberName("平台");
				model.setMobile("4006-119-529");
			}else{
				Member member = Member.dao.queryById(record.getInt("member_id"));
				if(member != null){
					model.setCreateBy(record.getStr("name"));
					model.setMemberName(member.getStr("name"));
					model.setMobile(member.getStr("mobile"));
				}
			}
			CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			if(feeStatus != null){
				model.setFeeStatus(feeStatus.getStr("name"));
			}
			CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(piType != null){
				model.setPiType(piType.getStr("name"));
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("act_rev_amount")));
			model.setRemark(record.getStr("remarks"));
			model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("cash.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String status = getSessionAttr("status");
		String pstatus = getPara("status");
		status = pstatus;
		this.setSessionAttr("status",status);
		
		String type = getSessionAttr("type");
		String ptype = getPara("type");
		type = ptype;
		this.setSessionAttr("type",type);
		
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		this.setSessionAttr("mobile",mobile);
		
		String name = getSessionAttr("name");
		String pname = getPara("name");
		name = pname;
		this.setSessionAttr("name",name);
		
		String time = getSessionAttr("time");
		String ptime = getPara("time");
		time = ptime;
		this.setSessionAttr("time",time);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<CashJournal> list = service.queryByPageParams(page, size,type,status,time,mobile,name);
			ArrayList<CashListModel> models = new ArrayList<>();
			CashListModel model = null;
			for(CashJournal record : list.getList()){
				model = new CashListModel();
				model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
				
				model.setOrderNo(record.getStr("cash_journal_no"));
				model.setRemark(record.getStr("remarks"));
				model.setTradeNo(record.getStr("trade_no"));
				if(StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
					model.setCreateBy(record.getStr("name"));
					model.setMemberName("平台");
					model.setMobile("4006-119-529");
				}else{
					Member member = Member.dao.queryById(record.getInt("member_id"));
					if(member != null){
						model.setCreateBy(record.getStr("name"));
						model.setMemberName(member.getStr("name"));
						model.setMobile(member.getStr("mobile"));
					}
				}
				
				CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
				if(feeStatus != null){
					model.setFeeStatus(feeStatus.getStr("name"));
				}
				CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
				if(piType != null){
					model.setPiType(piType.getStr("name"));
				}
				model.setMoneys(StringUtil.toString(record.getBigDecimal("act_rev_amount")));
				model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("cash.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
	}
	
	public void exportData() {
		String path = "//home//data//images//excel//资金记录.xls";
		try {
			String pstatus = getPara("status");
			String ptype = getPara("type");
			String pmobile = getPara("mobile");
			String pname = getPara("name");
			String ptime = getPara("time");
			
			FileOutputStream os = new FileOutputStream(new File(path));
			// 创建一个workbook 对应一个excel应用文件
			XSSFWorkbook workBook = new XSSFWorkbook();
			// 在workbook中添加一个sheet,对应Excel文件中的sheet
			XSSFSheet sheet = workBook.createSheet("提现申请");
			ExportUtil exportUtil = new ExportUtil(workBook, sheet);
			XSSFCellStyle headStyle = exportUtil.getHeadStyle();
			XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();

			XSSFRow headRow = sheet.createRow(0);
			XSSFCell cell = null;
			String[] titles = new String[] {"用户名", "注册手机号码", "订单号", "流水账单号", "类型", "状态","时间","金额","期初金额","期末金额","备注"};
			for (int i = 0; i < titles.length; i++) {
				cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles[i]);
			}

			List<CashJournal> list = CashJournal.dao.exportData(ptype,pstatus,ptime,pmobile,pname);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					XSSFRow bodyRow = sheet.createRow(j + 1);

					CashJournal record = list.get(j);

					// 申请时间
					cell = bodyRow.createCell(4);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getTimestamp("create_time")));
					//期末金额
					cell = bodyRow.createCell(9);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getBigDecimal("closing_balance")));
					//时间
					cell = bodyRow.createCell(6);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(DateUtil.format(record.getDate("occur_date")));
					
					//订单号
					cell = bodyRow.createCell(2);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(record.getStr("trade_no"));
					//流水单号
					cell = bodyRow.createCell(3);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(record.getStr("cash_journal_no"));
					//备注
					cell = bodyRow.createCell(10);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(record.getStr("remarks"));
					if(StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
						//用户名
						cell = bodyRow.createCell(0);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("平台");
						//手机号码
						cell = bodyRow.createCell(1);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("4006-119-529");
					}else{
						Member member = Member.dao.queryById(record.getInt("member_id"));
						if(member != null){
							//用户名
							cell = bodyRow.createCell(0);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(member.getStr("name"));
							//手机号码
							cell = bodyRow.createCell(1);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(member.getStr("mobile"));
						}
					}
					
					CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
					if(feeStatus != null){
						//状态
						cell = bodyRow.createCell(5);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(feeStatus.getStr("name"));
					}else{
						cell = bodyRow.createCell(5);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("");
					}
					CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
					if(piType != null){
						//类型
						cell = bodyRow.createCell(4);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(piType.getStr("name"));
					}else{
						cell = bodyRow.createCell(4);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("");
					}
					//类型
					cell = bodyRow.createCell(4);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(piType.getStr("name"));
					//金额
					cell = bodyRow.createCell(7);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getBigDecimal("act_rev_amount")));
					//期初金额
					cell = bodyRow.createCell(8);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getBigDecimal("opening_balance")));
				}
			}
			workBook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 判断路径是否存在
		if (new File(path).isFile()) {
			renderFile(new File(path));
		} else {
			renderNull();
		}
	}
}
