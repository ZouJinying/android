package cn.edu.zucc.playmusic.tool;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler; 

import cn.edu.zucc.playmusic.model.Beat;


public class XMLSongDetailHandler extends DefaultHandler{
	private List<Beat> details = null;
	private Beat beat; 
	private String tagName = null;


	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		details = new LinkedList<Beat>();
	}
	
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
//		if(tagName != null)
//		{
//			String chs = new String(ch,start,length);
//			
//			if(tagName.equalsIgnoreCase("time"))
//			{
//				detail.setMicroSecond(Long.parseLong(chs));
//			}
//			else if(tagName.equalsIgnoreCase("falling"))
//			{
//				
//				int[] falling = new int[chs.length()];
//				for(int i=0;i<falling.length;i++)
//				{
//					int temp = Integer.parseInt(""+chs.charAt(i));
//					if(temp>=0 && temp<3)
//					{
//						falling[i] = temp;
//					}
//				}
//				detail.setTypes(falling);
//			}
//
//		}
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
//		super.startElement(uri, localName, qName, attributes);
		if(localName.equals("beat"))
		{ 
			beat = new Beat(); 
			beat.setMicroSecond(Long.parseLong(attributes.getValue("time")));
			String chs = attributes.getValue("type");
			int[] falling = new int[chs.length()];
			for(int i=0;i<falling.length;i++)
			{
				int temp = Integer.parseInt(""+chs.charAt(i));

				falling[i] = temp;

			}
			beat.setTypes(falling);
		}
		this.tagName = localName;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		
		if(localName.equals("beat"))
		{
			details.add(beat);
			beat = null;
		}
		tagName = null;
	}

	public List<Beat> getFallingDetail() {
		return details;
	}
	
}
