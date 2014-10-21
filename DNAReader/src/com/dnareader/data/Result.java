package com.dnareader.data;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable{
	public static final int UNPROCESSED = 0;
	public static final int OCR_PROCESSED = 1;
	public static final int BLAST_PROCESSED = 2;
	public static final int DONE = 10;
	public static final int ERROR = -1;
	
	private static final long serialVersionUID = 982757938298536428L;
	private String id;
	private byte[] thumbnail;
	private byte[] image;
	private boolean checked;
	private Date date;
	private String content;
	private String ocrText;
	
	private int state = 0;

	public int getState() {
		return state;
	}

	public String getOcrText() {
		return ocrText;
	}

	public void setOcrText(String ocrText) {
		this.ocrText = ocrText;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] image) {
		this.thumbnail = image;
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
