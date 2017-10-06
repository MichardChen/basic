package my.core.service;

import java.sql.Date;

import com.alipay.api.domain.Data;

import my.app.service.LoginService;
import my.core.constants.Constants;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.tx.TxProxy;
import my.pvcloud.dto.LoginDTO;

public class MemberService {

public static final LoginService service = TxProxy.newProxy(LoginService.class);
	
	//充值
	public ReturnData updatePayInfo(LoginDTO dto){
		ReturnData data = new ReturnData();
		Member member = Member.dao.queryMember(dto.getMobile());
		int ret = Member.dao.updateMoneys(member.getInt("id")
										 ,member.getBigDecimal("moneys").add(dto.getMoney()));
		if(ret != 0){
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			data.setMessage("充值成功");
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("充值失败");
		}
		return data;
	}
}
