package my.pvcloud.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TestModel implements Serializable{

	private int id;
	private String name;
	private BigDecimal dist;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getDist() {
		return dist;
	}
	public void setDist(BigDecimal dist) {
		this.dist = dist;
	}
	
	
}
