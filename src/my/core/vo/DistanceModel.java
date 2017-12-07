package my.core.vo;

import java.math.BigDecimal;

import my.core.model.User;

public class DistanceModel implements Comparable<DistanceModel>{

	private int id;
	private BigDecimal distance;
	private TeaStoreListVO vo;
	
	public TeaStoreListVO getVo() {
		return vo;
	}
	public void setVo(TeaStoreListVO vo) {
		this.vo = vo;
	}
	public int getId() {
		return id;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(DistanceModel o){
		return this.getDistance().compareTo(o.getDistance());
	}
}
