package my.pvcloud.controller;

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
		Page<GetTeaRecord> list = service.queryByPage(page, size);
		ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
		GetTeaRecordListModel model = null;
		for(GetTeaRecord record : list.getList()){
			model = new GetTeaRecordListModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
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
			ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
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
				model.setAddress(detail+address.getStr("address"));
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
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<GetTeaRecord> list = service.queryByPageParams(page, size,time1,time2);
        ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
		GetTeaRecordListModel model = null;
		for(GetTeaRecord record : list.getList()){
			model = new GetTeaRecordListModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
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
			ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
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
				model.setAddress(detail+address.getStr("address"));
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
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<GetTeaRecord> list = service.queryByPageParams(page, size,time1,time2);
	        ArrayList<GetTeaRecordListModel> models = new ArrayList<>();
			GetTeaRecordListModel model = null;
			for(GetTeaRecord record : list.getList()){
				model = new GetTeaRecordListModel();
				model.setId(record.getInt("id"));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
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
				ReceiveAddress address = ReceiveAddress.dao.queryByKeyId(record.getInt("address_id"));
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
					model.setAddress(detail+address.getStr("address"));
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("getteareocord.jsp");
	}
	
	
	/**
	 * 更新
	 *//*
	public void update(){
		try{
			int id = getParaToInt("id");
			String flg = getPara("status");
			int ret = service.updateFlg(id, flg);
			if(ret!=0){
				CodeMst status = CodeMst.dao.queryCodestByCode(flg);
				if(status != null){
					Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "处理提现申请id:"+id+","+status.getStr("name"));
				}
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}*/
}
