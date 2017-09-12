package my.core.constants;

import org.eclipse.jdt.internal.compiler.classfmt.FieldInfoWithAnnotation;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import sun.net.www.content.text.plain;

/**
 * 系统常量
 * @author Chen Dang
 * @date 2016年3月29日 上午8:45:58 
 * @version 1.0
 * @Description:
 */
public interface Constants {

	public final static String SYSTEM_NAME = "56物流平台";
	
	/**
	 * 异常信息统一头信息<br>
	 * 非常遗憾的通知您,程序发生了异常
	 */
	public static final String Exception_Head = "OH,MY GOD! SOME ERRORS OCCURED! AS FOLLOWS :";
	/** 客户端语言 */
	public static final String USERLANGUAGE = "userLanguage";
	/** 客户端主题 */
	public static final String WEBTHEME = "webTheme";
	/** 当前用户 */
	public static final String CURRENT_USER = "CURRENT_USER";
	/** 在线用户数量 */
	public static final String ALLUSER_NUMBER = "ALLUSER_NUMBER";
	/** 登录用户数量 */
	public static final String USER_NUMBER = "USER_NUMBER";
	/** 上次请求地址 */
	public static final String PREREQUEST = "PREREQUEST";
	/** 上次请求时间 */
	public static final String PREREQUEST_TIME = "PREREQUEST_TIME";
	/** 非法请求次数 */
	public static final String MALICIOUS_REQUEST_TIMES = "MALICIOUS_REQUEST_TIMES";
	/** 缓存命名空间 */
	public static final String CACHE_NAMESPACE = "iBase4J:";
	
	/** 用户类型*/
	public static interface USER_TYPE{
		public static final String USER_TYPE_CLIENT = "010001";
		public static final String PLATFORM_USER = "010002";
	}
	
	/** 消息*/
	public static interface MESSAGE_CONTENT{
		public static final String VERTIFY_CODE_MSG = "尊敬的用户，您的验证码是：0914，10分钟内有效。";
	}
	
	/**状态码*/
	public static interface STATUS_CODE{
		public static final String LOGIN_SUCCESS = "5606";
		public static final String LOGIN_FAIL = "5607";
		public static final String LOGIN_FAIL_ERROR_PWD = "560701";
		public static final String LOGIN_FAIL_USER_NOT_EXIST= "560702";
		public static final String USER_NOT_LOGIN = "560703";
		public static final String USER_LOGIN_ANOTHER_DEVICE = "560704";
		public static final String LOGIN_EXPIRE = "560705";
		public static final String SUCCESS = "5600";
		public static final String FAIL = "5700";
		public static final String LOGIN_ANOTHER_PLACE = "5701";
		public static final String IMAGE_UPLOAD_SUCCESS = "5610";
		public static final String IMAGE_UPLOAD_FAIL = "5611";
	}
	
	
	
	/**日期格式*/
	public static interface DATE_FORMAT{
		public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd HH:mm";
		public static final String DATE_FORMATE_YYYYMMDD = "yyyy-MM-dd";
	}
	
	public static interface CHARACTER{
		public static final String STRENGTH_CHAR = "-";
	}
	
	public static interface STRING_ONE_ZERO{
		public static final String ONE = "1";
		public static final String ZERO = "0";
	}
	
	public static interface NUMBER{
		public static final int ONE = 1;
		public static final int ZERO = 0;
	}
	
	
	
	public static interface LOCATION_TYPE{
		public static final String PROVINCE = "province";
		public static final String CITY = "city";
		public static final String DISTRICT = "district";
	}
	
