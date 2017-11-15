package my.pvcloud.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.City;
import my.core.model.CodeMst;
import my.core.model.District;
import my.core.model.GetTeaRecord;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.WarehouseTeaMember;
import my.pvcloud.model.BankRecordModel;
import my.pvcloud.model.GetTeaRecordListModel;
import my.pvcloud.model.StoreModel;
import my.pvcloud.service.GetTeaRecordService;
import my.pvcloud.service.WithDrawService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/getTeaRecordInfo", path = "/pvcloud")
public class GetTeaRecordController extends Controller {

	GetTeaRecordService service = Enhancer.enhance(GetTeaRecordService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("time1");
		removeSessionAttr("time2");
		removeSessionAttr("mobile");
		removeSessionAttr("status");
		Page<GetTeaRecord> list = service.queryByPage(page, size);
		ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
		GetTeaRecordListModel model = null;
		for(GetTeaRecord record : list.getList()){
			model = new GetTeaRecordListModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			String express = "";
			String expressNo = "";
			if(StringUtil.isNoneBlank(record.getStr("express_company"))){
				express =  record.getStr("express_company");
			}
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}
			if(StringUtil.isNoneBlank(record.getStr("express_no"))){
				expressNo =  record.getStr("express_no");
				model.setExpress("快递公司："+express+"，单号："+expressNo);
			}
			
			model.setMark(record.getStr("mark") == null ? "" : record.getStr("mark"));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setMobile(member.getStr("mobile"));
				model.setUserName(member.getStr("name"));
			}
			CodeMst sizeType = CodeMst.dao.queryCodestByCode(record.getStr("size_type_cd"));
			String size = "";
			if(sizeType != null){
				size = sizeType.getStr("name");
			}
			model.setQuality(StringUtil.toString(record.getInt("quality"))+size);
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTea(tea.getStr("tea_title"));
			}
			int addressId = record.getInt("address_id") == null ? 0 :record.getInt("address_id");
			ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(addressId);
			if(address != null){
				String detail = "";
				Province province = Province.dao.queryProvince(address.getInt("province_id"));
				if(province != null){
					detail = detail + province.getStr("name");
				}
				City city = City.dao.queryCity(address.getInt("city_id"));
				if(city != null){
					detail = detail + city.getStr("name");
				}
				District district = District.dao.queryDistrict(address.getInt("district_id"));
				if(district != null){
					detail = detail + district.getStr("name");
				}
				String receiveMan = address.getStr("receiveman_name") == null ? "":address.getStr("receiveman_name");
				String m = address.getStr("mobile") == null ? "":address.getStr("mobile");
				model.setAddress(detail+address.getStr("address"));
				model.setLinkMan(receiveMan);
				model.setLinkTel(m);
				//当前库存
				BigDecimal currentStock = WarehouseTeaMember.dao.queryTeaStock(record.getInt("member_id")
																	 		  ,record.getInt("tea_id")
																	 		  ,Constants.USER_TYPE.USER_TYPE_CLIENT);
				model.setCurrentStock(StringUtil.toString(currentStock)+"片");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("getteareocord.jsp");
	}
	
	/**
	 * 分页
	 */
	public void queryByPage(){
		String time1 = getSessionAttr("time1");
		String time2 = getSessionAttr("time2");
		String mobile = getSessionAttr("mobile");
		String status = getSessionAttr("status");
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<GetTeaRecord> list = service.queryByPageParams(page, size,time1,time2,mobile,status);
        ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
		GetTeaRecordListModel model = null;
		for(GetTeaRecord record : list.getList()){
			model = new GetTeaRecordListModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			String express = "";
			String expressNo = "";
			if(StringUtil.isNoneBlank(record.getStr("express_company"))){
				express =  record.getStr("express_company");
			}
			if(StringUtil.isNoneBlank(record.getStr("express_no"))){
				expressNo =  record.getStr("express_no");
				model.setExpress("快递公司："+express+"，单号："+expressNo);
			}
			CodeMst s = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(s != null){
				model.setStatus(s.getStr("name"));
			}
			
			model.setMark(record.getStr("mark") == null ? "" : record.getStr("mark"));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setMobile(member.getStr("mobile"));
				model.setUserName(member.getStr("name"));
			}
			model.setQuality(StringUtil.toString(record.getInt("quality")));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			if(tea != null){
				model.setTea(tea.getStr("tea_title"));
			}
			int addressId = record.getInt("address_id") == null ? 0 :record.getInt("address_id");
			ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(addressId);
			if(address != null){
				String detail = "";
				Province province = Province.dao.queryProvince(address.getInt("province_id"));
				if(province != null){
					detail = detail + province.getStr("name");
				}
				City city = City.dao.queryCity(address.getInt("city_id"));
				if(city != null){
					detail = detail + city.getStr("name");
				}
				District district = District.dao.queryDistrict(address.getInt("district_id"));
				if(district != null){
					detail = detail + district.getStr("name");
				}
				String receiveMan = address.getStr("receiveman_name") == null ? "":address.getStr("receiveman_name");
				String m = address.getStr("mobile") == null ? "":address.getStr("mobile");
				model.setAddress(detail+address.getStr("address"));
				model.setLinkMan(receiveMan);
				model.setLinkTel(m);
				//当前库存
				BigDecimal currentStock = WarehouseTeaMember.dao.queryTeaStock(record.getInt("member_id")
																	 		  ,record.getInt("tea_id")
																	 		  ,Constants.USER_TYPE.USER_TYPE_CLIENT);
				model.setCurrentStock(StringUtil.toString(currentStock)+"片");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("getteareocord.jsp");
	}
	
	/**
	 * 模糊查询搜索框
	 */
	public void queryByConditionByPage(){
		
		String time1 = getPara("time1");
		this.setSessionAttr("time1",time1);
		String time2 = getPara("time2");
		this.setSessionAttr("time2",time2);
		String mobile = getPara("mobile");
		this.setSessionAttr("mobile",mobile);
		String status = getPara("status");
		this.setSessionAttr("status",status);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<GetTeaRecord> list = service.queryByPageParams(page, size,time1,time2,mobile,status);
	        ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
			GetTeaRecordListModel model = null;
			for(GetTeaRecord record : list.getList()){
				model = new GetTeaRecordListModel();
				model.setId(record.getInt("id"));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				Member member = Member.dao.queryById(record.getInt("member_id"));
				String express = "";
				String expressNo = "";
				if(StringUtil.isNoneBlank(record.getStr("express_company"))){
					express =  record.getStr("express_company");
				}
				
				if(StringUtil.isNoneBlank(record.getStr("express_no"))){
					expressNo =  record.getStr("express_no");
					model.setExpress("快递公司："+express+"，单号："+expressNo);
				}
				CodeMst s = CodeMst.dao.queryCodestByCode(record.getStr("status"));
				if(s != null){
					model.setStatus(s.getStr("name"));
				}
				
				model.setMark(record.getStr("mark") == null ? "" : record.getStr("mark"));
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setUserName(member.getStr("name"));
				}
				model.setQuality(StringUtil.toString(record.getInt("quality")));
				Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
				if(tea != null){
					model.setTea(tea.getStr("tea_title"));
				}
				int addressId = record.getInt("address_id") == null ? 0 :record.getInt("address_id");
				ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(addressId);
				if(address != null){
					String detail = "";
					Province province = Province.dao.queryProvince(address.getInt("province_id"));
					if(province != null){
						detail = detail + province.getStr("name");
					}
					City city = City.dao.queryCity(address.getInt("city_id"));
					if(city != null){
						detail = detail + city.getStr("name");
					}
					District district = District.dao.queryDistrict(address.getInt("district_id"));
					if(district != null){
						detail = detail + district.getStr("name");
					}
					String receiveMan = address.getStr("receiveman_name") == null ? "":address.getStr("receiveman_name");
					String m = address.getStr("mobile") == null ? "":address.getStr("mobile");
					model.setAddress(detail+address.getStr("address"));
					model.setLinkMan(receiveMan);
					model.setLinkTel(m);
					//当前库存
					BigDecimal currentStock = WarehouseTeaMember.dao.queryTeaStock(record.getInt("member_id")
																		 		  ,record.getInt("tea_id")
																		 		  ,Constants.USER_TYPE.USER_TYPE_CLIENT);
					model.setCurrentStock(StringUtil.toString(currentStock)+"片");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("getteareocord.jsp");
	}
	
	
	/**
	 * 更新
	 */
	public void updateRecord(){
		try{
			int recordId = getParaToInt("id");
			String expressName = StringUtil.checkCode(getPara("expressName"));
			String expressNo = StringUtil.checkCode(getPara("expressNo"));
			String status = StringUtil.checkCode(getPara("status"));
			String mark = StringUtil.checkCode(getPara("mark"));
			int ret = service.updateRecord(recordId, expressName, expressNo, mark, status);
			if(ret!=0){
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	public void editInt(){
		int id = StringUtil.toInteger(getPara("id"));
		GetTeaRecord record = GetTeaRecord.dao.queryById(id);
		if(record != null){
			Member member = Member.dao.queryById(record.getInt("member_id"));
			setAttr("member", "注册电话："+member.getStr("mobile")+",用户名："+member.getStr("name"));
			Tea tea = Tea.dao.queryById(record.getInt("tea_id"));
			setAttr("tea", tea);
		}
		setAttr("data", record);
		render("editExpress.jsp");
	}
}
