package com.tyrfing.games.id17.crestgen;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CrestAtlasPrinter {
	
	private final List<Crest> crests;
	
	public CrestAtlasPrinter(List<Crest> crests) {
		this.crests = crests;
	}
	
	public void print(String fileName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Atlas");
			rootElement.setAttribute("texture", "SIGILS1");
			rootElement.setAttribute("name", "SIGILS1");
			doc.appendChild(rootElement);

			for (Crest crest : crests) {
				printCrest(crest, doc, rootElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);

			System.out.println("Atlas saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	private void printCrest(Crest crest, Document doc, Element rootElement) {
		Element element = doc.createElement("Region");
		element.setAttribute("name", crest.name);
		element.setAttribute("x", String.valueOf(crest.region.x));
		element.setAttribute("y", String.valueOf(crest.region.y));
		element.setAttribute("w", String.valueOf(crest.region.width));
		element.setAttribute("h", String.valueOf(crest.region.height));
		rootElement.appendChild(element);
	}
}
