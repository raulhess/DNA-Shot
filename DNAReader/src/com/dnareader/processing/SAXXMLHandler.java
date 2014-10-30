package com.dnareader.processing;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Hit;
import com.dnareader.data.Hsp;

public class SAXXMLHandler extends DefaultHandler {

	private ArrayList<Hit> hits;
	private String tempVal;
	private Hit tempHit;
	private Hsp tempHsp;

	public SAXXMLHandler() {
		hits = new ArrayList<Hit>();
	}

	public ArrayList<Hit> getHits() {
		return hits;
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("Hit")) {
			tempHit = new Hit();
		} else if (qName.equalsIgnoreCase("Hsp")) {
			tempHsp = new Hsp();
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		// Hit

		if (qName.equalsIgnoreCase("Hit")) {
			// add it to the list
			hits.add(tempHit);
		} else if (qName.equalsIgnoreCase("Hit_id")) {
			tempHit.setHit_id(tempVal);
		} else if (qName.equalsIgnoreCase("Hit_def")) {
			tempHit.setHit_def(tempVal);
		} else if (qName.equalsIgnoreCase("Hit_len")) {
			tempHit.setHit_len(tempVal);
		}

		// Hsp

		else if (qName.equalsIgnoreCase("Hsp")) {
			// add it to the list
			tempHit.getHsps().add(tempHsp);
		} else if (qName.equalsIgnoreCase("hsp_evalue")) {
			tempHsp.setHsp_evalue(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_query_from")) {
			tempHsp.setHsp_query_from(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_query_to")) {
			tempHsp.setHsp_query_to(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_hit_from")) {
			Log.d(MainActivity.TAG, "reached hit from [" + tempVal + "]");
			tempHsp.setHsp_hit_from(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_hit_to")) {
			tempHsp.setHsp_hit_to(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_align_len")) {
			tempHsp.setHsp_align_len(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_qseq")) {
			tempHsp.setHsp_qseq(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_hseq")) {
			tempHsp.setHsp_hseq(tempVal);
		} else if (qName.equalsIgnoreCase("hsp_midline")) {
			tempHsp.setHsp_midline(tempVal);
		}else if (qName.equalsIgnoreCase("hsp_gaps")) {
			tempHsp.setHsp_gaps(tempVal);
		}

	}
}
