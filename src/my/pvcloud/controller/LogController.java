package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.Admin;
import my.core.model.CodeMst;
import my.core.model.Document;
import my.core.model.FeedBack;
import my.core.model.Log;
import my.core.model.Member;
import my.core.vo.LogListVO;
import my.pvcloud.model.DocumentModel;
import my.pvcloud.model.FeedBackModel;
import my.pvcloud.service.FeedBackService;
import my.pvcloud.service.LogService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/logInfo", path = "/pvcloud")
public class LogController extends Controller {

	LogService service = Enhancer.enhance(LogService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		removeSessionAttr("operation");
		Page<Log> list = service.queryByPage(page, size);
		ArrayList<LogListVO> models = new ArrayList<>();
		LogListVO model = null;
		for(Log log : list.getList()){
			model = new LogListVO();
			model.setCreateTime(StringUtil.toString(log.getTimestamp("create_time")));
			model.setId(log.getInt("id"));
			model.setMark(log.getStr("mark"));
			if(StringUtil.equals(log.getStr("user_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				Admin admin = Admin.dao.queryAdmin(log.getInt("user_id"));
				if(admin != null){
					model.setUserName(admin.getStr("username"));
				}
			}else{
				Member member = Member.dao.queryById(log.getInt("user_id"));
				if(member != null){
					model.setUserName(member.getStr("name"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("log.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		String operation=getSessionAttr("operation");
		this.setSessionAttr("operation",operation);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Log> list = service.queryByPageParams(page, size,title,operation);
		ArrayList<LogListVO> models = new ArrayList<>();
		LogListVO model = null;
		for(Log log : list.getList()){
			model = new LogListVO();
			model.setCreateTime(StringUtil.toString(log.getTimestamp("create_time")));
			model.setId(log.getInt("id"));
			model.setMark(log.getStr("mark"));
			if(StringUtil.equals(log.getStr("user_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				Admin admin = Admin.dao.queryAdmin(log.getInt("user_id"));
				if(admin != null){
					model.setUserName(admin.getStr("username"));
				}
			}else{
				Member member = Member.dao.queryById(log.getInt("user_id"));
				if(member != null){
					model.setUserName(member.getStr("name"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("log.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		String operation = getSessionAttr("operation");
		String poperation = getPara("operation");
		operation = poperation;
		
		this.setSessionAttr("title",title);
		this.setSessionAttr("operation",operation);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Log> list = service.queryByPageParams(page, size,title,operation);
			ArrayList<LogListVO> models = new ArrayList<>();
			LogListVO model = null; 
			for(Log log : list.getList()){
				model = new LogListVO();
				model.setCreateTime(StringUtil.toString(log.getTimestamp("create_time")));
				model.setId(log.getInt("id"));
				model.setMark(log.getStr("mark"));
				if(StringUtil.equals(log.getStr("user_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
					Admin admin = Admin.dao.queryAdmin(log.getInt("user_id"));
					if(admin != null){
						model.setUserName(admin.getStr("username"));
					}
				}else{
					Member member = Member.dao.queryById(log.getInt("user_id"));
					if(member != null){
						model.setUserName(member.getStr("name"));
					}
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("log.jsp");
	}
}
