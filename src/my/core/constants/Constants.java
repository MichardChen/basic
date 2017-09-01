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
	
	public static interface MANAGER_TYPE{
		public static final String LEADER = "030001";
		public static final String MANAGER = "030002";
		public static final String ENPLOYEE = "030003";
	}
	
	public static interface PLATFORM{
		public static final String ANDROID = "020002";
		public static final String IOS = "020001";
	}
	
	public static interface ADDRESS_TYPE{
		public static final String FROM_ADDRESS = "480001";
		public static final String TO_ADDRESS = "480002";
	}
	
	public static interface SERVICE{
		public static final String QQ = "490001";
		public static final String TEL = "500001";
	}

	public static interface USER_STATUS{
		public static final String NORMAL = "510001";
		public static final String DELETE = "510002";
	}
	
	public static interface SUPPLIER_ATTRIBUTE{
		public static final String COMPANY = "220001";
		public static final String CART_TEAM = "220002";
		public static final String PERSONAL = "220003";
	}
	
	public static interface MANAGE_LEVEL{
		public static final String LEADER = "030001";
		public static final String MANAGER = "030002";
		public static final String STAFF = "030003";
	}
	
	public static interface IMAGE_TYPE{
		public static final String DEFAULT_USER_ICON = "520001";
	}
	
	public static interface GROSS_MARGIN_RATE{
		public static final String NORMAL_RATE = "530001";
	}
	
	public static interface SPEED_TYPE{
		public static final String CART_NOMAL_SPEED = "540001";
	}
	
	public static interface SHARE_TYPE{
		public static final String CLIENT_APP_SHARE = "550001";
		public static final String SUPPLIER_APP_SHARE = "550002";
		public static final String CLIENT_ORDER_SHARE = "550003";
		public static final String SUPPLIER_ORDER_SHARE = "550004";
	}
	
	public static interface PUSH_TYPE_CD{
		public static final String ORDER_MESSAGE = "560001";
		public static final String AUTH_MESSAGE = "560002";
		public static final String APP_UPDATE_MESSAGE = "560003";
		public static final String NOTIFICATION_MESSAGE = "560004";
		public static final String AUTH_NOT_PASS = "560005";
	}
	
	public static interface PUSH_MESSAGE_CODE{
		
		public static final String REPLY_ORDER_NOTIFICATION = "570001";
		public static final String ADD_ORDER_NOTIFICATION = "570002";
		public static final String DISTRICT_CART_NOTIFICATION = "570003";
		public static final String OUT_HOUSE_NOTIFICATION = "570004";
		public static final String LOAD_GOODS_NOTIFICATION = "570005";
		public static final String CHECK_NOTIFICATION = "570006";
		
		public static final String AUTO_DISTRICT_ORDER = "570007";
		public static final String MANAUL_DISTRICT_ORDER = "570008";
		public static final String UPLOAD_CHECK_ORDER = "570009";
		public static final String ORDER_COMPLETE = "570010";
		
		public static final String OUT_CART_NOTIFICATION = "570011";
		public static final String DRIVER_UPLOAD_CHECK_ORDER = "570012";
		
		public static final String CLIENT_CERTIFICATION_NOT_PASS = "570013";
		public static final String SUPPLIER_CERTIFICATION_NOT_PASS = "570014";
		public static final String CART_CERTIFICATION_NOT_PASS = "570015";
	}
	
	public static interface APP_UPDATE_URL_TYPE{
		
		public static final String CLIENT_ANDROID_URL = "580001";
		public static final String CLIENT_IOS_URL = "580002";
		public static final String SUPPLIER_ANDROID_URL = "580003";
		public static final String SUPPLIER_IOS_URL = "580004";
	}
	
	public static interface IOS_VERSIONUPDATE_HIDDEN_TYPE{
		
		public static final String IOS_CHECK_VERSION_HIDDEN = "590001";
	}
	
	public static interface CODEMST{
		public static final String UNIT = "300000";
		public static final String CART_LENGTH = "280000";
		public static final String CART_TYPE = "400000";
	}
	
	public static interface RANKING{
		public static final String CLIENT_RANK = "630001";
		public static final String SUPPLIER_RANK = "630002";
		public static final String CART_RANK = "630003";
	}
	
	public static interface CLIENT_POINTS_TYPE{
		public static final String REGISTER = "600001";
		public static final String PASS_CERTIFICATION = "600002";
		public static final String SIGN = "600003";
		public static final String QUERY_ORDER = "600004";
		public static final String CONFIRM_ORDER = "600005";
		public static final String ACCOMPLISH_ORDER = "600006";
		public static final String SHARE_ORDER = "600007";
		public static final String SHARE_APP = "600008";
		public static final String EXCHANGE_POINTS = "600009";
		
	}
	
	public static interface SUPPLIER_POINTS_TYPE{
		public static final String REGISTER = "610001";
		public static final String PASS_CERTIFICATION = "610002";
		public static final String SIGN = "610003";
		public static final String GET_ORDER = "610004";
		public static final String ADD_LINE = "610005";
		public static final String ACCOMPLISH_ORDER = "610006";
		public static final String SHARE_ORDER = "610007";
		public static final String SHARE_APP = "610008";
		public static final String ADD_CART_DRIVER = "610009";
		public static final String EXCHANGE_POINTS = "610010";
	}
	
	public static interface CART_POINTS_TYPE{
		public static final String SIGN = "620001";
		public static final String ACCOMPLISH_ORDER = "620002";
		public static final String SHARE_ORDER = "620003";
		public static final String SHARE_APP = "620004";
		public static final String EXCHANGE_POINTS = "620005";
	}
	
	public static interface MALLPRODUCT_STATUS{
		public static final String HANDLING = "660001";
		public static final String SENDING = "660002";
		public static final String SUCCESS = "660003";
		public static final String FAIL = "660004";
		public static final String STAY_HANDLE = "660005";
	}
	
	public static interface MALLPRODUCT_TYPE{
		public static final String VIRTUAL = "640001";
		public static final String PHYSICAL = "640002";
	}
	
	public static interface ADVERTISEMENT{
		public static final String CLIENT = "670001";
		public static final String SUPPLIER = "670002";
		public static final String CART = "670003";
	}
	
	
	/**
	 * 本地
	 */
	public static interface HOST{
		public static final String LOCALHOST = "http://192.168.1.131:82/newsimages/";
		public static final String TEA = "http://192.168.1.131:82/tea/";
		public static final String DOCUMENT = "http://192.168.1.131:82/document/";
		public static final String FILE = "http://192.168.1.131:82/file/";
		public static final String ICON = "http://192.168.1.131:82/icon/";
		public static final String IMG = "http://192.168.1.131:82/img/";
	}
	
	public static interface FILE_HOST{
		public static final String LOCALHOST = "F:\\upload\\newsimages\\";
		public static final String TEA = "F:\\upload\\tea\\";
		public static final String DOCUMENT = "F:\\upload\\document\\";
		public static final String FILE = "F:\\upload\\file\\";
		public static final String ICON = "F:\\upload\\icon\\";
		public static final String IMG = "F:\\upload\\img\\";
	}

	
	/**服务器
	public static interface HOST{
		public static final String LOCALHOST = "http://139.196.171.205:88/newsimages/";
		public static final String TEA = "http://139.196.171.205:88/tea/";
		public static final String DOCUMENT = "http://139.196.171.205:88/document/";
		public static final String FILE = "http://139.196.171.205:88/file/";
		public static final String ICON = "http://139.196.171.205:88/icon/";
	}
	
	public static interface FILE_HOST{
		public static final String LOCALHOST = "D:\\upload\\newsimages\\";
		public static final String TEA = "D:\\upload\\tea\\";
		public static final String DOCUMENT = "D:\\upload\\document\\";
		public static final String FILE = "D:\\upload\\file\\";
		public static final String ICON = "D:\\upload\\icon\\";
	}*/
	
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
	}
	
	public static interface VERSION_TYPE{
		public static final String ANDROID = "070001";
		public static final String IOS = "070002";
	}
	
	public static interface MESSAGE_TYPE{
		public static final String BUY_TEA = "080001";
	}
}
