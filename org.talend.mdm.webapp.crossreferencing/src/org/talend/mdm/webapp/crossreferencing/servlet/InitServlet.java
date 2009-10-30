package org.talend.mdm.webapp.crossreferencing.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.talend.mdm.webapp.crossreferencing.bean.Configuration;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataCluster;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSExistsDataCluster;
import com.amalto.webapp.util.webservices.WSExistsDataModel;
import com.amalto.webapp.util.webservices.WSPutDataCluster;
import com.amalto.webapp.util.webservices.WSPutDataModel;

/**
 * Initializes the "package" if needed.
 * This should be run after the Core Servlet e.g. with start-up priority >=1000
 *
 * @author Bruno Grieder
 *
 */
public class InitServlet extends HttpServlet {

	/**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        //detect if this is a hot deploy
        try {
        	Util.getAjaxSubject();
        } catch (Exception e1) {
	        return;
        }

        org.apache.log4j.Logger.getLogger(this.getClass()).debug("init() Cross-Referencing - checking cluster and data model");
    	//create the cluster and data model  if they do not exist
		try {
			if (!Util.getPort().existsDataCluster(new WSExistsDataCluster(new WSDataClusterPK(Configuration.datacluster))).is_true()) {
				Util.getPort().putDataCluster(new WSPutDataCluster(
						new WSDataCluster(Configuration.datacluster, "b2box Cross Referencing Data","")
				));
			}
			if (!Util.getPort().existsDataModel(new WSExistsDataModel(new WSDataModelPK(Configuration.datamodel))).is_true()) {
				Util.getPort().putDataModel(new WSPutDataModel(
						new WSDataModel(Configuration.datamodel, "b2box Cross Referencing Table Definitions","")
				));
			}
		} catch (Exception e) {
			String err = "Unable to initialize the crossreferencing data cluster and data model.";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
			throw new ServletException(err);
		}
    }

}
