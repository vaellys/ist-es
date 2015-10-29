package com.ist.common.es.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.core.io.Resource;
import org.xml.sax.XMLFilter;

public class XMLProperties {
	/**
	* Logger for this class
	*/
	private static final Logger logger = Logger.getLogger(XMLProperties.class);
	
	
	
	private Resource configLocation = null;
	protected File file;
	protected Document doc;
	private String flag = "#";

	private Map<String, String> propertyCache = new HashMap<String, String>();

	public XMLProperties(Resource _configLocation) {
		try {
			InputStream is = _configLocation.getInputStream();
			SAXBuilder builder = new SAXBuilder();

			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter((XMLFilter) format);

			this.doc = builder.build(is);
		} catch (Exception e) {
			System.err.println("在XMLProperties类中建立XML剖析器失败。File=" + this.file);
			logger.error("在XMLProperties类中建立XML剖析器失败", e);

		}
	}

	public String getProperty(String name) {
		if (this.propertyCache.containsKey(name)) {
			return (String) this.propertyCache.get(name);
		}

		String[] propName = parsePropertyName(name);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			element = element.getChild(propName[i]);
			if (element == null) {
				return null;
			}

		}

		String value = element.getText();
		if ("".equals(value)) {
			return null;
		}

		value = value.trim();
		this.propertyCache.put(name, value);
		return value;
	}

	@SuppressWarnings("rawtypes")
    public String[] getChildrenProperties(String parent) {
		String[] propName = parsePropertyName(parent);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			element = element.getChild(propName[i]);
			if (element == null) {
				return new String[0];
			}
		}

		List children = element.getChildren();
		int childCount = children.size();
		String[] childrenNames = new String[childCount];
		for (int i = 0; i < childCount; i++) {
			childrenNames[i] = ((Element) children.get(i)).getName();
		}
		return childrenNames;
	}

	public void setProperty(String name, String value) {
		this.propertyCache.put(name, value);

		String[] propName = parsePropertyName(name);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			if (element.getChild(propName[i]) == null) {
				element.addContent(new Element(propName[i]));
			}
			element = element.getChild(propName[i]);
		}

		element.setText(value);

		saveProperties();
	}

	public void deleteProperty(String name) {
		String[] propName = parsePropertyName(name);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.getChild(propName[i]);

			if (element == null) {
				return;
			}
		}

		element.removeChild(propName[(propName.length - 1)]);

		saveProperties();
	}

	private synchronized void saveProperties() {
		OutputStream out = null;
		boolean error = false;

		File tempFile = null;
		try {
			tempFile = new File(this.file.getParentFile(), this.file.getName() + ".tmp");

			Format format = Format.getCompactFormat();
			format.setIndent("    ");
			XMLOutputter outputter = new XMLOutputter(format);
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			outputter.output(this.doc, out);
		} catch (Exception e) {
			logger.error("", e);
			error = true;
			try {
				out.close();
			} catch (Exception e2) {
				error = true;
				logger.error("", e2);
			}
			
		} finally {
			try {
				out.close();
			} catch (Exception e) {

				error = true;
				logger.error("", e);
			}
		}

		if (!error) {
			this.file.delete();

			tempFile.renameTo(this.file);
		}
	}

	private String[] parsePropertyName(String name) {
		int size = 1;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '.') {
				size++;
			}
		}
		String[] propName = new String[size];

		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			propName[i] = tokenizer.nextToken();
			i++;
		}
		return propName;
	}

	public Resource getConfigLocation() {
		return this.configLocation;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	@SuppressWarnings("rawtypes")
    public int getChildrenCount(String path) {
		Element element = getElement(path);
		List list = element.getChildren();
		return list.isEmpty() ? 0 : list.size();
	}

	@SuppressWarnings("rawtypes")
    private void getElementList(List<Element> nodeList, Element root, String elementName) {
		if (root.getName().equals(elementName)) {
			nodeList.add(root);
		} else {
			List elements = root.getChildren();
			if (elements != null)
				for (Iterator it = elements.iterator(); it.hasNext();) {
					Object iter = it.next();
					if ((iter instanceof Element))
						getElementList(nodeList, (Element) iter, elementName);
				}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
    private Element getElement(String path) {
		String[] propName = parsePropertyName(path);
		Element element = this.doc.getRootElement();

		for (int i = 0; i < propName.length; i++) {
			if (propName[i].contains(this.flag)) {
				String elementName = propName[i].split(this.flag)[0];
				int elementIndex = 0;
				try {
					elementIndex = Integer.parseInt(propName[i].split(this.flag)[1]);
				} catch (NumberFormatException e) {
					return null;
				}
				List list = new ArrayList();
				getElementList(list, element, elementName);
				if ((list.isEmpty()) || (list.size() <= elementIndex)) {
					return null;
				}
				element = (Element) list.get(elementIndex);
			} else {
				element = element.getChild(propName[i]);
				if (element == null) {
					return null;
				}
			}
		}
		return element;
	}

	public String getAttributeValue(String path, String name) {
		Element element = getElement(path);
		if (element == null) {
			return null;
		}

		String value = element.getAttributeValue(name);
		if ((value == null) || ("".equals(value))) {
			return null;
		}

		value = value.trim();

		return value;
	}

	public String getArrayProperty(String name) {
		if (this.propertyCache.containsKey(name)) {
			return (String) this.propertyCache.get(name);
		}

		Element element = getElement(name);

		if (element == null) {
			return null;
		}

		String value = element.getText();
		if ((value == null) || ("".equals(value))) {
			return null;
		}

		value = value.trim();
		this.propertyCache.put(name, value);
		return value;
	}
}
