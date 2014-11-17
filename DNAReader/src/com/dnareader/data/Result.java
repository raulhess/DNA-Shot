package com.dnareader.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

public class Result implements Serializable{
	public static final int UNPROCESSED = 0;
	public static final int OCR_STARTED = 1;
	public static final int OCR_FINISHED = 2;
	public static final int BLAST_STARTED =3;
	public static final int BLAST_FINISHED = 4;
	public static final int PREPROCESSING_STARTED = 5;
	public static final int PREPROCESSING_FINISHED = 6;
	public static final int DONE = 10;
	public static final int ERROR = -1;
	
	private static final long serialVersionUID = 982757938298536428L;
	private long id;
	private Bitmap thumbnail;
	private Bitmap image;
	private Bitmap PreProcessedimage;
	private boolean checked;
	private Date date;
	private String content;
	private String ocrText;
	private String blastXML;		
	private int state = 0;
	private String rid; // Blast request id
	private List<Hit> hits;
	
	public String getBlastXML() {
		return blastXML;
	}

	public void setBlastXML(String blastXML) {
		this.blastXML = blastXML;
	}

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
		return id + "";
	}
	
	public long getLongId() {
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap image) {
		this.thumbnail = image;
	}
	
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
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

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public Bitmap getPreProcessedimage() {
		return PreProcessedimage;
	}

	public void setPreProcessedimage(Bitmap preProcessedimage) {
		PreProcessedimage = preProcessedimage;
	}

	public List<Hit> getHits() {
		return hits;
	}

	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
	
	
	
}
