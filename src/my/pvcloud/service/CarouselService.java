package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Carousel;

public class CarouselService {

	public Page<Carousel> queryByPage(int page,int size){
		return Carousel.dao.queryByPage(page, size);
	}
	
	public Carousel queryById(int teaId){
		return Carousel.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Carousel tea){
		return Carousel.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(Carousel tea){
		return Carousel.dao.saveInfo(tea);
	}
	
	public int updateFlg(int id,int flg){
		return Carousel.dao.updateCarouselStatus(id, flg);
	}
}
