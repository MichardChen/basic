package my.pvcloud.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.City;
import my.core.model.District;
import my.core.model.Document;
import my.core.model.Province;
import my.pvcloud.model.CityModel;

public class Service{

	public Page<Document> queryByPage(int page,int size){
		return Document.dao.queryByPage(page, size);
	}
	
	public Page<Document> queryByPageParams(int page,int size,String title){
		return Document.dao.queryByPageParams(page, size,title);
	}
	
	public Document queryById(int id){
		return Document.dao.queryById(id);
	}
	
	public boolean updateInfo(Document data){
		return Document.dao.updateInfo(data);
	}
	
	public boolean saveInfo(Document data){
		return Document.dao.saveInfo(data);
	}
	
	public int updateFlg(int id,int flg,int updateUser){
		return Document.dao.updateDocumentStatus(id, flg,updateUser);
	}
	
	public List<CityModel> queryCity(int flg,int pid){
		if(flg == 0){
			//省
			List<CityModel> list = new ArrayList<>();
			CityModel model = null;
			List<Province> provinces = Province.dao.queryAllProvince();
			for(Province data : provinces){
				model = new CityModel();
				model.setId(data.getInt("id"));
				model.setName(data.getStr("name"));
				model.setPid(data.getInt("pid"));
				list.add(model);
			}
			return list;
		}else if(flg == 1){
			//市
			List<CityModel> list = new ArrayList<>();
			CityModel model = null;
			List<City> datas = City.dao.queryAllCityByPid(pid);
			for(City data : datas){
				model = new CityModel();
				model.setId(data.getInt("id"));
				model.setName(data.getStr("name"));
				model.setPid(data.getInt("pid"));
				list.add(model);
			}
			return list;
		}else if(flg == 2){
			//区
			List<CityModel> list = new ArrayList<>();
			CityModel model = null;
			List<District> datas = District.dao.queryAllDistrictByPid(pid);
			for(District data : datas){
				model = new CityModel();
				model.setId(data.getInt("id"));
				model.setName(data.getStr("name"));
				model.setPid(data.getInt("pid"));
				list.add(model);
			}
			return list;
		}else{
			return null;
		}
	}
}