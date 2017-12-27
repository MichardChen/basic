package my.pvcloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.service.SaleOrderService;
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

@ControllerBind(key = "/saleorderInfo", path = "/pvcloud")
public class SaleOrderController extends Controller {

	SaleOrderService service = Enhancer.enhance(SaleOrderService.class);

	int page = 1;
	int size = 10;

	public void index() {

		removeSessionAttr("title");
		removeSessionAttr("saleMobile");
		removeSessionAttr("saleUserTypeCd");
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明

		}

		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByPage(page, size, Constants.TEA_STATUS.ON_SALE);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (WarehouseTeaMemberItem order : list.getList()) {
			model = new OrderListVO();

			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if (sizeCodeMst != null) {
				size = sizeCodeMst.getStr("name");
				model.setSize(size);
				model.setStock(order.getInt("quality") + sizeCodeMst.getStr("name"));
			} else {
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if (status != null) {
				model.setStatus(status.getStr("name"));
			} else {
				model.setStatus("");
			}
			if (wtm != null) {
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if (tea == null) {
					continue;
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
				model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("update_time")));
				String memberTypeCd = wtm.getStr("member_type_cd");
				if (StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
					Member member = Member.dao.queryById(wtm.getInt("member_id"));
					if (member != null) {
						model.setSaleUser(member.getStr("mobile"));
					} else {
						continue;
					}
				} else {
					User user = User.dao.queryById(wtm.getInt("member_id"));
					if (user != null) {
						model.setSaleUser(user.getStr("username"));
					} else {
						continue;
					}
				}
				// 查询相关库存数据
				int onSale = order.getInt("quality") == null ? 0 : order.getInt("quality");
				int originStock = order.getInt("origin_stock") == null ? 0 : order.getInt("origin_stock");
				int cancleQuality = order.getInt("cancle_quality") == null ? 0 : order.getInt("cancle_quality");
				model.setOnSale(StringUtil.toString(onSale) + size);
				model.setCancle(StringUtil.toString(cancleQuality) + size);
				model.setOriginStock(StringUtil.toString(originStock) + size);
				model.setHaveSale(StringUtil.toString(originStock - cancleQuality - onSale) + size);

				models.add(model);
			} else {
				continue;
			}

		}
		setAttr("list", list);
		setAttr("sList", models);
		render("saleorder.jsp");
	}

	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage() {

		String title = getSessionAttr("title");
		this.setSessionAttr("title", title);

		String saleMobile = getSessionAttr("saleMobile");
		this.setSessionAttr("saleMobile", saleMobile);

		String saleUserTypeCd = getSessionAttr("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd", saleUserTypeCd);
		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明
		}
		int saleUserId = 0;
		if (StringUtil.isNoneBlank(saleMobile)) {
			if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.PLATFORM_USER)) {
				User user = User.dao.queryUser(saleMobile);
				if (user != null) {
					saleUserId = user.getInt("user_id");
				}
			}

			if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
				Member member = Member.dao.queryMember(saleMobile);
				if (member != null) {
					saleUserId = member.getInt("user_id");
				}
			}
		}
		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByParam(page, size, title, saleUserId, saleUserTypeCd,
				Constants.TEA_STATUS.ON_SALE);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (WarehouseTeaMemberItem order : list.getList()) {
			model = new OrderListVO();

			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if (sizeCodeMst != null) {
				size = sizeCodeMst.getStr("name");
				model.setSize(size);
				model.setStock(order.getInt("quality") + sizeCodeMst.getStr("name"));
			} else {
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if (status != null) {
				model.setStatus(status.getStr("name"));
			} else {
				model.setStatus("");
			}
			if (wtm != null) {
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if (tea == null) {
					continue;
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
				model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("update_time")));
				String memberTypeCd = wtm.getStr("member_type_cd");
				if (StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
					Member member = Member.dao.queryById(wtm.getInt("member_id"));
					if (member != null) {
						model.setSaleUser(member.getStr("mobile"));
					} else {
						continue;
					}
				} else {
					User user = User.dao.queryById(wtm.getInt("member_id"));
					if (user != null) {
						model.setSaleUser(user.getStr("username"));
					} else {
						continue;
					}
				}
				// 查询相关库存数据
				int onSale = order.getInt("quality") == null ? 0 : order.getInt("quality");
				int originStock = order.getInt("origin_stock") == null ? 0 : order.getInt("origin_stock");
				int cancleQuality = order.getInt("cancle_quality") == null ? 0 : order.getInt("cancle_quality");
				model.setOnSale(StringUtil.toString(onSale) + size);
				model.setCancle(StringUtil.toString(cancleQuality) + size);
				model.setOriginStock(StringUtil.toString(originStock) + size);
				model.setHaveSale(StringUtil.toString(originStock - cancleQuality - onSale) + size);

				models.add(model);
			} else {
				continue;
			}

		}
		setAttr("list", list);
		setAttr("sList", models);
		render("saleorder.jsp");
	}

	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage() {

		String ptitle = getPara("title");
		this.setSessionAttr("title", ptitle);

		String saleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile", saleMobile);

		String saleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd", saleUserTypeCd);
		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}
		String flg = getPara(0);
		if (StringUtil.equals(flg, "1")) {
			// 默认发售说明

		}
		int saleUserId = 0;
		if (StringUtil.isNoneBlank(saleMobile)) {
			if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.PLATFORM_USER)) {
				User user = User.dao.queryUser(saleMobile);
				if (user != null) {
					saleUserId = user.getInt("user_id");
				}
			}

			if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
				Member member = Member.dao.queryMember(saleMobile);
				if (member != null) {
					saleUserId = member.getInt("id");
				}
			}
		}
		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByParam(page, size, ptitle, saleUserId, saleUserTypeCd,
				Constants.TEA_STATUS.ON_SALE);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for (WarehouseTeaMemberItem order : list.getList()) {
			model = new OrderListVO();
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if (sizeCodeMst != null) {
				size = sizeCodeMst.getStr("name");
				model.setSize(size);
				model.setStock(order.getInt("quality") + sizeCodeMst.getStr("name"));
			} else {
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if (status != null) {
				model.setStatus(status.getStr("name"));
			} else {
				model.setStatus("");
			}

			if (wtm != null) {
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if (tea == null) {
					continue;
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
				model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("update_time")));
				String memberTypeCd = wtm.getStr("member_type_cd");
				if (StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
					Member member = Member.dao.queryById(wtm.getInt("member_id"));
					if (member != null) {
						model.setSaleUser(member.getStr("mobile"));
					} else {
						continue;
					}
				} else {
					User user = User.dao.queryById(wtm.getInt("member_id"));
					if (user != null) {
						model.setSaleUser(user.getStr("username"));
					} else {
						continue;
					}
				}
				// 查询相关库存数据
				int onSale = order.getInt("quality") == null ? 0 : order.getInt("quality");
				int originStock = order.getInt("origin_stock") == null ? 0 : order.getInt("origin_stock");
				int cancleQuality = order.getInt("cancle_quality") == null ? 0 : order.getInt("cancle_quality");
				model.setOnSale(StringUtil.toString(onSale) + size);
				model.setCancle(StringUtil.toString(cancleQuality) + size);
				model.setOriginStock(StringUtil.toString(originStock) + size);
				model.setHaveSale(StringUtil.toString(originStock - cancleQuality - onSale) + size);

				models.add(model);
			} else {
				continue;
			}

		}
		setAttr("list", list);
		setAttr("sList", models);
		render("saleorder.jsp");
	}

	public void exportData() {
		String path = "//home//data//images//excel//在售茶叶.xls";
		try {
			String ptitle = getPara("title");
			String saleMobile = getPara("saleMobile");
			String saleUserTypeCd = getPara("saleUserTypeCd");
			FileOutputStream os = new FileOutputStream(new File(path));
			// 创建一个workbook 对应一个excel应用文件
			XSSFWorkbook workBook = new XSSFWorkbook();
			// 在workbook中添加一个sheet,对应Excel文件中的sheet
			XSSFSheet sheet = workBook.createSheet("取茶记录");
			ExportUtil exportUtil = new ExportUtil(workBook, sheet);
			XSSFCellStyle headStyle = exportUtil.getHeadStyle();
			XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();

			XSSFRow headRow = sheet.createRow(0);
			XSSFCell cell = null;
			String[] titles = new String[] { "产品名称", "类型", "仓库", "店铺", "卖家", "销售价", "原始库存	", "已售", "在售", "撤单", "更新时间",
					"状态" };
			for (int i = 0; i < titles.length; i++) {
				cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles[i]);
			}

			int saleUserId = 0;
			if (StringUtil.isNoneBlank(saleMobile)) {
				if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.PLATFORM_USER)) {
					User user = User.dao.queryUser(saleMobile);
					if (user != null) {
						saleUserId = user.getInt("user_id");
					}
				}

				if (StringUtil.equals(saleUserTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
					Member member = Member.dao.queryMember(saleMobile);
					if (member != null) {
						saleUserId = member.getInt("id");
					}
				}
			}
			List<WarehouseTeaMemberItem> list = WarehouseTeaMemberItem.dao.exportData(ptitle, saleUserId,
					saleUserTypeCd, Constants.TEA_STATUS.ON_SALE);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					XSSFRow bodyRow = sheet.createRow(j + 1);

					WarehouseTeaMemberItem order = list.get(j);

					WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
					// 销售价
					cell = bodyRow.createCell(5);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(order.getBigDecimal("price")));

					String sizeTypeCd = order.getStr("size_type_cd");
					CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
					String size = "";
					if (sizeCodeMst != null) {
						size = sizeCodeMst.getStr("name");
					}
					CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
					if (status != null) {
						// 状态
						cell = bodyRow.createCell(11);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(status.getStr("name"));
					} else {
						cell = bodyRow.createCell(11);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("");
					}

					if (wtm != null) {
						Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
						if (tea == null) {
							continue;
						}

						if (StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))) {
							Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
							if (store != null) {
								// 店铺
								cell = bodyRow.createCell(3);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(store.getStr("store_name"));
							} else {
								cell = bodyRow.createCell(3);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
							}
						} else {
							cell = bodyRow.createCell(3);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("平台");
						}

						CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
						// 类型
						if (type != null) {
							cell = bodyRow.createCell(3);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(type.getStr("name"));
						} else {
							cell = bodyRow.createCell(3);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(type.getStr("name"));
						}

						WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
						if (house != null) {
							cell = bodyRow.createCell(2);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(house.getStr("warehouse_name"));
						} else {
							cell = bodyRow.createCell(2);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("");
						}
						// 产品名称
						cell = bodyRow.createCell(0);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(tea.getStr("tea_title"));
						// 更新时间
						cell = bodyRow.createCell(10);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(DateUtil.formatTimestampForDate(order.getTimestamp("update_time")));
						String memberTypeCd = wtm.getStr("member_type_cd");
						if (StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)) {
							Member member = Member.dao.queryById(wtm.getInt("member_id"));
							if (member != null) {
								// 卖家
								cell = bodyRow.createCell(4);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(member.getStr("mobile"));
							} else {
								cell = bodyRow.createCell(4);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
								continue;
							}
						} else {
							User user = User.dao.queryById(wtm.getInt("member_id"));
							if (user != null) {
								cell = bodyRow.createCell(4);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(user.getStr("username"));
							} else {
								cell = bodyRow.createCell(4);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
								continue;
							}
						}
						// 查询相关库存数据
						int onSale = order.getInt("quality") == null ? 0 : order.getInt("quality");
						int originStock = order.getInt("origin_stock") == null ? 0 : order.getInt("origin_stock");
						int cancleQuality = order.getInt("cancle_quality") == null ? 0 : order.getInt("cancle_quality");
						// 在售
						cell = bodyRow.createCell(8);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(onSale) + size);
						// 撤单
						cell = bodyRow.createCell(9);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(cancleQuality) + size);
						// 原始库存
						cell = bodyRow.createCell(6);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(originStock) + size);
						// 已售
						cell = bodyRow.createCell(7);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(StringUtil.toString(originStock - cancleQuality - onSale) + size);

						Tea tea1 = Tea.dao.queryById(wtm.getInt("tea_id"));
						if (tea1 != null) {
							// 茶叶名称

							CodeMst t = CodeMst.dao.queryCodestByCode(tea1.getStr("type_cd"));
							if (t != null) {
								cell = bodyRow.createCell(1);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(t.getStr("name"));
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
