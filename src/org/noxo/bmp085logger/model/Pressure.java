package org.noxo.bmp085logger.model;

public class Pressure {
	
	int id;
	double value;
	long utc;
	String formattedTime;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public long getUtc() {
		return utc;
	}
	
	public void setUtc(long utc) {
		this.utc = utc;
	}

	public String getFormattedTime() {
		return formattedTime;
	}

	public void setFormattedTime(String formattedTime) {
		this.formattedTime = formattedTime;
	}

	
}
