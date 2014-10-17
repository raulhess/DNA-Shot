package com.dnareader.data;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable{
	private static final long serialVersionUID = 982757938298536428L;
	private String id;
	private byte[] image;
	private boolean checked;
	private Date date;
	private String content;
	
	private int state = 0;

	public int getState() {
		return state;
	}

	public void setNotSent() {
		this.state = 0;
	}
	
	public void setWaiting() {
		this.state = 1;
	}
	
	public void setDone() {
		this.state = 2;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
