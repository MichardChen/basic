package my.app.service;

import my.core.tx.TxProxy;

public class RestService {

	public static final RestService service = TxProxy.newProxy(RestService.class);
	
}
