package com.dnashot.data;

import java.util.ArrayList;
import java.util.List;

public class Hit {

	private long resultId;
	private long hit_id;
	private String hit_def;
	private String hit_len;
	private List<Hsp> hsps;

	public Hit() {
		hsps = new ArrayList<Hsp>();
	}

	public long getResultId() {
		return resultId;
	}

	public void setResultId(long resultId) {
		this.resultId = resultId;
	}

	public String getHit_id() {
		return hit_id + "";
	}
	
	public long getLongHit_id() {
		return hit_id;
	}

	public void setHit_id(long hit_id) {
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

	public List<Hsp> getHsps() {
		return hsps;
	}

	public void setHsps(List<Hsp> hsps) {
		this.hsps = hsps;
	}
	
	@Override
	public String toString() {
		return "Hit[" + resultId + "]: id(" + hit_id + "), " + " hit_def(" + hit_def + "), hit_len(" + hit_len + ");";
	}

}
