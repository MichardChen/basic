package my.pvcloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import my.app.service.FileService;
import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CashJournal;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.model.User;
import my.pvcloud.model.BankRecordModel;
import my.pvcloud.service.WithDrawService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.ImageTools;
import my.pvcloud.util.ImageZipUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/withdrawInfo", path = "/pvcloud")
public class WithDrawInfoController extends Controller {

	WithDrawService service = Enhancer.enhance(WithDrawService.class);

	int page = 1;
	int size = 10;

	/**
	 * 门店列表
	 */
	public void index() {

		removeSessionAttr("time");
		removeSessionAttr("status");
		removeSessionAttr("mobile");
		Page<BankCardRecord> list = service.queryByPage(page, size);
		ArrayList<BankRecordModel> models = new ArrayList<>();
		BankRecordModel model = null;
		for (BankCardRecord record : list.getList()) {
			model = new BankRecordModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if (member != null) {
				model.setMemberId(member.getInt("id"));
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
				if (memberBankcard != null) {
					model.setName(memberBankcard.getStr("owner_name"));
				}
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
			model.setMark(record.getStr("mark"));
			model.setMarkImg(record.getStr("mark_img"));
			model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status == null ? "" : status.getStr("name"));
			model.setStatusCd(record.getStr("status"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("withdraw.jsp");
	}

	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage() {
		String time = getSessionAttr("time");
		this.setSessionAttr("time", time);
		String s = getSessionAttr("status");
		this.setSessionAttr("status", s);
		String mobile = getSessionAttr("mobile");
		this.setSessionAttr("mobile", mobile);
		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}
		Page<BankCardRecord> list = service.queryByPageParams(page, size, time, s, mobile);
		ArrayList<BankRecordModel> models = new ArrayList<>();
		BankRecordModel model = null;
		for (BankCardRecord record : list.getList()) {
			model = new BankRecordModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if (member != null) {
				model.setMemberId(member.getInt("id"));
				model.setName(member.getStr("mobile"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
				if (memberBankcard != null) {
					model.setName(memberBankcard.getStr("owner_name"));
				}
			}
			model.setMark(record.getStr("mark"));
			model.setMarkImg(record.getStr("mark_img"));
			model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status == null ? "" : status.getStr("name"));
			model.setStatusCd(record.getStr("status"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("withdraw.jsp");
	}

	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage() {
		String title = getSessionAttr("time");
		String ptitle = getPara("time");
		String s = getSessionAttr("status");
		String st = getPara("status");
		title = ptitle;

		// String mobile = getSessionAttr("mobile");
		String mobile = getPara("mobile");

		this.setSessionAttr("time", title);
		this.setSessionAttr("status", st);
		this.setSessionAttr("mobile", mobile);

		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}

		Page<BankCardRecord> list = service.queryByPageParams(page, size, title, st, mobile);
		ArrayList<BankRecordModel> models = new ArrayList<>();
		BankRecordModel model = null;
		for (BankCardRecord record : list.getList()) {
			model = new BankRecordModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if (member != null) {
				model.setMemberId(member.getInt("id"));
				model.setName(member.getStr("mobile"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
				if (memberBankcard != null) {
					model.setName(memberBankcard.getStr("owner_name"));
				}
			}
			model.setMark(record.getStr("mark"));
			model.setMarkImg(record.getStr("mark_img"));
			model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status == null ? "" : status.getStr("name"));
			model.setStatusCd(record.getStr("status"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("withdraw.jsp");
	}
	
	public void updateMarkInit(){
		setAttr("withDrawId", getPara("id"));
		render("withdrawMark.jsp");
	}

	public void updateMark(){
		UploadFile uploadFile1 = getFile("markImg");
		FileService fs=new FileService();
		String logo1 = "";
		int id = StringUtil.toInteger(getPara("id"));

		//上传文件
		if(uploadFile1 != null){
			String uuid = UUID.randomUUID().toString();
			String fileName = uploadFile1.getOriginalFileName();
			String[] names = fileName.split("\\.");
		    File file=uploadFile1.getFile();
		    File t=new File(Constants.FILE_HOST.TEA+uuid+"."+names[1]);
		    logo1 = Constants.HOST.TEA+uuid+"."+names[1];
		    try{
		        t.createNewFile();
		    }catch(IOException e){
		        e.printStackTrace();
		    }
		    
		    fs.fileChannelCopy(file, t);
		    ImageZipUtil.zipWidthHeightImageFile(file, t, ImageTools.getImgWidth(file), ImageTools.getImgHeight(file), 0.5f);
		    file.delete();
		    int saveFlg = BankCardRecord.dao.updateStoreMark(id, logo1, StringUtil.checkCode(getPara("mark")));
		    if(saveFlg != 0){
				setAttr("message", "更新成功");
			}else{
				setAttr("message", "更新失败");
			}
		}else{
			 int saveFlg = BankCardRecord.dao.updateStoreMark(id, "", StringUtil.checkCode(getPara("mark")));
			 if(saveFlg != 0){
				 setAttr("message", "更新成功");
			 }else{
				 setAttr("message", "更新失败");
			}
		}
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "处理提现申请，申请id："+id);
		index();
	}
	
	/**
	 * 更新
	 */
	public void update() {
		try {
			int id = getParaToInt("id");
			String flg = getPara("status");
			int ret = service.updateFlg(id, flg);
			if (ret != 0) {
				CodeMst status = CodeMst.dao.queryCodestByCode(flg);
				if (status != null) {
					Log.dao.saveLogInfo((Integer) getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER,
							"处理提现申请id:" + id + "," + status.getStr("name"));
				}
				if (StringUtil.equals(flg, Constants.WITHDRAW_STATUS.FAIL)) {
					// 失败，要把金额还回到账号
					BankCardRecord bankCardRecord = BankCardRecord.dao.queryById(id);
					if (bankCardRecord != null) {
						Member member = Member.dao.queryById(bankCardRecord.getInt("member_id"));
						BigDecimal moneys = bankCardRecord.getBigDecimal("moneys");
						if (moneys != null) {
							int updateFlg = Member.dao.updateCharge(bankCardRecord.getInt("member_id"), moneys);
							if (updateFlg != 0) {
								// 提现失败
								CashJournal cash = new CashJournal();
								cash.set("cash_journal_no", CashJournal.dao.queryCurrentCashNo());
								cash.set("member_id", bankCardRecord.getInt("member_id"));
								cash.set("pi_type", Constants.PI_TYPE.GET_CASH);
								cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_FAIL);
								cash.set("occur_date", new Date());
								cash.set("act_rev_amount", moneys);
								cash.set("act_pay_amount", moneys);
								cash.set("opening_balance", member.getBigDecimal("moneys"));
								cash.set("closing_balance", member.getBigDecimal("moneys").add(moneys));
								cash.set("remarks", "申请提现" + moneys + "，后台审核失败");
								User admin = User.dao.queryById((Integer) getSessionAttr("agentId"));
								if (admin != null) {
									cash.set("create_by", admin.getInt("user_id"));
									cash.set("update_by", admin.getInt("user_id"));
								}
								cash.set("create_time", DateUtil.getNowTimestamp());
								cash.set("update_time", DateUtil.getNowTimestamp());
								CashJournal.dao.saveInfo(cash);
								setAttr("message", "操作成功");
							} else {
								setAttr("message", "操作失败");
							}
						} else {
							setAttr("message", "操作失败");
						}
					} else {
						setAttr("message", "操作失败");
					}
				}

				if (StringUtil.equals(flg, Constants.WITHDRAW_STATUS.SUCCESS)) {
					// 后台审核提现成功
					BankCardRecord bankCardRecord = BankCardRecord.dao.queryById(id);
					if (bankCardRecord != null) {
						Member member = Member.dao.queryById(bankCardRecord.getInt("member_id"));
						BigDecimal moneys = bankCardRecord.getBigDecimal("moneys");
						if (moneys != null) {
							// 提现失败
							CashJournal cash = new CashJournal();
							cash.set("cash_journal_no", StringUtil.getOrderNo());
							cash.set("member_id", bankCardRecord.getInt("member_id"));
							cash.set("pi_type", Constants.PI_TYPE.GET_CASH);
							cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
							cash.set("occur_date", new Date());
							cash.set("act_rev_amount", moneys);
							cash.set("act_pay_amount", moneys);
							cash.set("opening_balance", member.getBigDecimal("moneys"));
							cash.set("closing_balance", member.getBigDecimal("moneys"));
							cash.set("remarks", "申请提现" + moneys + "，后台审核成功");
							User admin = User.dao.queryById((Integer) getSessionAttr("agentId"));
							if (admin != null) {
								cash.set("create_by", admin.getInt("user_id"));
								cash.set("update_by", admin.getInt("user_id"));
							}
							cash.set("create_time", DateUtil.getNowTimestamp());
							cash.set("update_time", DateUtil.getNowTimestamp());
							CashJournal.dao.saveInfo(cash);
							setAttr("message", "操作成功");
						} else {
							setAttr("message", "操作失败");
						}
					} else {
						setAttr("message", "操作失败");
					}
				}
			} else {
				setAttr("message", "操作失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		index();
	}

	public void exportData() {
		String path = "//home//data//images//excel//提现申请.xls";
		try {
			String time = getPara("time");
			String mobile = getPara("mobile");
			String status = getPara("status");
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
			String[] titles = new String[] { "申请人", "注册号码", "提现金额", "账号余额", "申请时间", "状态" };
			for (int i = 0; i < titles.length; i++) {
				cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles[i]);
			}

			List<BankCardRecord> list = BankCardRecord.dao.exportData(time, mobile, status);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					XSSFRow bodyRow = sheet.createRow(j + 1);

					BankCardRecord record = list.get(j);

					// 申请时间
					cell = bodyRow.createCell(4);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getTimestamp("create_time")));
					Member member = Member.dao.queryById(record.getInt("member_id"));
					if (member != null) {
						// 注册号码
						cell = bodyRow.createCell(1);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(member.getStr("mobile"));

						// 提现金额
						cell = bodyRow.createCell(2);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(record.getBigDecimal("moneys")));

						MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
						if (memberBankcard != null) {
							// 申请人
							cell = bodyRow.createCell(0);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(memberBankcard.getStr("owner_name"));
						}
					}
					// 账号余额
					cell = bodyRow.createCell(3);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getBigDecimal("balance")));
					CodeMst statusMst = CodeMst.dao.queryCodestByCode(record.getStr("status"));
					if (statusMst != null) {
						// 状态
						cell = bodyRow.createCell(5);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(statusMst == null ? "" : statusMst.getStr("name"));
					}
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
