package my.core.vo;

import java.math.BigDecimal;

public class XcxDistanceModel implements Comparable<XcxDistanceModel>{

	private int id;
	private BigDecimal distance;
	private XcxTeaStoreListVO vo;
	
	public int getId() {
		return id;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public XcxTeaStoreListVO getVo() {
		return vo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public void setVo(XcxTeaStoreListVO vo) {
		this.vo = vo;
	}

	@Override
	public int compareTo(XcxDistanceModel o){
		return this.getDistance().compareTo(o.getDistance());
	}
}
