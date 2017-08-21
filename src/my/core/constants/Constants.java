package my.core.constants;

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
		public static final String USER_TYPE_SUPPLIER = "010002";
		public static final String USER_TYPE_CART = "010003";
		public static final String USER_TYPE = "010000";
	}
	
	/** 消息*/
	public static interface MESSAGE_CONTENT{
		public static final String VERTIFY_CODE_MSG = "尊敬的用户，您的验证码是：0914，10分钟内有效。";
	}
	
	/** 数据字段*/
	public static interface REQUEST_DTO_MODEL{
		public static final String USER_ID= "userId";
		public static final String USER_TYPE_CD = "userTypeCd";
		public static final String ACCESS_TOKEN = "accessToken";
		public static final String PLATFORM = "platForm";
		public static final String MOBILE = "mobile";
		public static final String PAGESIZE = "pageSize";
		public static final String PAGENUM = "pageNum";
		public static final String STATUS = "status";
		public static final String TIME_TYPE = "timeType";
		public static final String DEPARTURE = "departure";
		public static final String DESTINATION = "destination";
		public static final String DEPARTURE_DETAIL = "departureDetail";
		public static final String DESTINATION_DETAIL = "destinationDetail";
		public static final String GOODSNAME = "goodsName";
		public static final String WEIGHT = "weight";
		public static final String VOLUME = "volume";
		public static final String CART_TYPE = "cartType";
		public static final String CART_LENGTH ="cartLength";
		public static final String COMMENT = "comment";
		public static final String ORDER_ID = "orderId";
		public static final String FROM_PROVINCE_ID = "fromProvinceId";
		public static final String FROM_CITY_ID = "fromCityId";
		public static final String FROM_DISTRICT_ID = "fromDistrictId";
		public static final String FROM_ADDRESS = "fromAddress";
		public static final String TO_PROVINCE_ID = "toProvinceId";
		public static final String TO_CITY_ID = "toCityId";
		public static final String TO_DISTRICT_ID = "toDistrictId";
		public static final String TO_ADDRESS = "toAddress";
		public static final String GOODS_UNIT = "unit";
		public static final String DISTANCE = "distance";
		public static final String GOODS_NUM = "goodsNum";
		public static final String ORDER_NO = "orderNo";
		public static final String REASON = "reason";
		public static final String REASON_MARK = "reasonMark";
		public static final String EXPECT_PRICE = "expectPrice";
		public static final String LINE_ID = "lineId";
		public static final String FEEDBACK = "feedBack";
		public static final String VERSION = "version";
		public static final String MESSAGE_TYPE = "messageType";
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
	
	/**客户状态*/
	public static interface CLIENT_STATUS_CODE{
		public static final String NOT_REVIEWED = "070001";
		public static final String REVIEWING = "070002";
		public static final String REVIEWED = "070003";
		public static final String NOT_PASS = "070004";
	}
	
	/**供应商状态*/
	public static interface SUPPLIERS_STATUS_CODE{
		public static final String NOT_REVIEWED = "230001";
		public static final String REVIEWING = "230002";
		public static final String REVIEWED = "230003";
		public static final String NOT_PASS = "230004";
	}
	
	/**车辆状态*/
	public static interface CART_STATUS_CODE{
		public static final String NOT_REVIEWED = "410001";
		public static final String REVIEWING = "410002";
		public static final String REVIEWED = "410003";
		public static final String NOT_PASS = "410004";
	}
	
	/**订单查询时间类型*/
	public static interface ORDER_QUERY_TIMETYPE{
		public static final String TIME_TYPE_DAY = "420001";
		public static final String TIME_TYPE_WEEK = "420002";
		public static final String TIME_TYPE_ALL = "420003";
	}
	
	/**订单完成情况*/
	public static interface ORDER_ACCOMPLISH_TYPE{
		public static final String ACCOMPLISHED = "430001";
		public static final String NOT_ACCOMPLISHED = "430002";
	}
	
	/**客户订单状态*/
	public static interface CLIENT_ORDER_STATUS{
		public static final String QUERY_PRICE = "160101";
		public static final String REPLYED = "160201";
		public static final String CANCLE = "160205";
		public static final String GENERATE_ORDER = "160301";
		public static final String DISTRIBUTE_CART = "160401";
		public static final String OUT_STOREHOUSE = "160501";
		public static final String LOAD = "160601";
		public static final String CHECK_ORDER = "160701";
		public static final String ACCOMPLISH = "160801";
	}
	
	/**供应商订单状态*/
	public static interface SUPPLY_ORDER_STATUS{
		public static final String NEW_ORDER = "170201";
		public static final String OUT_CART = "170301";
		public static final String OUT_STOREHOUSE = "170401";
		public static final String LOADED = "170501";
		public static final String UPLOAD_CHEKC_ORDER = "170601";
		public static final String ACCOMPLISH = "170701";
		public static final String CANCLE = "170205";
		public static final String INVALID = "170101";
	}
	
	public static interface ORDER_STATUS{
		public static final String NEW_ORDER = "150101";
		public static final String REPLY_PRICE = "150201";
		public static final String CANCLE = "150205";
		public static final String GENERATE_ORDER = "150301";
		public static final String OUT_CART = "150401";
		public static final String OUT_STOREHOUSE = "150501";
		public static final String LOAD = "150601";
		public static final String CHECK_ORDER = "150701";
		public static final String ACCOMPLISH = "150801";
	}
	
	/**日期格式*/
	public static interface DATE_FORMAT{
		public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd HH:mm";
		public static final String DATE_FORMATE_YYYYMMDD = "yyyy-MM-dd";
	}
	
	/**图片上传*/
	public static interface UPLOAD_IMAGE_URL{
		public static final String CLIENT_AUTH_UPLOAD_IMG = "CLIENT_AUTH_UPLOAD_IMG";
		public static final String CLIENT_AUTH_UPLOAD_URL = "CLIENT_AUTH_UPLOAD_URL";
		public static final String SUPPPLIER_AUTH_UPLOAD_URL = "SUPPLIER_AUTH_UPLOAD_URL";
		public static final String SUPPLIER_AUTH_UPLOAD_IMG = "SUPPLIER_AUTH_UPLOAD_IMG";
		public static final String CHECK_ORDER_URL = "CHECK_ORDER_URL";
		public static final String CHECK_ORDER_IMG = "CHECK_ORDER_IMG";
		public static final String CART_AUTH_UPLOAD_URL = "CART_AUTH_UPLOAD_URL";
		public static final String CART_AUTH_UPLOAD_IMG = "CART_AUTH_UPLOAD_IMG";
		//
		public static final String SAVE_PRODUCT_URL = "SAVE_PRODUCT_URL";
		public static final String SAVE_PRODUCT_IMG = "SAVE_PRODUCT_IMG";
		
		public static final String SAVE_MESSAGE_URL = "SAVE_MESSAGE_URL";
		public static final String SAVE_MESSAGE_IMG = "SAVE_MESSAGE_IMG";
		
	}
	
	public static interface ORDER_READ{
		public static final String NOT_READED = "未读";
		public static final String READED = "已读";
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
	
	public static interface ORDER_ACCOMPLISH_STATUS{
		public static final String STAY_ACCOMPLISH = "430001";
		public static final String ACCOMPLISH = "430002";
	}
	
	public static interface MESSAGE_TYPE{
		public static final String ORDER_MESSAGE = "450001";
		public static final String SYSTEM_MESSAGE = "450002";
		public static final String ACTIVITY_MESSAGE = "450003";
		public static final String PLATFORM_MESSAGE = "450004";
		public static final String WL_MESSAGE = "450005";
		public static final String TRADE_MESSAGE = "450006";
	}
	
	public static interface SYSTEM_VERSION_CONTROL{
		public static final String LOCATION_DATA_VERSION = "460001";
		public static final String CLIENT_APP_VERSION_ANDROID = "460002";
		public static final String SUPPLY_APP_VERSION_ANDROID = "460003";
		public static final String CLIENT_APP_VERSION_IOS = "460004";
		public static final String SUPPLY_APP_VERSION_IOS = "460005";
	}
	
	public static interface LOCATION_TYPE{
		public static final String PROVINCE = "province";
		public static final String CITY = "city";
		public static final String DISTRICT = "district";
	}
	
	public static interface DEPARMENT_TYPE{
		public static final String COMPANY = "040001";
		public static final String BUSINESS = "040002";
		public static final String CLIENT = "040003";
		public static final String SUPPLIER = "040004";
		public static final String ONLINE_BUSINESS = "040005";
	}
	
	public static interface DISTANCE_TYPE{
		public static final String LESS_THREEHUNDRED = "470001";
		public static final String THREE_SEVEN_HUNDRED = "470002";
		public static final String SEVEN_TWELVE_HUNDRED = "470003";
		public static final String MORE_TWELVEHUNDRED = "470004";
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
	
	public static interface PUSH_TYPE_DETAIL{
		
		public static final String ANDROID_CLIENT_PUSH_APPKEY = "ANDROID_CLIENT_PUSH_APPKEY";
		public static final String ANDROID_CLIENT_MASTER_SECRET = "ANDROID_CLIENT_MASTER_SECRET";
		public static final String ANDROID_SUPPLIER_PUSH_APPKEY = "ANDROID_SUPPLIER_PUSH_APPKEY";
		public static final String ANDROID_SUPPLIER_MASTER_SECRET = "ANDROID_SUPPLIER_MASTER_SECRET";
		public static final String IOS_CLIENT_PUSH_APPKEY = "IOS_CLIENT_PUSH_APPKEY";
		public static final String IOS_CLIENT_MASTER_SECRET = "IOS_CLIENT_MASTER_SECRET";
		public static final String IOS_SUPPLIER_PUSH_APPKEY = "IOS_SUPPLIER_PUSH_APPKEY";
		public static final String IOS_SUPPLIER_MASTER_SECRET = "IOS_SUPPLIER_MASTER_SECRET";
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
}
