package com.dnareader.processing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.biojava3.core.sequence.io.util.IOUtils;
import org.biojava3.ws.alignment.qblast.BlastProgramEnum;
import org.biojava3.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava3.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava3.ws.alignment.qblast.NCBIQBlastService;

public class Blast {

	
	public String doBlast(String sequence) {

		NCBIQBlastService service = new NCBIQBlastService();

		// set alignment options
		NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
		props.setBlastProgram(BlastProgramEnum.blastn);
		props.setBlastDatabase("nr");
		//props.setAlignmentOption(ENTREZ_QUERY,"\"serum albumin\"[Protein name] AND mammals[Organism]");

		// set output options
		NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();
		// in this example we use default values set by constructor (XML format,
		// pairwise alignment, 100 descriptions and alignments)

		// Example of two possible ways of setting output options
		outputProps.setAlignmentNumber(5);
		outputProps.setDescriptionNumber(5);
		// outputProps.setOutputOption(BlastOutputParameterEnum.ALIGNMENTS,
		// "200");

		String rid = null; // blast request ID		
		BufferedReader reader = null;
		String xml = "";
		try {
			// send blast request and save request id
			rid = service.sendAlignmentRequest(sequence, props);

			// wait until results become available. Alternatively, one can do
			// other computations/send other alignment requests
			
			while (!service.isReady(rid)) {
				System.out.println("Waiting for results. Sleeping for 8 seconds");
				Thread.sleep(8000);
			}

			// read results when they are ready
			InputStream in = service.getAlignmentResults(rid, outputProps);
			reader = new BufferedReader(new InputStreamReader(in));			

			String line;
			while ((line = reader.readLine()) != null) {
				xml += line + System.getProperty("line.separator");
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			// clean up	
			IOUtils.close(reader);
			// delete given alignment results from blast server (optional
			// operation)
			service.sendDeleteRequest(rid);

		}
		return xml;
	}
	
	
	public static String parseBlastXML(String xml){
		
		SAXParserFactory SAXfactory = SAXParserFactory.newInstance();
        try {
            SAXParser SAXParserObj = SAXfactory.newSAXParser();
            
           

        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return "";
	}
	
}
