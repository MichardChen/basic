package my.pvcloud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.core.constants.Constants;
import my.core.model.City;
import my.core.model.CodeMst;
import my.core.model.District;
import my.core.model.GetTeaRecord;
import my.core.model.Member;
import my.core.model.MemberStore;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.Province;
import my.core.model.ReceiveAddress;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.model.WarehouseTeaMemberVO;
import my.pvcloud.service.OrderService;
import my.pvcloud.service.WarehouseTeaMemberService;
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

@ControllerBind(key = "/wtmInfo", path = "/pvcloud")
public class WarehouseTeaMemberController extends Controller {

	WarehouseTeaMemberService service = Enhancer.enhance(WarehouseTeaMemberService.class);

	int page = 1;
	int size = 10;

	public void index() {

		removeSessionAttr("date");
		removeSessionAttr("saleMobile");
		removeSessionAttr("saleUserTypeCd");
		removeSessionAttr("tea");

		Page<WarehouseTeaMember> list = service.queryByPage(page, size);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for (WarehouseTeaMember order : list.getList()) {
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if (tea != null) {
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if (type != null) {
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock")) + "片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if (wareHouse != null) {
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)) {
				User user = User.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser("平台");
					model.setSaleUserType("平台");
				}
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)) {
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					Store store = Store.dao.queryMemberStore(user.getInt("id"));
					if (store != null) {
						model.setStore(store.getStr("store_name"));
					}
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}

			// 查看在售茶叶
			BigDecimal all = new BigDecimal("0");
			BigDecimal itemOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.ITEM);
			if (itemOnSale != null) {
				all = itemOnSale.multiply(new BigDecimal(StringUtil.toString(tea.getInt("size"))));
			}
			BigDecimal pieceOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.PIECE);
			all = all.add(new BigDecimal(StringUtil.toString(pieceOnSale)));
			model.setOnSale(StringUtil.toString(all) + "片");

			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}

	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage() {

		String pdate = getPara("date");
		this.setSessionAttr("date", pdate);

		String psaleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile", psaleMobile);

		String psaleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd", psaleUserTypeCd);

		String ptea = getPara("tea");
		this.setSessionAttr("tea", ptea);

		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}

		Page<WarehouseTeaMember> list = service.queryWarehouseTeaMemberByParam(page, size, pdate, psaleMobile,
				psaleUserTypeCd, ptea);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for (WarehouseTeaMember order : list.getList()) {
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if (tea != null) {
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if (type != null) {
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock")) + "片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if (wareHouse != null) {
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)) {
				User user = User.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser("平台");
					model.setSaleUserType("平台");
				}
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)) {
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					Store store = Store.dao.queryMemberStore(user.getInt("id"));
					if (store != null) {
						model.setStore(store.getStr("store_name"));
					}
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}

			// 查看在售茶叶
			BigDecimal all = new BigDecimal("0");
			BigDecimal itemOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.ITEM);
			if (itemOnSale != null) {
				all = itemOnSale.multiply(new BigDecimal(StringUtil.toString(tea.getInt("size"))));
			}
			BigDecimal pieceOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.PIECE);
			all = all.add(new BigDecimal(StringUtil.toString(pieceOnSale)));
			model.setOnSale(StringUtil.toString(all) + "片");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}

	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage() {

		String pdate = getPara("date");
		this.setSessionAttr("date", pdate);

		String ptea = getPara("tea");
		this.setSessionAttr("tea", ptea);

		String psaleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile", psaleMobile);

		String psaleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd", psaleUserTypeCd);

		Integer page = getParaToInt(1);
		if (page == null || page == 0) {
			page = 1;
		}

		Page<WarehouseTeaMember> list = service.queryWarehouseTeaMemberByParam(page, size, pdate, psaleMobile,
				psaleUserTypeCd, ptea);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for (WarehouseTeaMember order : list.getList()) {
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if (tea != null) {
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if (type != null) {
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock")) + "片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if (wareHouse != null) {
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)) {
				User user = User.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser("平台");
					model.setSaleUserType("平台");
				}
			}
			if (StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)) {
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if (user != null) {
					Store store = Store.dao.queryMemberStore(user.getInt("id"));
					if (store != null) {
						model.setStore(store.getStr("store_name"));
					}
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}

			// 查看在售茶叶
			BigDecimal all = new BigDecimal("0");
			BigDecimal itemOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.ITEM);
			if (itemOnSale != null) {
				all = itemOnSale.multiply(new BigDecimal(StringUtil.toString(tea.getInt("size"))));
			}
			BigDecimal pieceOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(order.getInt("id"),
					Constants.TEA_UNIT.PIECE);
			all = all.add(new BigDecimal(StringUtil.toString(pieceOnSale)));
			model.setOnSale(StringUtil.toString(all) + "片");
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}

	public void exportData() {
		String path = "//home//data//images//excel//在库茶叶.xls";
		try {
			String date = getPara("date");
			String tea = getPara("tea");
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
			String[] titles = new String[] { "茶叶名称", "茶叶类型", "仓库", "库存", "在售", "门店名称", "商家名称", "商家注册号码", "商家类型",
					"入库时间" };
			for (int i = 0; i < titles.length; i++) {
				cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles[i]);
			}

			List<WarehouseTeaMember> list = WarehouseTeaMember.dao.exportData(date, saleMobile, saleUserTypeCd, tea);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					XSSFRow bodyRow = sheet.createRow(j + 1);

					WarehouseTeaMember record = list.get(j);

					Tea tea1 = Tea.dao.queryById(record.getInt("tea_id"));
					if (tea1 != null) {
						// 茶叶名称
						cell = bodyRow.createCell(0);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(tea1.getStr("tea_title"));
						CodeMst type = CodeMst.dao.queryCodestByCode(tea1.getStr("type_cd"));
						if (type != null) {
							// 茶叶类型
							cell = bodyRow.createCell(1);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(type.getStr("name"));
						}else{
							cell = bodyRow.createCell(1);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("");
						}
					}

					// 仓库
					WareHouse wareHouse = WareHouse.dao.queryById(record.getInt("warehouse_id"));
					if (wareHouse != null) {
						cell = bodyRow.createCell(2);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue(wareHouse.getStr("warehouse_name"));
					}else{
						cell = bodyRow.createCell(2);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("");
					}

					// 库存
					cell = bodyRow.createCell(3);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getInt("stock")) + "片");

					// 查看在售茶叶
					BigDecimal all = new BigDecimal("0");
					BigDecimal itemOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(record.getInt("id"),
							Constants.TEA_UNIT.ITEM);
					if (itemOnSale != null) {
						all = itemOnSale.multiply(new BigDecimal(StringUtil.toString(tea1.getInt("size"))));
					}
					BigDecimal pieceOnSale = WarehouseTeaMemberItem.dao.queryOnSaleListCount(record.getInt("id"),
							Constants.TEA_UNIT.PIECE);
					all = all.add(new BigDecimal(StringUtil.toString(pieceOnSale)));
					// 在售
					cell = bodyRow.createCell(4);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(all) + "片");

					// 入库时间
					cell = bodyRow.createCell(9);
					cell.setCellStyle(bodyStyle);
					cell.setCellValue(StringUtil.toString(record.getTimestamp("create_time")));

					if (StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)) {
						User user = User.dao.queryById(record.getInt("member_id"));
						cell = bodyRow.createCell(5);
						cell.setCellStyle(bodyStyle);
						cell.setCellValue("");
						if (user != null) {
							// 商家名称
							cell = bodyRow.createCell(6);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("平台");

							// 商家注册号码
							cell = bodyRow.createCell(7);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(user.getStr("mobile"));
							// 商家类型
							cell = bodyRow.createCell(8);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("平台");
						}else{
							// 商家名称
							cell = bodyRow.createCell(6);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("");

							// 商家注册号码
							cell = bodyRow.createCell(7);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("");
							// 商家类型
							cell = bodyRow.createCell(8);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("");
						}
					}

					if (StringUtil.equals(record.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)) {
						Member user = Member.dao.queryById(record.getInt("member_id"));
						if (user != null) {
							Store store = Store.dao.queryMemberStore(user.getInt("id"));
							if (store != null) {
								// 门店名称
								cell = bodyRow.createCell(5);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue(store.getStr("store_name"));
							}else{
								cell = bodyRow.createCell(5);
								cell.setCellStyle(bodyStyle);
								cell.setCellValue("");
							}
							// 商家名称
							cell = bodyRow.createCell(6);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(user.getStr("name"));

							// 商家注册号码
							cell = bodyRow.createCell(7);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue(user.getStr("mobile"));
							// 商家类型
							cell = bodyRow.createCell(8);
							cell.setCellStyle(bodyStyle);
							cell.setCellValue("用户");
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
