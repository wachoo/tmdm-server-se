package com.amalto.webapp.widget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONException;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSGetItem;
import com.amalto.core.webservice.WSItem;
import com.amalto.core.webservice.WSItemPK;

/**
 * @author starkey
 * 
 * 
 */
public class ItemTreeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String jsonTree = "";

		String cluster = req.getParameter("cluster");
		String concept = req.getParameter("concept");
		String keys = req.getParameter("keys");
		
		if (cluster != null && !cluster.equals("undefined")
		  &&concept != null && !concept.equals("undefined")
		  &&keys != null && !keys.equals("undefined")) {
			try {
				String clusterName = cluster;
				String conceptName = concept;
				String[] keysArray = keys.split("\\.");

				WSItem wsItem = Util.getPort().getItem(
						new WSGetItem(new WSItemPK(new WSDataClusterPK(clusterName), conceptName, keysArray)));

				Document doc = parse(new ByteArrayInputStream(wsItem.getContent().getBytes()));
				//JSONObject json = new JSONObject();
				JSONArray jsonChildren = new JSONArray();
				visitElementList(doc.getRootElement(), jsonChildren);
				//json.put("text", conceptName);
				//json.put("leaf", false);
				//json.put("children", jsonChildren);

				// jsonTree=json.toString();
				jsonTree = jsonChildren.toString();
			} catch (XtentisWebappException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		PrintWriter out = resp.getWriter();
		out.println(jsonTree);
		out.close();

	}

	public static Document parse(String fileName) throws DocumentException {
		InputStream is = null;
		is = ItemTreeServlet.class.getResourceAsStream("/" + fileName);
		Document document = parse(is);
		return document;
	}

	public static Document parse(InputStream in) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(in);
		return document;
	}

	public static void visitElementList(Element element, JSONArray jsonChildren)
			throws JSONException {

		List elements = element.elements();
		if (elements.size() == 0) {
			// no children
			String name = element.getName();
			String value = element.getTextTrim();

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("elemText", name);
			jsonObj.put("elemValue", value);
			jsonObj.put("uiProvider", "col");
			jsonObj.put("leaf", true);
			jsonObj.put("draggable", true);
			jsonObj.put("allowDrop", true);

			jsonChildren.put(jsonObj);
		} else {
			// has children
			for (Iterator it = elements.iterator(); it.hasNext();) {
				Element elem = (Element) it.next();

				if (hasChildElements(elem)) {
					String name = elem.getName();
					String value = elem.getTextTrim();

					JSONObject jsonObj = new JSONObject();
					JSONArray subJsonChildren = new JSONArray();
					jsonObj.put("elemText", name);
					jsonObj.put("uiProvider", "col");
					jsonObj.put("leaf", false);
					jsonObj.put("children", subJsonChildren);
					jsonObj.put("draggable", true);
					jsonObj.put("allowDrop", true);

					jsonChildren.put(jsonObj);
					visitElementList(elem, subJsonChildren);
				} else {
					visitElementList(elem, jsonChildren);
				}

			}
		}
	}

	public static boolean hasChildElements(Element element) {

		return element.elementIterator().hasNext();

	}

}
