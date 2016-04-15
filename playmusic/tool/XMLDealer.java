package cn.edu.zucc.playmusic.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;
import cn.edu.zucc.playmusic.model.Beat;


public class XMLDealer {

	public static List<Beat> Reader(InputStream in)
	{
		List<Beat> songs = null;
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
//			saxParser.setProperty("http://xml.org/sax/features/namespaces",false);
			XMLSongDetailHandler handler = new XMLSongDetailHandler();
			saxParser.parse(in, handler);
			songs = handler.getFallingDetail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return songs;
	}
	
	public static String writeXML(List<Beat> beats, Writer writer)
	{ 
	    XmlSerializer serializer = Xml.newSerializer();
	    String falls = "";
	    try { 
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true); 
	        serializer.startTag("", "beats"); 
	        
	        for (Beat beat : beats)
	        { 
	        	serializer.startTag("", "beat");
	            serializer.attribute("", "time", ""+beat.getMicroSecond());
	            String typeStr = "";
	            int[] types = beat.getTypes();
	            for(int i=0;i<types.length;i++)
	            {
	            	typeStr += types[i];
	            }
	            serializer.attribute("", "type", ""+typeStr);
	            serializer.endTag("", "beat");
	        }
	        serializer.endTag("", "beats"); 
	        serializer.endDocument(); 
	        return writer.toString();
	    } catch (Exception e) { 
	        e.printStackTrace();
	    } 
	    return null; 
	} 
	
	public static void writeBeatsXMl(String path,List<Beat> beats) throws Exception
	{
		File xmlFile = new File(path);

		FileOutputStream outStream = new FileOutputStream(xmlFile); 

		OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8"); 

		BufferedWriter writer = new BufferedWriter(outStreamWriter); 

		writeXML(beats, writer); 

		writer.flush(); 

		writer.close(); 
	}

	
}
