package com.dnareader.data;

import java.util.ArrayList;

public class Hit {

	private int result_id;
	private String hit_id;
	private String hit_def;
	private String hit_len;
	private ArrayList<Hsp> hsps;

	public Hit() {
		hsps = new ArrayList<Hsp>();
	}

	public int getResult_id() {
		return result_id;
	}

	public void setResult_id(int result_id) {
		this.result_id = result_id;
	}

	public String getHit_id() {
		return hit_id;
	}

	public void setHit_id(String hit_id) {
		this.hit_id = hit_id;
	}

	public String getHit_def() {
		return hit_def;
	}

	public void setHit_def(String hit_def) {
		this.hit_def = hit_def;
	}

	public String getHit_len() {
		return hit_len;
	}

	public void setHit_len(String hit_len) {
		this.hit_len = hit_len;
	}

	public ArrayList<Hsp> getHsps() {
		return hsps;
	}

	public void setHsps(ArrayList<Hsp> hsps) {
		this.hsps = hsps;
	}

}
