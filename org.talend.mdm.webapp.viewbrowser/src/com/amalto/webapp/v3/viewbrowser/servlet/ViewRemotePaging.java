package com.amalto.webapp.v3.viewbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.v3.viewbrowser.bean.View;

/**
 * 
 * @author asaintguilhem
 * 
 * serve data to a json grid
 */

public class ViewRemotePaging extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ViewRemotePaging.class);

    public ViewRemotePaging() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (LOG.isDebugEnabled())
            LOG.debug("doPost() called"); //$NON-NLS-1$

        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        response.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$

        Configuration config = null;
        try {
            // config = (Configuration)request.getSession().getAttribute("configuration");
            config = Configuration.getInstance(true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        String start = request.getParameter("start"); //$NON-NLS-1$
        String limit = request.getParameter("limit"); //$NON-NLS-1$

        String viewName = request.getParameter("viewName"); //$NON-NLS-1$				
        String criteria = request.getParameter("criteria"); //$NON-NLS-1$
        int max = 50;
        if (limit != null && limit.length() > 0)
            max = Integer.parseInt(limit);
        int skip = 0;
        if (limit != null && limit.length() > 0)
            skip = Integer.parseInt(start);
        String sortDir = null;
        String sortCol = null;
        if (request.getParameter("sort") != null && request.getParameter("sort").length() > 0) //$NON-NLS-1$ //$NON-NLS-2$
            sortCol = request.getParameter("sort"); //$NON-NLS-1$
        if (sortCol != null && request.getParameter("dir") != null && request.getParameter("dir").length() > 0) { //$NON-NLS-1$ //$NON-NLS-2$
            if (request.getParameter("dir").toUpperCase().equals("ASC")) //$NON-NLS-1$ //$NON-NLS-2$
                sortDir = "ascending"; //$NON-NLS-1$
            if (request.getParameter("dir").toUpperCase().equals("DESC")) //$NON-NLS-1$ //$NON-NLS-2$
                sortDir = "descending"; //$NON-NLS-1$
        }

        JSONObject json = new JSONObject();
        int totalCount = 0;
        String[] results;
        ArrayList<String[]> viewBrowserContent = new ArrayList<String[]>();

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("doPost() case : new remote items call"); //$NON-NLS-1$
            ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
            WSWhereItem wi;
            String[] filters = criteria.split(","); //$NON-NLS-1$
            String[] filterXpaths = new String[filters.length];
            String[] filterOperators = new String[filters.length];
            String[] filterValues = new String[filters.length];

            for (int i = 0; i < filters.length; i++) {
                if (LOG.isDebugEnabled())
                    LOG.debug(filters[i]);
                filterXpaths[i] = filters[i].split("#")[0]; //$NON-NLS-1$
                filterOperators[i] = filters[i].split("#")[1]; //$NON-NLS-1$
                filterValues[i] = filters[i].split("#")[2]; //$NON-NLS-1$
            }
            for (int i = 0; i < filterValues.length; i++) {
                if (filterValues[i] == null || "*".equals(filterValues[i]) || "".equals(filterValues[i])) //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                // if("CONTAINS".equals(filterOperators[i])) filterValues[i] = filterValues[i];
                if ("Any field".equals(filterXpaths[i])) //$NON-NLS-1$
                    filterXpaths[i] = ""; //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition(filterXpaths[i], Util.getOperator(filterOperators[i]),
                        filterValues[i], WSStringPredicate.NONE, false);
                WSWhereItem item = new WSWhereItem(wc, null, null);
                conditions.add(item);
            }
            if (conditions.size() == 0) {
                wi = null;
            } else {
                WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
                wi = new WSWhereItem(null, and, null);
            }
            results = Util
                    .getPort()
                    .viewSearch(
                            new WSViewSearch(new WSDataClusterPK(config.getCluster()), new WSViewPK(viewName), wi, -1, skip, max,
                                    sortCol, sortDir)).getStrings();

            String[] results2 = new String[results.length - 1];
            System.arraycopy(results, 1, results2, 0, results.length - 1);
            request.getSession().setAttribute("resultsXML", results2); //$NON-NLS-1$

            String[] viewables = new View(viewName).getViewablesXpath();
            for (int i = 0; i < results.length; i++) {
                // yin guo fix bug 0010867. the totalCountOnfirstRow is true.
                if (i == 0) {
                    totalCount = Integer.parseInt(Util.parse(results[i]).getDocumentElement().getTextContent());
                    continue;
                }
                List<String> list = null;
                if (list == null) {
                    // aiming modify when there is null value in fields, the viewable fields sequence is the same as the
                    // childlist of result
                    if (!results[i].startsWith("<result>")) { //$NON-NLS-1$
                        results[i] = "<result>" + results[i] + "</result>"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    Element root = Util.parse(results[i]).getDocumentElement();
                    list = Util.getElementValues(root);
                }
                String[] elements = list.toArray(new String[list.size()]);
                // end
                String[] fields = new String[viewables.length];
                // aiming modify
                int count = Math.min(elements.length, fields.length);
                for (int j = 0; j < count; j++) {
                    if (elements[j] != null)
                        fields[j] = StringEscapeUtils.unescapeXml(elements[j]);
                    else
                        fields[j] = ""; //$NON-NLS-1$
                }

                viewBrowserContent.add(fields);
            }
            request.getSession().setAttribute("viewBrowserContent", viewBrowserContent); //$NON-NLS-1$

            // get part we are interested
            if (max > totalCount)
                max = totalCount;
            if (max > (totalCount - skip)) {
                max = totalCount - skip;
            }
            if (LOG.isDebugEnabled())
                LOG.debug("doPost() starting to build json object"); //$NON-NLS-1$
            json.put("TotalCount", totalCount); //$NON-NLS-1$
            ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
            for (int i = skip; i < (max + skip); i++) {
                int index = i - skip;
                if (index > viewBrowserContent.size() - 1)
                    break;
                JSONObject fields = new JSONObject();
                fields.put("id", index); //$NON-NLS-1$
                for (int j = 0; j < viewBrowserContent.get(index).length; j++) {
                    fields.put("/" + viewables[j], viewBrowserContent.get(index)[j]); //$NON-NLS-1$
                }
                rows.add(fields);
            }

            json.put("view", rows); //$NON-NLS-1$
            // aiming add 'success' to let the search result can display after get the results
            json.put("success", true); //$NON-NLS-1$

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            PrintWriter writer = response.getWriter();
            writer.write(e.getLocalizedMessage());
            writer.close();
            throw new ServletException(e.getLocalizedMessage());
        }

        PrintWriter writer = response.getWriter();
        writer.write(json.toString());
        writer.close();

    }

}
