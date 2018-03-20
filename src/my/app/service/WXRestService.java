package my.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.core.constants.Constants;
import my.core.model.ReturnData;
import my.core.model.Store;
import my.core.model.StoreImage;
import my.core.model.StoreXcx;
import my.core.tx.TxProxy;
import my.core.vo.StoreDetailListVO;
import my.core.vo.XcxDistanceModel;
import my.core.vo.XcxTeaStoreListVO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.GeoUtil;
import my.pvcloud.util.StringUtil;

public class WXRestService {

	public static final LoginService service = TxProxy.newProxy(LoginService.class);

	public ReturnData queryTeaStoreDetail(LoginDTO dto) {
		ReturnData data = new ReturnData();
		Store store = Store.dao.queryById(dto.getId());
		if (store == null) {
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("数据出错，门店不存在");
			return data;
		}
		StoreDetailListVO vo = new StoreDetailListVO();
		vo.setAddress(store.getStr("store_address"));
		vo.setBusinessFromTime(store.getStr("business_fromtime"));
		vo.setBusinessToTime(store.getStr("business_totime"));
		vo.setLatitude(store.getFloat("latitude"));
		vo.setLongitude(store.getFloat("longitude"));
		vo.setMobile(store.getStr("link_phone"));
		vo.setName(store.getStr("store_name"));
		vo.setStoreDesc(store.getStr("store_desc"));
		List<StoreImage> images = StoreImage.dao.queryStoreImages(store.getInt("id"));
		List<String> imgs = new ArrayList<>();
		for (int i = 0; i < images.size(); i++) {
			StoreImage image = images.get(i);
			imgs.add(image.getStr("img"));
		}
		vo.setImgs(imgs);
		Map<String, Object> map = new HashMap<>();
		map.put("store", vo);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}

	public ReturnData queryTeaStoreList(LoginDTO dto) {

		ReturnData data = new ReturnData();
		// 经度
		double localLongtitude = Double
				.valueOf(StringUtil.isBlank(dto.getLocalLongtitude()) ? "116.40" : dto.getLocalLongtitude());
		// 纬度
		double localLatitude = Double
				.valueOf(StringUtil.isBlank(dto.getLocalLatitude()) ? "39.90" : dto.getLocalLatitude());

		// 查询出所有门店
		List<XcxTeaStoreListVO> resultList = new ArrayList<>();
		List<XcxDistanceModel> sortList = new ArrayList<>();
		XcxTeaStoreListVO v = null;
		List<Store> allStores = Store.dao.queryAllStoreForXcxList(Constants.VERTIFY_STATUS.CERTIFICATE_SUCCESS);
		for (Store store : allStores) {
			// 循环
			v = new XcxTeaStoreListVO();
			v.setStoreId(store.getInt("id"));
			v.setName(store.getStr("store_name"));
			v.setAddress(store.getStr("city_district"));
			if (store.getInt("member_id") != null) {
				v.setBusinessId(store.getInt("member_id"));
			} else {
				v.setBusinessId(0);
			}
			StoreXcx xcx = StoreXcx.dao.queryByStoreId(store.getInt("id"));
			if (xcx != null) {
				v.setAppId(xcx.getStr("appid"));
			}
			v.setBusinessTea(store.getStr("business_tea"));
			double lg = Double.valueOf(String.valueOf(store.getFloat("longitude")));
			double lat = Double.valueOf(String.valueOf(store.getFloat("latitude")));
			double dist = GeoUtil.getDistanceOfMeter(localLatitude, localLongtitude, lat, lg);
			StoreImage storeImage = StoreImage.dao.queryStoreFirstImages(v.getStoreId());
			if (storeImage != null) {
				v.setImg(storeImage.getStr("img"));
			}
			BigDecimal decimals = new BigDecimal(dist);
			if (decimals != null) {
				XcxDistanceModel model = new XcxDistanceModel();
				BigDecimal km = decimals.divide(new BigDecimal("1000"));
				if (km.compareTo(new BigDecimal("1")) != 1) {
					v.setDistance("1Km以内");
					model.setDistance(new BigDecimal("1"));
				} else {
					v.setDistance(StringUtil.toString(km.setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "Km");
					model.setDistance(km.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				model.setId(store.getInt("id"));
				model.setVo(v);
				sortList.add(model);

			}
		}
		Collections.sort(sortList);
		int fromRow = dto.getPageSize() * (dto.getPageNum() - 1);
		int toRow = fromRow + dto.getPageSize() - 1;

		if (sortList.size() < toRow) {
			toRow = sortList.size() - 1;
		}

		// 获取fromRow到toRow之间的key值
		for (int i = fromRow; ((i < sortList.size()) && (i <= toRow)); i++) {
			XcxDistanceModel k = sortList.get(i);
			resultList.add(k.getVo());
		}

		Map<String, Object> map = new HashMap<>();
		map.put("storeList", resultList);
		data.setData(map);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage("查询成功");
		return data;
	}
}
