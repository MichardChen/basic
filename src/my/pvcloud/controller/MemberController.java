package my.pvcloud.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Invoice;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.MemberStore;
import my.core.model.Message;
import my.core.model.PayRecord;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.vo.MemberVO;
import my.pvcloud.model.CustInfo;
import my.pvcloud.service.MemberService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/memberInfo", path = "/pvcloud")
public class MemberController extends Controller {

	MemberService service = Enhancer.enhance(MemberService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 会员列表
	 */
	public void index(){
		
		//清除查询条件
		removeSessionAttr("cmobile");
		removeSessionAttr("cname");
		removeSessionAttr("storeName");
		removeSessionAttr("type");
		Page<Member> list = service.queryByPage(page, size);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setKeyCode(member.getStr("id_code"));
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("nick_name"));
			model.setUserName(member.getStr("name"));
			CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
			if(roleMst != null){
				model.setRole(roleMst.getStr("name"));
			}
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			//查询用户已提现金额和提现中的金额
			BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
			model.setApplingMoneys(StringUtil.toString(applying));
			BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
			model.setApplyedMoneys(StringUtil.toString(applySuccess));
			BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
			model.setRechargeMoneys(StringUtil.toString(paySuccess));
			MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
			if(memberBankcard == null){
				model.setBankStatus("暂未绑定银行卡");
			}else{
				String bankStatus = memberBankcard.getStr("status");
				CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
				if(sCodeMst != null){
					model.setBankStatus(sCodeMst.getStr("name"));
				}
			}
			
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
			
			Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
			if(store1 != null){
				model.setStoreId(store1.getInt("id"));
				model.setOpenStore(1);
			}else{
				model.setOpenStore(0);
			}
			
			
			
			Store store = Store.dao.queryById(member.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}else{
				model.setStore("");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("member.jsp");
	}
	
	/**
	 * 模糊查询条件分页
	 */
	public void queryByConditionByPage(){
			
		String cmobile = getSessionAttr("cmobile");
		String cname = getSessionAttr("cname");
		String storeName = getSessionAttr("storeName");
		String ctype = getSessionAttr("type");
		
		String mobile = getPara("mobile");
		cmobile = mobile;
		this.setSessionAttr("cmobile",cmobile);
		
		String name = getPara("cname");
		cname = name;
		this.setSessionAttr("cname",cname);
		
		String type = getPara("type");
		ctype = type;
		this.setSessionAttr("type",ctype);
		
		String storeNames = getPara("storeName");
		storeName = storeNames;
		this.setSessionAttr("storeName",storeName);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Member> list = service.queryMemberListByPage(page, size,mobile,name,storeName,ctype);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setKeyCode(member.getStr("id_code"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("nick_name"));
				model.setUserName(member.getStr("name"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
				CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
				if(roleMst != null){
					model.setRole(roleMst.getStr("name"));
				}
				//查询用户已提现金额和提现中的金额
				BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
				model.setApplingMoneys(StringUtil.toString(applying));
				BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
				model.setApplyedMoneys(StringUtil.toString(applySuccess));
				BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
				model.setRechargeMoneys(StringUtil.toString(paySuccess));
				
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
				if(memberBankcard == null){
					model.setBankStatus("暂未绑定银行卡");
				}else{
					String bankStatus = memberBankcard.getStr("status");
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
					if(sCodeMst != null){
						model.setBankStatus(sCodeMst.getStr("name"));
					}
				}
				
				Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
				if(store1 != null){
					model.setStoreId(store1.getInt("id"));
					model.setOpenStore(1);
				}else{
					model.setOpenStore(0);
				}
				
				
				Store store = Store.dao.queryById(member.getInt("store_id"));
				if(store != null){
					model.setStore(store.getStr("store_name"));
				}else{
					model.setStore("");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("member.jsp");
	}
	
	/**
	 * 模糊查询底部页码分页
	 */
	public void queryByPage(){
		try {
			
			String cmobile=getSessionAttr("cmobile");
			this.setSessionAttr("cmobile",cmobile);
			
			String cname=getSessionAttr("cname");
			this.setSessionAttr("cname",cname);
			
			String storeName=getSessionAttr("storeName");
			this.setSessionAttr("storeName",storeName);
			
			String ctype=getSessionAttr("type");
			this.setSessionAttr("type",ctype);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Member> list = service.queryMemberListByPage(page, size,cmobile,cname,storeName,ctype);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setMobile(member.getStr("mobile"));
				model.setKeyCode(member.getStr("id_code"));
				model.setName(member.getStr("nick_name"));
				model.setUserName(member.getStr("name"));
				CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
				if(roleMst != null){
					model.setRole(roleMst.getStr("name"));
				}
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
				//查询用户已提现金额和提现中的金额
				BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
				model.setApplingMoneys(StringUtil.toString(applying));
				BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
				model.setApplyedMoneys(StringUtil.toString(applySuccess));
				BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
				model.setRechargeMoneys(StringUtil.toString(paySuccess));
				
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
				if(memberBankcard == null){
					model.setBankStatus("暂未绑定银行卡");
				}else{
					String bankStatus = memberBankcard.getStr("status");
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
					if(sCodeMst != null){
						model.setBankStatus(sCodeMst.getStr("name"));
					}
				}
				
				Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
				if(store1 != null){
					model.setStoreId(store1.getInt("id"));
					model.setOpenStore(1);
				}else{
					model.setOpenStore(0);
				}
				
				Store store = Store.dao.queryById(member.getInt("store_id"));
				if(store != null){
					model.setStore(store.getStr("store_name"));
				}else{
					model.setStore("");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("member.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		int id = StringUtil.toInteger(getPara("id"));
		Member model = service.queryById(id);
		setAttr("model", model);
		//查询银行卡
		MemberBankcard bankCard = MemberBankcard.dao.queryByMemberId(id);
		if((bankCard != null)&&(StringUtil.isBlank(bankCard.getStr("card_img")))){
			bankCard.set("card_img", "#");
		}
		if(bankCard != null){
			String bankCd = bankCard.getStr("bank_name_cd");
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankCd);
			if(bank != null){
				bankCard.set("bank_name_cd", bank.getStr("name"));
			}
			setAttr("bankCard", bankCard);
		}
		
		Store store = Store.dao.queryById(model.getInt("store_id"));
		if(store != null){
			setAttr("store", store.getStr("store_name"));
		}else{
			setAttr("store", "");
		}
		Store openStore = Store.dao.queryMemberStore(id);
		if(openStore != null){
			setAttr("openStore", openStore.getStr("store_name"));
		}else{
			setAttr("openStore", "");
		}
		render("memberAlert.jsp");
	}
	
	public void see(){
		int id = StringUtil.toInteger(getPara("id"));
		Member model = service.queryById(id);
		setAttr("model", model);
		//查询银行卡
		MemberBankcard bankCard = MemberBankcard.dao.queryByMemberId(id);
		if((bankCard !=null)&&(StringUtil.isBlank(bankCard.getStr("card_img")))){
			bankCard.set("card_img", "#");
		}
		if(bankCard != null){
			String bankCd = bankCard.getStr("bank_name_cd");
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankCd);
			if(bank != null){
				bankCard.set("bank_name_cd", bank.getStr("name"));
			}
			setAttr("bankCard", bankCard);
		}
		
		Store store = Store.dao.queryById(model.getInt("store_id"));
		if(store != null){
			setAttr("store", store.getStr("store_name"));
		}else{
			setAttr("store", "");
		}
		Store openStore = Store.dao.queryMemberStore(id);
		if(openStore != null){
			setAttr("openStore", openStore.getStr("store_name"));
		}else{
			setAttr("openStore", "");
		}
		render("memberseeAlert.jsp");
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = StringUtil.toInteger(getPara("id"));
			if(id==0){
				setAttr("message", "用户还没有绑定门店");
			}else{
				int ret = service.updateStatus(id, getPara("status"));
				if(ret==0){
					setAttr("message", "修改成功");
				}else{
					setAttr("message", "修改失败");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	/**
	 * 更新用户
	 */
	public void updateMember(){
		int id = StringUtil.toInteger(getPara("id"));
		if(id==0){
			setAttr("message", "用户数据不存在");
		}else{
			String mobile = StringUtil.checkCode(getPara("mobile"));
			String name = StringUtil.checkCode(getPara("name"));
			String statusString = StringUtil.checkCode(getPara("status"));
			Member member = new Member();
			member.set("id", id);
			member.set("nick_name", name);
			member.set("status", statusString);
			member.set("name", StringUtil.checkCode(getPara("userName")));
			boolean ret = Member.dao.updateInfo(member);
			if(ret){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户"+mobile+"的信息");
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
			index();
		}
	}
	
	public void updateStatus(){
		int id = StringUtil.toInteger(getPara("id"));
		if(id==0){
			setAttr("message", "用户还未绑定银行卡，无法审核");
		}else{
			String status = StringUtil.checkCode(getPara("status"));
			MemberBankcard member = new MemberBankcard();
			member.set("id", id);
			member.set("status", status);
			member.set("update_time", DateUtil.getNowTimestamp());
			
			//消息
			String stStr = "";
			if(StringUtil.equals(status, "240002")){
				stStr = "您的银行卡已审核通过";
			}
			if(StringUtil.equals(status, "240003")){
				stStr = "您的银行卡审核未通过，请重新提交";
			}
		
			int userId = 0;
			MemberBankcard mbc = MemberBankcard.dao.queryById(id);
			if(mbc != null){
				userId = mbc.getInt("member_id") == null ? 0 : mbc.getInt("member_id");
			}
			
			/*Message message = new Message();
			message.set("message_type_cd", Constants.MESSAGE_TYPE.BANK_REVIEW_MSG);
			message.set("message",stStr);
			message.set("title","绑定银行卡审核");
			message.set("params", "{id:"+id+"}");
			message.set("create_time", DateUtil.getNowTimestamp());
			message.set("update_time", DateUtil.getNowTimestamp());
			message.set("user_id", userId);
			boolean messageSave = Message.dao.saveInfo(message);*/
			
			boolean ret = MemberBankcard.dao.updateInfo(member);
			if(ret){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户id:"+id+"的银行卡状态");
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
		}
		index();
	}
	
	public void exportData(){
		 String path = "D:\\upload\\用户数据.xls";
		 try {  
			
		 FileOutputStream os = new FileOutputStream(new File(path));  
		// 创建一个workbook 对应一个excel应用文件  
	        XSSFWorkbook workBook = new XSSFWorkbook();  
	        // 在workbook中添加一个sheet,对应Excel文件中的sheet  
	        XSSFSheet sheet = workBook.createSheet("用户数据");  
	       ExportUtil exportUtil = new ExportUtil(workBook, sheet);
	       XSSFCellStyle headStyle = exportUtil.getHeadStyle();  
	        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle(); 
	        
	        XSSFRow headRow = sheet.createRow(0);  
	        XSSFCell cell = null;  
	        String[] titles = new String[]{"用户名","用户编码	","昵称","注册号码","用户角色","经销商门店","余额","已提现金额","申请提现中金额","支付宝充值金额","银行卡审核状态","注册时间"};
	        for (int i = 0; i < titles.length; i++){  
	            cell = headRow.createCell(i);  
	            cell.setCellStyle(headStyle);  
	            cell.setCellValue(titles[i]);  
	        }  
	        
	        String mobile = getPara("cmobile");
			String name = getPara("cname");
			String storeName = getPara("storeName");
			String type = getPara("type");
			
			
		    List<Member> list = Member.dao.exportData(mobile, name, storeName, type);
		    ArrayList<MemberVO> models = new ArrayList<>();
		    if (list != null && list.size() > 0){  
		    	for (int j = 0; j < list.size(); j++){  
		    		XSSFRow bodyRow = sheet.createRow(j + 1);
		    		
		    		Member member = list.get(j);
		    		//用户名
		    		cell = bodyRow.createCell(0);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(member.getStr("name"));
		            
		            //用户编码
		            cell = bodyRow.createCell(1);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(member.getStr("id_code"));
		            
		            //昵称
		            cell = bodyRow.createCell(2);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(member.getStr("nick_name"));
		            
		            //注册号码
		            cell = bodyRow.createCell(3);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(member.getStr("mobile"));
		            
		            //用户角色
		            cell = bodyRow.createCell(4);  
		            cell.setCellStyle(bodyStyle);  
		            CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
					if(roleMst != null){
						cell.setCellValue(roleMst.getStr("name"));
					}else{
						cell.setCellValue("");
					}
		            
		            //绑定门店
		            cell = bodyRow.createCell(5);  
		            cell.setCellStyle(bodyStyle);  
		            Store store = Store.dao.queryById(member.getInt("store_id"));
					if(store != null){
						cell.setCellValue(store.getStr("store_name"));
					}else{
						cell.setCellValue("");
					}
		            
		            //余额
		            cell = bodyRow.createCell(6);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(member.getBigDecimal("moneys")));
		            
		            //已提现金额	
		            BigDecimal applying = BankCardRecord.dao.sumApplying(member.getInt("id"), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
					BigDecimal applySuccess = BankCardRecord.dao.sumApplying(member.getInt("id"), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
					BigDecimal paySuccess = PayRecord.dao.sumPay(member.getInt("id"), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
					
		            cell = bodyRow.createCell(7);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(applySuccess));
		            
		            //申请提现中金额	
		            cell = bodyRow.createCell(8);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(applying));
		            
		            //支付宝充值金额	
		            cell = bodyRow.createCell(9);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(paySuccess));
		            
		            //银行卡审核状态
		            cell = bodyRow.createCell(10);  
		            cell.setCellStyle(bodyStyle);  
		            MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
					if(memberBankcard == null){
						cell.setCellValue("暂未绑定银行卡");
					}else{
						String bankStatus = memberBankcard.getStr("status");
						CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
						if(sCodeMst != null){
							cell.setCellValue(sCodeMst.getStr("name"));
						}else{
							cell.setCellValue("");
						}
					}
		             
		            //注册时间
		            cell = bodyRow.createCell(11);  
		            cell.setCellStyle(bodyStyle);  
		            cell.setCellValue(StringUtil.toString(member.getTimestamp("create_time")));
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
