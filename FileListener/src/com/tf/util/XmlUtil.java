package com.tf.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmlUtil {

	private synchronized static Document getConfig()
			throws ParserConfigurationException, SAXException, IOException {
		File f = new File(System.getProperty("user.dir") + "\\DBConf.xml");//XML配置文件路径
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(f);
		return doc;
	}

	private static void insertNode(String nodeName, String nodeValue) {
		File f = new File(System.getProperty("user.dir") + "\\DBConf.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			Element tempElem = doc.createElement(nodeName);
			Text child = doc.createTextNode(nodeValue);
			tempElem.appendChild(child);
			doc.getElementsByTagName("conf").item(0).appendChild(tempElem);
			DOMSource source = new DOMSource(doc);
			StreamResult Streamres = new StreamResult(new FileOutputStream(f));
			TransformerFactory.newInstance().newTransformer()
					.transform(source, Streamres);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateNode(String nodeName, String value)
			throws Exception {
		File f = new File(System.getProperty("user.dir") + "\\DBConf.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(f);
		Node tempElem = doc.getElementsByTagName(nodeName).item(0);
		tempElem.getFirstChild().setNodeValue(value);
		doc.getElementsByTagName("conf").item(0).appendChild(tempElem);
		DOMSource source = new DOMSource(doc);
		StreamResult res = new StreamResult(new FileOutputStream(f));
		TransformerFactory.newInstance().newTransformer()
				.transform(source, res);

	}

	public static void saveOrUpdate(String nodeName, String value) {
		try {
			updateNode(nodeName, value);
		} catch (NullPointerException e) {
			insertNode(nodeName, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getNodeValue(String nodeName)
			throws NullPointerException {
		try {
			Document doc = getConfig();
			NodeList list = doc.getElementsByTagName(nodeName);
			return list.item(0).getFirstChild().getNodeValue();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
