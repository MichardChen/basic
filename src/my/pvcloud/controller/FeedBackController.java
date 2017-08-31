package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.FeedBack;
import my.core.model.Member;
import my.pvcloud.model.FeedBackModel;
import my.pvcloud.service.FeedBackService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/feedbackInfo", path = "/pvcloud")
public class FeedBackController extends Controller {

	FeedBackService service = Enhancer.enhance(FeedBackService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
		Page<FeedBack> list = service.queryByPage(page, size);
		ArrayList<FeedBackModel> models = new ArrayList<>();
		FeedBackModel model = null;
		for(FeedBack feedBack : list.getList()){
			model = new FeedBackModel();
			model.setContent(feedBack.getStr("feedback"));
			model.setId(feedBack.getInt("id"));
			Integer userId = feedBack.getInt("user_id");
			if(userId != null){
				Member member = Member.dao.queryMemberById(userId);
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setName(member.getStr("name"));
				}
			}
			model.setFlg(feedBack.getInt("readed"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("feedback.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		/*try {
			
			String custInfo=getSessionAttr("custInfo");
			String custValue=getSessionAttr("custValue");
				
			Page<News> custInfoList = new Page<News>(null, 0, 0, 0, 0);	
				
			this.setSessionAttr("custInfo",custInfo);
			this.setSessionAttr("custValue", custValue);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0){
	            page = 1;
	        }
			if(custInfo!=null){
				if(("addrName").equals(custInfo)){
					custInfoList = service.queryByPage(page, size);
				}else if(("phoneNum").equals(custInfo)){
					custInfoList = service.queryByPage(page, size);
				}else{
					custInfoList = service.queryByPage(page, size);
				}
			}else{
				custInfoList = service.queryByPage(page, size);
			}
			setAttr("custInfoList", custInfoList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("custInfo.jsp");*/
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		FeedBack model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
		render("feedbackAlter.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			int flg = StringUtil.toInteger(getPara("flg"));
			int ret = service.updateFlg(id, flg);
			if(ret!=0){
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
