package com.dnareader.data;

import java.io.Serializable;

public class Hsp implements Serializable{
	private static final long serialVersionUID = -935674131813014970L;
	
	private long hitId;
	private String hsp_evalue;
	private String hsp_query_from;
	private String hsp_query_to;
	private String hsp_hit_from;
	private String hsp_hit_to;
	private String hsp_align_len;
	private String hsp_qseq;
	private String hsp_hseq;
	private String hsp_midline;
	private String hsp_gaps;

	public String getHsp_evalue() {
		return hsp_evalue;
	}

	public void setHsp_evalue(String hsp_evalue) {
		this.hsp_evalue = hsp_evalue;
	}

	public String getHsp_query_from() {
		return hsp_query_from;
	}

	public void setHsp_query_from(String hsp_query_from) {
		this.hsp_query_from = hsp_query_from;
	}

	public String getHsp_query_to() {
		return hsp_query_to;
	}

	public void setHsp_query_to(String hsp_query_to) {
		this.hsp_query_to = hsp_query_to;
	}

	public String getHsp_hit_from() {
		return hsp_hit_from;
	}

	public void setHsp_hit_from(String hsp_hit_from) {
		this.hsp_hit_from = hsp_hit_from;
	}

	public String getHsp_hit_to() {
		return hsp_hit_to;
	}

	public void setHsp_hit_to(String hsp_hit_to) {
		this.hsp_hit_to = hsp_hit_to;
	}

	public String getHsp_align_len() {
		return hsp_align_len;
	}

	public void setHsp_align_len(String hsp_align_len) {
		this.hsp_align_len = hsp_align_len;
	}

	public String getHsp_qseq() {
		return hsp_qseq;
	}

	public void setHsp_qseq(String hsp_qseq) {
		this.hsp_qseq = hsp_qseq;
	}

	public String getHsp_hseq() {
		return hsp_hseq;
	}

	public void setHsp_hseq(String hsp_hseq) {
		this.hsp_hseq = hsp_hseq;
	}

	public String getHsp_midline() {
		return hsp_midline;
	}

	public void setHsp_midline(String hsp_midline) {
		this.hsp_midline = hsp_midline;
	}

	public String getHsp_gaps() {
		return hsp_gaps;
	}

	public void setHsp_gaps(String hsp_gaps) {
		this.hsp_gaps = hsp_gaps;
	}

	public long getHitId() {
		return hitId;
	}

	public void setHitId(long hitId) {
		this.hitId = hitId;
	}
	
	

}