	public static interface PLATFORM{
		public static final String ANDROID = "020002";
		public static final String IOS = "020001";
	}
	
	
	/**
	 * 本地
	 
	public static interface HOST{
		public static final String LOCALHOST = "http://192.168.1.131:82/newsimages/";
		public static final String TEA = "http://192.168.1.131:82/tea/";
		public static final String DOCUMENT = "http://192.168.1.131:82/document/";
		public static final String FILE = "http://192.168.1.131:82/file/";
		public static final String ICON = "http://192.168.1.131:82/icon/";
		public static final String IMG = "http://192.168.1.131:82/img/";
		public static final String STORE = "http://192.168.1.131:82/store/";
	}
	
	public static interface FILE_HOST{
		public static final String LOCALHOST = "F:\\upload\\newsimages\\";
		public static final String TEA = "F:\\upload\\tea\\";
		public static final String DOCUMENT = "F:\\upload\\document\\";
		public static final String FILE = "F:\\upload\\file\\";
		public static final String ICON = "F:\\upload\\icon\\";
		public static final String IMG = "F:\\upload\\img\\";
		public static final String STORE = "F:\\upload\\store\\";
	}*/

	
	/**服务器*/
	public static interface HOST{
		public static final String LOCALHOST = "http://139.196.171.205:88/newsimages/";
		public static final String TEA = "http://139.196.171.205:88/tea/";
		public static final String DOCUMENT = "http://139.196.171.205:88/document/";
		public static final String FILE = "http://139.196.171.205:88/file/";
		public static final String ICON = "http://139.196.171.205:88/icon/";
		public static final String IMG = "http://139.196.171.205:88/img/";
		public static final String STORE = "http://139.196.171.205:88/store/";
	}
	
	public static interface FILE_HOST{
		public static final String LOCALHOST = "D:\\upload\\newsimages\\";
		public static final String TEA = "D:\\upload\\tea\\";
		public static final String DOCUMENT = "D:\\upload\\document\\";
		public static final String FILE = "D:\\upload\\file\\";
		public static final String ICON = "D:\\upload\\icon\\";
		public static final String IMG = "D:\\upload\\img\\";
		public static final String STORE = "D:\\upload\\store\\";
	}
	
	public static interface MEMBER_STATUS{
		public static final String CERTIFICATED = "040001";
		public static final String NOT_CERTIFICATED = "040002";
	}
	
	public static interface COMMON_STATUS{
		public static final String DELETE = "050001";
		public static final String NORMAL = "050002";
	}
	
	public static interface DOCUMENT_TYPE{
		public static final String SALE_COMMENT = "060001";
		public static final String USE_HELP = "060002";
		public static final String CONTRACT_COMMENT = "060003";
		public static final String NEW_TEA_SALE_MARK = "060004";
		public static final String CERTIFICATE_TIP = "060005";
	}
	
	public static interface VERSION_TYPE{
		public static final String ANDROID = "070001";
		public static final String IOS = "070002";
	}
	
	public static interface MESSAGE_TYPE{
		public static final String BUY_TEA = "080001";
	}
	
	public static interface NEWTEA_STATUS{
		public static final String STAY_SALE = "090001";
		public static final String ON_SALE = "090002";
		public static final String END = "090003";
	}
	
	public static interface PHONE{
		public static final String CUSTOM = "100001";
	}
	
	public static interface STORE_STATUS{
		public static final String NOT_CERTIFICATE = "110001";
		public static final String STAY_CERTIFICATE = "110002";
		public static final String CERTIFICATE_SUCCESS = "110003";
		public static final String CERTIFICATE_FAIL = "110004";
	}
	
	public static interface LOG_TYPE_CD{
		public static final String BUY_TEA = "120001";
		public static final String SALE_TEA = "120002";
		public static final String WAREHOUSE_FEE = "120003";
		public static final String GET_TEA = "120004";
		public static final String RECHARGE = "120005";
		public static final String WITHDRAW = "120006";
		public static final String REFUND = "120007";
	}
	
	public static interface BANK_MANU_TYPE_CD{
		public static final String RECHARGE = "130001";
		public static final String WITHDRAW = "130002";
		public static final String REFUND = "130003";
	}
	
	public static interface ORDER_STATUS{
		public static final String INVALID = "140001";
		public static final String SHOPPING_CART = "140002";
		public static final String PAY_SUCCESS = "140003";
		public static final String PAY_FAIL = "140004";
		public static final String DELETE = "140005";
	}
	
	public static interface TEA_UNIT{
		public static final String PIECE = "150001";
		public static final String ITEM = "150002";
	}
	
	public static interface TEA_STATUS{
		public static final String ON_SALE = "160001";
		public static final String STOP_SALE = "160002";
	}
}
