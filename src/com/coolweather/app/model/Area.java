package com.coolweather.app.model;

public class Area {
	private int id;
	private String weaid;
	private String citynm;

	public void setId(int id) {
		this.id = id;
	}

	public void setWeaid(String weaid) {
		this.weaid = weaid;
	}

	public void setCitynm(String citynm) {
		this.citynm = citynm;
	}

	public int getId() {
		return id;
	}

	public String getWeaid() {
		return weaid;
	}

	public String getCitynm() {
		return citynm;
	}
}
