package my.core.constants;

public interface Constants {

	/**成功失败状态码*/
	public static interface STATUS_CODE{
		public static final String SUCCESS = "100";
		public static final String FAIL = "101";
	}
	
/*	public static interface LOGIN_STATUS{
		public static final String LOING_SUCCESS = "5600";
		public static final String USER_NOT_EXIST = "5701";
		public static final String USER_PWD_ERROR = "5702";
		public static final String LOGIN_FAIL = "5700";
	}*/
	
	public static interface ADMIN_LEVEL{
		public static final String ADMIN = "010001";
		public static final String SHOPKEEPER = "010002";
		public static final String BARBER = "010003";
		public static final String ASSISTANT = "010004";
		public static final String HAIRDRESS = "010005";
		public static final String PHYSICALER = "010006";
	}
	
	/**用户类型*/
	public static interface USER_TYPE_CD {
		public static final String CLIENT = "201";
		public static final String EMPLOYEE = "301";
		public static final String BARBER = "202";
		public static final String ASSITANTS = "203";
		public static final String SHOPKEEPER = "204";
		public static final String ADMIN = "205";
	}
	
	/**用户等级*/
	public static interface USER_GRADE_CD{
		public static final String GENERNAL_USER = "301";
	}
	
	/**预约时间段*/
	public static interface APPOINT_TIME_CD{
		public static final String TIME = "020000";
		public static final String TIME1 = "020001";
		public static final String TIME2 = "020002";
		public static final String TIME3 = "020003";
		public static final String TIME4 = "020004";
		public static final String TIME5 = "020005";
		public static final String TIME6 = "020006";
		public static final String TIME7 = "020007";
		public static final String TIME8 = "020008";
	}
	
	/**订单状态*/
	public static interface ORDER_STATUS{
		public static final String APPOINTED = "030001";
		public static final String CANCLE = "030002";
		public static final String HAIRING = "030003";
		public static final String STAYPAY = "030005";
		public static final String STAYCOMMENT = "030006";
		public static final String COMPLETE = "030007";
		public static final String ALL = "030000";
	}
	
	/**支付方式*/
	public static interface PAY_TYPE{
		public static final String WEIXIN_PAY = "040001";
		public static final String ALI_PAY = "040002";
		public static final String YUER_PAY = "040008";
		public static final String CARD_PAY = "040009";
	}
	
	/**评论类型*/
	public static interface COMMENT_TYPE{
		public static final String MOST_SATISFY = "050001";
		public static final String SATISFY = "050002";
		public static final String NORMAL = "050003";
	}
	
	/**商品类型*/
	public static interface PRODUCT_TYPE{
		public static final String HAIR_SERIALS = "060001";
		public static final String BEAUTICIAN_SERIALS = "060002";
		public static final String XI = "060003";
		public static final String JIAN = "060004";
		public static final String CHUI = "060005";
		public static final String TANG = "060006";
		public static final String RAN = "060007";
		public static final String HULI = "060008";
		public static final String XIHU = "060009";
		public static final String MEILI = "060010";
		public static final String JIAMENG = "060011";
		public static final String ALL = "060000";
	}
	
	/**积分类型*/
	public static interface POINTS_TYPE{
		public static final String SIGN = "080001";
	}
	
	/**员工状态*/
	public static interface EMPLOYEE_WORK_STATUS{
		public static final String SIGN = "070001";
		public static final String NOT_SIGN = "070002";
		public static final String GO_OUT = "070003";
	}
	
	public static interface HOST{
		public static final String LOCALHOST = "http://119.23.75.45:88/icon/";
		public static final String PRODUCT = "http://119.23.75.45:88/icon/";
	}
	
	public static interface FILE_HOST{
		public static final String LOCALHOST = "E:\\upload\\icon\\";
		public static final String PRODUCT = "E:\\upload\\icon\\";
	}
	
	public static interface ACTIVITY_TYPE{
		public static final String DISCOUNT = "100001";
		public static final String RECHARGE_DISCOUNT = "100002";
		public static final String RECHARGE_ADDPOINT = "100003";
	}
	
	public static interface MEMBER_CARD_TYPE{
		public static final String FIVE_DISCOUNT = "110001";
		public static final String SEVEN_DISCOUNT = "110002";
	}
	
	public static interface MONEY_CHANGE_TYPE{
		public static final String PAY_ORDER = "120001";
		public static final String PHONE_RECHARGE = "120002";
		public static final String OUT_LINE = "120003";
	}
	
	public static interface CARD_RECORD_TYPE{
		public static final String INCREASE = "130001";
		public static final String DECREASE = "130002";
		public static final String NOTCHANGE = "130003";
	}
}
