package com.tyrfing.games.id17.mapgen.printers;

import java.io.File;
import java.util.Collections;

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

import com.tyrfing.games.id17.mapgen.Map;
import com.tyrfing.games.id17.mapgen.objects.MapObject;
import com.tyrfing.games.id17.mapgen.objects.MapObjectType;
import com.tyrfing.games.id17.mapgen.zones.Barony;

public class BaronyPrinter {
	
	private Map map;
	
	public BaronyPrinter(Map map) {
		this.map = map;
	}
	
	public void print(String fileName){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Baronies");
			doc.appendChild(rootElement);

			for (int i = 0; i < map.allBaronies.size(); ++i) {
				printBarony(map.allBaronies.get(i), doc, rootElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);

			System.out.println("Barony File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	private void printBarony(Barony barony, Document doc, Element parent) {
		Element b = doc.createElement("Barony");
		parent.appendChild(b);
		b.setAttribute("name", barony.name);
		long color = barony.printColor.getRGB();
		String colorStr = String.valueOf(color);
		b.setAttribute("color", colorStr);
		b.setAttribute("x", String.valueOf(barony.x));
		b.setAttribute("y", String.valueOf(barony.y));
		b.setAttribute("w", String.valueOf(barony.width));
		b.setAttribute("h", String.valueOf(barony.height));
		b.setAttribute("countSubHoldings", String.valueOf(barony.mapObjects.size()));
		
		Collections.sort(barony.mapObjects);

		for (int i = 0; i < barony.mapObjects.size(); ++i) {
			Element holding = doc.createElement("Holding");
			MapObject o = barony.mapObjects.get(i);
			if (o.type != MapObjectType.CASTLE) {
				holding.setAttribute("name", o.name);
				holding.setAttribute("type", o.type.getTypeName());
				holding.setAttribute("objectNo", String.valueOf(i));
				b.appendChild(holding);
			}
		}
		
		Element army = doc.createElement("Levy");
		b.appendChild(army);
		
		Element regiment = doc.createElement("Regiment");
		army.appendChild(regiment);
		regiment.setAttribute("type", "Guardians");
		regiment.setAttribute("amount", "100");
		regiment.setAttribute("maxAmount", "100");
		regiment.setAttribute("position", "0");
		
		army = doc.createElement("Garrison");
		b.appendChild(army);
		
		regiment = doc.createElement("Regiment");
		army.appendChild(regiment);
		regiment.setAttribute("type", "Guardians");
		regiment.setAttribute("amount", "100");
		regiment.setAttribute("maxAmount", "100");
		regiment.setAttribute("position", "2");
		
		regiment = doc.createElement("Regiment");
		army.appendChild(regiment);
		regiment.setAttribute("type", "Walls");
		regiment.setAttribute("amount", "1000");
		regiment.setAttribute("maxAmount", "1000");
		regiment.setAttribute("position", "0");
		
		regiment = doc.createElement("Regiment");
		army.appendChild(regiment);
		regiment.setAttribute("type", "Walls");
		regiment.setAttribute("amount", "1000");
		regiment.setAttribute("maxAmount", "1000");
		regiment.setAttribute("position", "1");
		
		
	}
}


