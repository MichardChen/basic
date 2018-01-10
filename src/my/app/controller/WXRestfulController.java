package my.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.app.service.WXRestService;
import my.core.constants.Constants;
import my.core.model.ReturnData;
import my.pvcloud.dto.LoginDTO;

@ControllerBind(key = "/wxrest", path = "/wxrest")
public class WXRestfulController extends Controller{

    WXRestService restService = Enhancer.enhance(WXRestService.class);
    LoginService service = Enhancer.enhance(LoginService.class);
    
    public void index(){
    	ReturnData data = new ReturnData();
    	data.setCode(Constants.STATUS_CODE.SUCCESS);
    	data.setMessage("查询成功");
    	Map<String, Object> map = new HashMap<>();
    	List<String> d = new ArrayList<String>();
    	d.add("1");
    	d.add("2");
    	d.add("3");
    	map.put("list", d);
    	data.setData(map);
		renderJson(data);
	}
    
    public void queryStoreDetail(){
    	LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(restService.queryTeaStoreDetail(dto));
	}
    
    public void queryTeaStoreList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(restService.queryTeaStoreList(dto));
	}
}
