package my.pvcloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.service.OrderService;
import my.pvcloud.service.SellManageService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.ExportUtil;
import my.pvcloud.util.StringUtil;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/sellManageInfo", path = "/pvcloud")
public class SellManageController extends Controller {

	SellManageService service = Enhancer.enhance(SellManageService.class);

	int page = 1;
	int size = 10;

	public void index() {

		removeSessionAttr("title");
		removeSessionAttr("saleMobile");
		removeSessionAttr("buyMobile");
		/*removeSessionAttr("orderNo");
		removeSessionAttr("status");
		removeSessionAttr("addTime");
		removeSessionAttr("payTime");
		removeSessionAttr("saleMobile");
		removeSessionAttr("saleUserTypeCd");
		removeSessionAttr("buyMobile");*/
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明

		}
		Page<OrderItem> list = service.queryByPage(page, size);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (OrderItem order : list.getList()) {
			model = new OrderListVO();
			model.setAmount(StringUtil.toString(order.getBigDecimal("item_amount")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if (wtmItem != null) {
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				model.setPrice(wtmItem.getBigDecimal("price"));
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				if (size != null) {
					model.setStock(order.getInt("quality") + size.getStr("name"));
				} else {
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if (wtm != null) {
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if (tea == null) {
						continue;
					}
					model.setProductUrl(tea.getStr("desc_url"));
					if (StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))) {
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if (store != null) {
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					} else {
						model.setStore("平台");
					}

					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if (type != null) {
						model.setType(type.getStr("name"));
					}

					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if (house != null) {
						model.setWareHouse(house.getStr("warehouse_name"));
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if (order2 != null) {
						////
						CodeMst orderStatus = CodeMst.dao.queryCodestByCode(order2.getStr("order_status"));
						if (orderStatus != null) {
							model.setStatus(orderStatus.getStr("name"));
						}
						model.setOrderNo(order2.getStr("order_no"));
						model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
						model.setPayTime(StringUtil.toString(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if (StringUtil.isBlank(saleUserType)) {
							continue;
						}
						int saleId = order.getInt("sale_id");
						if (StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
							Member member = Member.dao.queryById(saleId);
							if (member != null) {
								model.setSaleUser(member.getStr("mobile"));
							}
						} else {
							User user = User.dao.queryById(saleId);
							if (user != null) {
								model.setSaleUser(user.getStr("username"));
							}
						}
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if (m != null) {
							model.setBuyUser(m.getStr("mobile"));
							int storeId = 0;
							if(m.getInt("store_id") != null){
								storeId = m.getInt("store_id");
							}
							Store store = Store.dao.queryById(storeId);
							if(store != null){
								Member seller = Member.dao.queryById(store.getInt("member_id"));
								if(seller != null){
									model.setSellMobile(seller.getStr("mobile"));
									model.setSellName(seller.getStr("name"));
								}
							}
						}
					} else {
						continue;
					}
					models.add(model);
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("sellorder.jsp");
	}

	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage() {

		String ptitle = getSessionAttr("title");
		this.setSessionAttr("title", ptitle);
		
		String psellerMobile = getSessionAttr("saleMobile");
		this.setSessionAttr("saleMobile", psellerMobile);

		String buyMobile = getSessionAttr("buyMobile");
		this.setSessionAttr("buyMobile", buyMobile);

		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明

		}
		int storeIds = 0;
		if(StringUtil.isNoneBlank(psellerMobile)){
			Member storeUserMember = Member.dao.queryMember(psellerMobile);
			if(storeUserMember != null){
				Store store = Store.dao.queryMemberStore(storeUserMember.getInt("id"));
				if(store != null){
					storeIds = store.getInt("id");
				}
			}
		}

		// 买家
		int buyUserId = 0;
		if (StringUtil.isNoneBlank(buyMobile)) {
			Member member = Member.dao.queryMember(buyMobile);
			if (member != null) {
				buyUserId = member.getInt("id");
			}
		}
		Page<OrderItem> list = service.queryOrderItemByParam(page, size, ptitle, buyMobile, psellerMobile,storeIds);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (OrderItem order : list.getList()) {
			model = new OrderListVO();
			model.setAmount(StringUtil.toString(order.getBigDecimal("item_amount")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if (wtmItem != null) {
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				model.setPrice(wtmItem.getBigDecimal("price"));
				if (size != null) {
					model.setStock(order.getInt("quality") + size.getStr("name"));
				} else {
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if (wtm != null) {
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if (tea == null) {
						continue;
					}
					model.setProductUrl(tea.getStr("desc_url"));
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if (type != null) {
						model.setType(type.getStr("name"));
					}

					if (StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))) {
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if (store != null) {
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					} else {
						model.setStore("平台");
					}

					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if (house != null) {
						model.setWareHouse(house.getStr("warehouse_name"));
					}

					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if (order2 != null) {
						CodeMst orderStatus = CodeMst.dao.queryCodestByCode(order2.getStr("order_status"));
						if (orderStatus != null) {
							model.setStatus(orderStatus.getStr("name"));
						}
						model.setOrderNo(order2.getStr("order_no"));
						model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
						model.setPayTime(StringUtil.toString(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if (StringUtil.isBlank(saleUserType)) {
							continue;
						}
						int saleId = order.getInt("sale_id");
						if (StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
							Member member = Member.dao.queryById(saleId);
							if (member != null) {
								model.setSaleUser(member.getStr("mobile"));
							}
						} else {
							User user = User.dao.queryById(saleId);
							if (user != null) {
								model.setSaleUser(user.getStr("username"));
							}
						}
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if (m != null) {
							model.setBuyUser(m.getStr("mobile"));
							int storeId = 0;
							if(m.getInt("store_id") != null){
								storeId = m.getInt("store_id");
							}
							Store store = Store.dao.queryById(storeId);
							if(store != null){
								Member seller = Member.dao.queryById(store.getInt("member_id"));
								if(seller != null){
									model.setSellMobile(seller.getStr("mobile"));
									model.setSellName(seller.getStr("name"));
								}
							}
						}
					} else {
						continue;
					}
					models.add(model);
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("sellorder.jsp");

	}

	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage() {

		String ptitle = getPara("title");
		this.setSessionAttr("title", ptitle);

		String porderNo = getPara("orderNo");
		this.setSessionAttr("orderNo", porderNo);

		String pstatus = getPara("status");
		this.setSessionAttr("status", pstatus);

		String ppayTime = getPara("payTime");
		this.setSessionAttr("payTime", ppayTime);

		String saleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile", saleMobile);

		String saleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd", saleUserTypeCd);

		String buyMobile = getPara("buyMobile");
		this.setSessionAttr("buyMobile", buyMobile);

		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明

		}

		int storeIds = 0;
		if(StringUtil.isNoneBlank(saleMobile)){
			Member storeUserMember = Member.dao.queryMember(saleMobile);
			if(storeUserMember != null){
				Store store = Store.dao.queryMemberStore(storeUserMember.getInt("id"));
				if(store != null){
					storeIds = store.getInt("id");
				}
			}
		}

		// 买家
		int buyUserId = 0;
		if (StringUtil.isNoneBlank(buyMobile)) {
			Member member = Member.dao.queryMember(buyMobile);
			if (member != null) {
				buyUserId = member.getInt("id");
			}
		}
		Page<OrderItem> list = service.queryOrderItemByParam(page, size, ptitle, buyMobile, saleMobile,storeIds);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (OrderItem order : list.getList()) {
			model = new OrderListVO();
			model.setAmount(StringUtil.toString(order.getBigDecimal("item_amount")));
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if (wtmItem != null) {
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				if (size != null) {
					model.setStock(order.getInt("quality") + size.getStr("name"));
				} else {
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				model.setPrice(wtmItem.getBigDecimal("price"));
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if (wtm != null) {
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if (tea == null) {
						continue;
					}
					model.setProductUrl(tea.getStr("desc_url"));
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if (type != null) {
						model.setType(type.getStr("name"));
					}

					if (StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))) {
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if (store != null) {
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					} else {
						model.setStore("平台");
					}

					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if (house != null) {
						model.setWareHouse(house.getStr("warehouse_name"));
					}

					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if (order2 != null) {
						CodeMst orderStatus = CodeMst.dao.queryCodestByCode(order2.getStr("order_status"));
						if (orderStatus != null) {
							model.setStatus(orderStatus.getStr("name"));
						}
						model.setOrderNo(order2.getStr("order_no"));
						model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
						model.setPayTime(StringUtil.toString(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if (StringUtil.isBlank(saleUserType)) {
							continue;
						}
						int saleId = order.getInt("sale_id");
						if (StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
							Member member = Member.dao.queryById(saleId);
							if (member != null) {
								model.setSaleUser(member.getStr("mobile"));
							}
						} else {
							User user = User.dao.queryById(saleId);
							if (user != null) {
								model.setSaleUser(user.getStr("username"));
							}
						}
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if (m != null) {
							model.setBuyUser(m.getStr("mobile"));
							int storeId = 0;
							if(m.getInt("store_id") != null){
								storeId = m.getInt("store_id");
							}
							Store store = Store.dao.queryById(storeId);
							if(store != null){
								Member seller = Member.dao.queryById(store.getInt("member_id"));
								if(seller != null){
									model.setSellMobile(seller.getStr("mobile"));
									model.setSellName(seller.getStr("name"));
								}
							}
						}
					} else {
						continue;
					}
					models.add(model);
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("sellorder.jsp");

	}

	public void exportData() {
		
		String ptitle = getPara("title");
		String saleMobile = getPara("saleMobile");
		String buyMobile = getPara("buyMobile");
		

		String path = "//home//data//images//excel//经销商管理.xls";
		try {
			FileOutputStream os = new FileOutputStream(new File(path));
			// 创建一个workbook 对应一个excel应用文件
			XSSFWorkbook workBook = new XSSFWorkbook();
			// 在workbook中添加一个sheet,对应Excel文件中的sheet
			XSSFSheet sheet = workBook.createSheet("订单信息");
			ExportUtil exportUtil = new ExportUtil(workBook, sheet);
			XSSFCellStyle headStyle = exportUtil.getHeadStyle();
			XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();

			XSSFRow headRow = sheet.createRow(0);
			XSSFCell cell = null;
			String[] titles = new String[] { "订单号", "产品名称", "类型", "仓库","经销商","经销商注册电话","买家", "下单时间", "付款时间",
					"数量", "单价", "总金额", "状态" };
			for (int i = 0; i < titles.length; i++) {
				cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles[i]);
			}

			int storeIds = 0;
			if(StringUtil.isNoneBlank(saleMobile)){
				Member storeUserMember = Member.dao.queryMember(saleMobile);
				if(storeUserMember != null){
					Store store = Store.dao.queryMemberStore(storeUserMember.getInt("id"));
					if(store != null){
						storeIds = store.getInt("id");
					}
				}
			}

			// 买家
			int buyUserId = 0;
			if (StringUtil.isNoneBlank(buyMobile)) {
				Member member = Member.dao.queryMember(buyMobile);
				if (member != null) {
					buyUserId = member.getInt("id");
				}
			}

			List<OrderItem> list = OrderItem.dao.exportSellData(ptitle, buyMobile, saleMobile, storeIds);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					XSSFRow bodyRow = sheet.createRow(j + 1);

					OrderItem item = list.get(j);

					// 总金额
					cell = bodyRow.createCell(11);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(item.getBigDecimal("item_amount")));

					WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao
							.queryByKeyId(item.getInt("wtm_item_id"));
					if (wtmItem != null) {
						int wtmId = wtmItem.getInt("warehouse_tea_member_id");
						String sizeTypeCd = wtmItem.getStr("size_type_cd");
						CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
						// 数量
						if (size != null) {
							cell = bodyRow.createCell(9);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(item.getInt("quality") + size.getStr("name"));
						} else {
							cell = bodyRow.createCell(9);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(StringUtil.toString(item.getInt("quality")));
						}
						// 单价
						cell = bodyRow.createCell(10);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(wtmItem.getBigDecimal("price")));
						WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
						if (wtm != null) {
							Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
							if (tea == null) {
								continue;
							}

							CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
							if (type != null) {

								// 单价
								cell = bodyRow.createCell(2);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(type.getStr("name"));
							} else {
								cell = bodyRow.createCell(2);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
							}

							/*if (StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))) {
								Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
								if (store != null) {
									// 卖家 门店
									cell = bodyRow.createCell(5);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(store.getStr("link_phone"));

									cell = bodyRow.createCell(6);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(store.getStr("store_name"));
									
									cell = bodyRow.createCell(7);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(store.getStr("link_phone"));
								} else {
									cell = bodyRow.createCell(5);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");

									cell = bodyRow.createCell(6);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
									
									cell = bodyRow.createCell(7);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
								}
							} else {
								cell = bodyRow.createCell(5);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");

								cell = bodyRow.createCell(6);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("平台");
							}*/

							WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
							if (house != null) {
								// 仓库
								cell = bodyRow.createCell(3);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(house.getStr("warehouse_name"));
							}
							int buyId = item.getInt("member_id");
							Member m = Member.dao.queryById(buyId);
							//经销商和注册电话
							int storeId = 0;
							if(m.getInt("store_id") != null){
								storeId = m.getInt("store_id");
							}
							Store store = Store.dao.queryById(storeId);
							if(store != null){
								Member seller = Member.dao.queryById(store.getInt("member_id"));
								if(seller != null){
									cell = bodyRow.createCell(4);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(seller.getStr("name"));
									
									cell = bodyRow.createCell(5);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(seller.getStr("mobile"));
								}else{
									cell = bodyRow.createCell(4);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
									
									cell = bodyRow.createCell(5);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
								}
							}else{
								cell = bodyRow.createCell(4);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
								
								cell = bodyRow.createCell(5);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
							}
							
							
							// 产品名称
							cell = bodyRow.createCell(1);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(tea.getStr("tea_title"));

							Order order2 = Order.dao.queryById(item.getInt("order_id"));
							if (order2 != null) {
								CodeMst orderStatus = CodeMst.dao.queryCodestByCode(order2.getStr("order_status"));
								if (orderStatus != null) {
									cell = bodyRow.createCell(12);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(orderStatus.getStr("name"));
								} else {
									cell = bodyRow.createCell(12);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
								}
								// 订单号
								cell = bodyRow.createCell(0);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(order2.getStr("order_no"));
								// 下单时间
								cell = bodyRow.createCell(7);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(StringUtil.toString(item.getTimestamp("create_time")));
								// 付款时间
								cell = bodyRow.createCell(8);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(StringUtil.toString(order2.getTimestamp("pay_time")));

								String saleUserType = item.getStr("sale_user_type");
								if (StringUtil.isBlank(saleUserType)) {
									continue;
								}
								int saleId = item.getInt("sale_id");
								/*if (StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
									Member member = Member.dao.queryById(saleId);
									if (member != null) {
										// 卖家
										cell = bodyRow.createCell(5);
										cell.setCellStyle(bodyStyle);
										cell.setCellValue(member.getStr("mobile"));
									} else {
										// 卖家
										cell = bodyRow.createCell(5);
										cell.setCellStyle(bodyStyle);
										cell.setCellValue("");
									}
								} else {
									User user = User.dao.queryById(saleId);
									if (user != null) {
										// 卖家
										cell = bodyRow.createCell(5);
										cell.setCellStyle(bodyStyle);
										cell.setCellValue(user.getStr("username"));
									} else {
										// 卖家
										cell = bodyRow.createCell(5);
										cell.setCellStyle(bodyStyle);
										cell.setCellValue("");
									}
								}*/
								/*int buyIds = order2.getInt("member_id");
								Member mm = Member.dao.queryById(buyIds);*/
								if (m != null) {
									// 买家
									cell = bodyRow.createCell(6);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue(m.getStr("mobile"));
								} else {
									// 买家
									cell = bodyRow.createCell(6);
									cell.setCellStyle(bodyStyle);
									cell.setCellValue("");
								}
							} else {
								continue;
							}

						}
					}
				}
			}
			workBook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 判断路径是否存在
		if (new File(path).isFile()) {
			renderFile(new File(path));
		} else {
			renderNull();
		}
	}
}
