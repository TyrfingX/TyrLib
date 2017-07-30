package com.tyrfing.games.id17.mapgen.printers;

import java.io.File;

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

import com.tyrfing.games.id17.mapgen.House;
import com.tyrfing.games.id17.mapgen.Map;
import com.tyrfing.games.id17.mapgen.objects.MapObject;


public class HousePrinter {
	
	private Map map;

	public HousePrinter(Map map) {
		this.map = map;
	}

	public void print(String fileName) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Houses");
			doc.appendChild(rootElement);

			for (int i = 0; i < map.allHeadHouses.size(); ++i) {
				printHouse(map.allHeadHouses.get(i), doc, rootElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);

			System.out.println("House File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	private void printHouse(House house, Document doc, Element parent) {
		Element h = doc.createElement("House");
		parent.appendChild(h);
		h.setAttribute("name", house.name);
		
		for (int i = 0; i < house.holdings.size(); ++i) {
			Element holding = doc.createElement("Holding");
			MapObject o = house.holdings.get(i);
			holding.setAttribute("name", o.area.barony.name + "/" + o.name);
			h.appendChild(holding);
		}
		
		for (int i = 0; i < house.subHouses.size(); ++i) {
			printHouse(house.subHouses.get(i), doc, h);
		}
		
	}
}
