package com.dnareader.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.biojava3.core.sequence.io.util.IOUtils;
import org.biojava3.ws.alignment.qblast.BlastOutputParameterEnum;
import org.biojava3.ws.alignment.qblast.BlastProgramEnum;
import org.biojava3.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava3.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava3.ws.alignment.qblast.NCBIQBlastService;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Hit;

public class Blast {

	NCBIQBlastService service;
	NCBIQBlastAlignmentProperties props;
	NCBIQBlastOutputProperties outputProps;

	public Blast() {
		service = new NCBIQBlastService();

		// set alignment options
		props = new NCBIQBlastAlignmentProperties();

		props.setBlastProgram(BlastProgramEnum.blastn);
		props.setBlastDatabase("nr");

		// set output options
		outputProps = new NCBIQBlastOutputProperties();
		outputProps.setAlignmentNumber(10);
		outputProps.setDescriptionNumber(10);
		
	}

	public String startBlast(String sequence) throws Exception {

		String rid = null; // blast request ID
		
		// send blast request and save request id
		rid = service.sendAlignmentRequest(sequence, props);
		return rid;
	}

	public String checkBlast(String rid) throws IOException, Exception {

		String xml = "";
		BufferedReader reader = null;

		if (service.isReady(rid)) {

			InputStream in = service.getAlignmentResults(rid, outputProps);
			reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				xml += line + System.getProperty("line.separator");
			}

			IOUtils.close(reader);
			service.sendDeleteRequest(rid);
		} else {
			return null;
		}

		return xml;

	}

	public static ArrayList<Hit> parseBlastXML(String xml) {
		ArrayList<Hit> hits = null;		
		try {
			SAXParserFactory SAXfactory = SAXParserFactory.newInstance();
			SAXParser SAXParserObj = SAXfactory.newSAXParser();
			XMLReader xmlReader = SAXParserObj.getXMLReader();
			SAXXMLHandler handler = new SAXXMLHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new StringReader(xml)));
			
			hits = handler.getHits();
			if(hits.size() > 10)
				Log.d(MainActivity.TAG, "Cropping hits: " + hits.size());
				return  new ArrayList<>(hits.subList(0, 10));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hits;
	}

}
