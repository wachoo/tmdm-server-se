// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.v3.itemsbrowser.bean.View;

/**
 * 
 * @author asaintguilhem
 * 
 *serve data to a json grid
 */

public class ItemsRemotePaging extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ItemsRemotePaging.class);

    private static final long serialVersionUID = 1L;

    private static Pattern highlightLeft = Pattern.compile("\\s*__h");

    private static Pattern highlightRight = Pattern.compile("h__\\s*");

    private static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");

    private static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");

    private static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");

    public ItemsRemotePaging() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (LOG.isTraceEnabled())
            LOG.trace("Remote paging for items");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        Configuration config = null;
        try {
            if (LOG.isTraceEnabled())
                LOG.trace("doPost() session items " + request.getSession().getId());
            config = (Configuration) request.getSession().getAttribute("configuration");
        } catch (Exception e1) {
            LOG.error(e1.getMessage(), e1);
        }
        if (config.getCluster() == null || config.getCluster().trim().length() == 0) {
            PrintWriter writer = response.getWriter();
            writer.write("Data Container can't be empty!");
            writer.close();
            throw new ServletException("Data Container can't be empty!");
        }
        String start = request.getParameter("start");
        String limit = request.getParameter("limit");
        String sortCol = null;
        if (request.getParameter("sort") != null && request.getParameter("sort").length() > 0
                && viewFieldValidate(request.getParameter("viewName"), request.getParameter("sort")))
            sortCol = request.getParameter("sort");
        String sortDir = null;
        if (sortCol != null && request.getParameter("dir") != null && request.getParameter("dir").length() > 0) {
            if (request.getParameter("dir").toUpperCase().equals("ASC"))
                sortDir = "ascending";
            if (request.getParameter("dir").toUpperCase().equals("DESC"))
                sortDir = "descending";
        }

        String viewName = request.getParameter("viewName");
        String criteria = request.getParameter("criteria");
        String concept = viewName.replaceAll("Browse_items_", "").replaceAll("#.*", "");

        if (LOG.isDebugEnabled()) {
            LOG.debug("doPost() \n" + "start : " + start + "\n" + "limit : " + limit + "\n" + "criteria : " + criteria + "\n"
                    + "viewName : " + viewName + "\n" + "sortCol : " + (sortCol == null ? "" : sortCol) + "\n" + "sortDir : "
                    + (sortDir == null ? "" : sortDir));
        }

        JSONObject json = new JSONObject();

        try {
            int max = 50;
            if (limit != null && limit.length() > 0)
                max = Integer.parseInt(limit);
            int skip = 0;
            if (limit != null && limit.length() > 0)
                skip = Integer.parseInt(start);
            View view = new View(viewName);

            if (LOG.isDebugEnabled())
                LOG.debug("doPost() case : new remote items call");

            WSWhereItem wi = Util.buildWhereItems(criteria);

            if (LOG.isTraceEnabled())
                LOG.trace("doPost() starting to search");

            int totalSize = 0;

            String[] results = Util.getPort().viewSearch(
                    new WSViewSearch(new WSDataClusterPK(config.getCluster()), new WSViewPK(view.getViewPK()), wi, -1, skip, max,
                            sortCol, sortDir)).getStrings();
            if (LOG.isTraceEnabled())
                LOG.trace("doPost() end of search");

            ArrayList<String[]> itemsBrowserContent = new ArrayList<String[]>();

            for (int i = 0; i < results.length; i++) {
                // aiming modify
                // yin guo fix bug 0010867. the totalCountOnfirstRow is true.
                if (i == 0) {
                    try {
                        // Qizx doesn't wrap the count in a XML element, so try to parse it
                        totalSize = Integer.parseInt(results[i]);
                    } catch (NumberFormatException e) {
                        totalSize = Integer.parseInt(Util.parse(results[i]).getDocumentElement().getTextContent());
                    }
                    continue;
                }

                // modified by lzhang, same realization with itemsbrowser2
                // if it has more than one nodes with same name, retrieve by its order
                HashMap<String, Integer> countMap = new HashMap<String, Integer>();
                Document docXml = Util.parse(results[i]);
                String path = null;

                // end
                String[] fields = new String[view.getViewables().length];

                for (int index = 0; index < view.getViewables().length; index++) {
                    path = view.getViewables()[index];
                    String leafPath = path.substring(path.lastIndexOf('/') + 1); //$NON-NLS-1$ 
                    NodeList nodes = Util.getNodeList(docXml, leafPath);
                    if (nodes.getLength() > 1) {
                        // result has same name nodes
                        if (countMap.containsKey(leafPath)) {
                            int count = Integer.valueOf(countMap.get(leafPath).toString());
                            fields[index] = nodes.item(count).getTextContent();
                            countMap.put(leafPath, count + 1);
                        } else {
                            fields[index] = nodes.item(0).getTextContent();
                            countMap.put(leafPath, 1);
                        }
                    } else if (nodes.getLength() == 1) {
                        fields[index] = nodes.item(0).getTextContent();
                    }
                }
                itemsBrowserContent.add(fields);
            }

            int totalCount = totalSize;

            if (LOG.isDebugEnabled())
                LOG.debug("doPost() Total result = " + totalCount);
            /**
             * sort the collections
             */
            int col = Util.getSortCol(view.getViewables(), sortCol);
            if (Util.checkDigist(itemsBrowserContent, col)) {
                Util.sortCollections(itemsBrowserContent, col, sortDir);
            }

            // get part we are interested
            if (max > totalCount)
                max = totalCount;
            if (max > (totalCount - skip)) {
                max = totalCount - skip;
            }
            if (LOG.isDebugEnabled())
                LOG.debug("doPost() starting to build json object");
            json.put("TotalCount", totalCount);
            ArrayList<JSONObject> rows = new ArrayList<JSONObject>();

            for (int i = skip; i < (max + skip); i++) {
                int index = i - skip;
                if (index > itemsBrowserContent.size() - 1)
                    break;
                JSONObject fields = new JSONObject();
                for (int j = 0; j < itemsBrowserContent.get(index).length; j++) {
                    fields.put("/" + view.getViewables()[j], itemsBrowserContent.get(index)[j]);
                }
                rows.add(fields);
            }
            json.put("items", rows);
            // aiming add 'success' to let the search result can display after get the results
            json.put("success", true);
            if (LOG.isDebugEnabled())
                LOG.debug(json);

        } catch (Exception e) {
            PrintWriter writer = response.getWriter();
            if (e.getLocalizedMessage() != null)
                writer.write(e.getLocalizedMessage());
            e.printStackTrace(writer);
            writer.close();
            throw new ServletException(e.getLocalizedMessage());
        }
        PrintWriter writer = response.getWriter();
        writer.write(json.toString());
        writer.close();

    }

    private boolean viewFieldValidate(String viewName, String fieldPath) {

        if (viewName != null && viewName.length() > 0 && fieldPath != null && fieldPath.length() > 0) {
            if (viewName.matches("Browse_items_.*")) {
                String concept = viewName.replaceAll("Browse_items_", "").replaceAll("#.*", "");
                if (fieldPath.startsWith(concept + "/") || fieldPath.startsWith("/" + concept + "/")
                        || fieldPath.startsWith("//" + concept + "/"))
                    return true;
            }
        }

        return false;

    }
}
