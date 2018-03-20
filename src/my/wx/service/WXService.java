package my.wx.service;

import my.core.tx.TxProxy;

public class WXService {

	public static final WXService service = TxProxy.newProxy(WXService.class);
	
}
