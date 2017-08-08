package my.pvcloud.controller;

import org.huadalink.route.ControllerBind;

import com.jfinal.core.Controller;

@ControllerBind(key = "/overview", path = "/pvcloud")
public class OverviewController extends Controller {
	/**
	 * 概览
	 */
	public void index(){
		render("welcome.jsp");
	}
}
