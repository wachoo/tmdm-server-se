package talend.ext.service.dao;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import talend.ext.service.entity.SrvSchedulePlan;
import talend.ext.service.entity.SrvSchedulePlanStatus;
import talend.ext.service.template.TemplateFillAssistant;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.util.XtentisException;

public class ServiceSchedulePlanDAOImpl implements ServiceSchedulPlanDAO {

	private static final String PLAN_CLUSTER_NAME = "MDMServiceSchedulePlans";

	private Logger logger = Logger.getLogger(this.getClass());

	private XmlServerSLWrapperLocal server;

	public ServiceSchedulePlanDAOImpl() {
		try {
			server = ((XmlServerSLWrapperLocalHome) new InitialContext()
					.lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to access the XML Server wrapper";
			logger.error(err, e);
		}
	}

	public boolean savePlan(SrvSchedulePlan srvSchedulePlan) {

		try {
			// set id
			if (srvSchedulePlan.getSchedulePlanId() == null
					|| srvSchedulePlan.getSchedulePlanId().length() == 0) {
				String uniqueId = srvSchedulePlan.obtainSrvSchedulePlanPk()
						.getUniqueId();
				srvSchedulePlan.setSchedulePlanId(uniqueId);
			}

			// set status
			if (srvSchedulePlan.getSchedulePlanStatus() == null
					|| srvSchedulePlan.getSchedulePlanStatus().trim()
							.equals(""))
				srvSchedulePlan
						.setSchedulePlanStatus(SrvSchedulePlanStatus.UNSCHEDULE);

			// reload parameters
			srvSchedulePlan.setParameters(TemplateFillAssistant.fill(
					srvSchedulePlan.getServiceName(), srvSchedulePlan
							.getMethodName(), srvSchedulePlan.getParameters(),
					srvSchedulePlan.getSchedulePlanId()));

			// Marshal
			StringWriter sw = new StringWriter();
			Marshaller.marshal(srvSchedulePlan, sw);

			long rtnStatus = server.putDocumentFromString(sw.toString(),
					srvSchedulePlan.getSchedulePlanId(), PLAN_CLUSTER_NAME,
					null);
			if (rtnStatus == -1) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public SrvSchedulePlan loadPlan(String srvSchedulePlanID) {

		try {

			String itemProjection = server.getDocumentAsString(null,
					PLAN_CLUSTER_NAME, srvSchedulePlanID, null);
			if (itemProjection == null) {
				return null;
			}

			SrvSchedulePlan srvSchedulePlan = (SrvSchedulePlan) Unmarshaller
					.unmarshal(SrvSchedulePlan.class, new InputSource(
							new StringReader(itemProjection)));
			
			return srvSchedulePlan;

		} catch (XtentisException e) {
			e.printStackTrace();
			return null;
		} catch (MarshalException e) {
			e.printStackTrace();
			return null;
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}

	}

	public boolean updatePlanStatus(String srvSchedulePlanID,
			String schedulePlanStatus) {

		SrvSchedulePlan srvSchedulePlan = loadPlan(srvSchedulePlanID);
		
		return updatePlanStatus(srvSchedulePlan,schedulePlanStatus);
	}
	
	public boolean updatePlanStatus(SrvSchedulePlan srvSchedulePlan,
			String schedulePlanStatus) {

		if (srvSchedulePlan == null)
			return false;
		
		srvSchedulePlan.setSchedulePlanStatus(schedulePlanStatus);

		return savePlan(srvSchedulePlan);

	}
	
	public ArrayList findAllSchedulingPlans() throws XtentisException {
		
		StringBuffer query=new StringBuffer();
		query.append("for $x in collection(\"MDMServiceSchedulePlans\")/srv-schedule-plan "); 
		query.append("where $x/schedule-plan-status=\"scheduling\" "); 
		query.append("return $x ");
		return server.runQuery(null, PLAN_CLUSTER_NAME, query.toString(), null);

	}
	
	public ArrayList findAllPlans() throws XtentisException {
		StringBuffer query=new StringBuffer();
		query.append("for $x in collection(\"MDMServiceSchedulePlans\")/srv-schedule-plan "); 
		query.append("return $x ");
		return server.runQuery(null, PLAN_CLUSTER_NAME, query.toString(), null);
	}
	
	public boolean deletePlan(String srvSchedulePlanID) {
		
		try {
			long rtnStatus=server.deleteDocument(null, PLAN_CLUSTER_NAME, srvSchedulePlanID);
			if(rtnStatus==-1){
				return false;
			}else{
				return true;
			}
		} catch (XtentisException e) {
			e.printStackTrace();
			return false;
		}

	}

}
