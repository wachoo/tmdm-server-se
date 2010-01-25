package com.amalto.webapp.core.util;

import java.rmi.RemoteException;

import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.webapp.util.webservices.WSBackgroundJobPK;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSConceptRevisionMap;
import com.amalto.webapp.util.webservices.WSDeleteRole;
import com.amalto.webapp.util.webservices.WSDeleteSynchronizationItem;
import com.amalto.webapp.util.webservices.WSDeleteSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSDeleteUniverse;
import com.amalto.webapp.util.webservices.WSExistsRole;
import com.amalto.webapp.util.webservices.WSExistsSynchronizationItem;
import com.amalto.webapp.util.webservices.WSExistsSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSExistsUniverse;
import com.amalto.webapp.util.webservices.WSGetConceptsInDataClusterWithRevisions;
import com.amalto.webapp.util.webservices.WSGetCurrentUniverse;
import com.amalto.webapp.util.webservices.WSGetObjectsForRoles;
import com.amalto.webapp.util.webservices.WSGetObjectsForSynchronizationPlans;
import com.amalto.webapp.util.webservices.WSGetObjectsForUniverses;
import com.amalto.webapp.util.webservices.WSGetRole;
import com.amalto.webapp.util.webservices.WSGetRolePKs;
import com.amalto.webapp.util.webservices.WSGetSynchronizationItem;
import com.amalto.webapp.util.webservices.WSGetSynchronizationItemPKs;
import com.amalto.webapp.util.webservices.WSGetSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSGetSynchronizationPlanItemsAlgorithms;
import com.amalto.webapp.util.webservices.WSGetSynchronizationPlanObjectsAlgorithms;
import com.amalto.webapp.util.webservices.WSGetSynchronizationPlanPKs;
import com.amalto.webapp.util.webservices.WSGetUniverse;
import com.amalto.webapp.util.webservices.WSGetUniverseByRevision;
import com.amalto.webapp.util.webservices.WSGetUniversePKs;
import com.amalto.webapp.util.webservices.WSGetVersioningSystemConfiguration;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSItemPKArray;
import com.amalto.webapp.util.webservices.WSPutRole;
import com.amalto.webapp.util.webservices.WSPutSynchronizationItem;
import com.amalto.webapp.util.webservices.WSPutSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSPutUniverse;
import com.amalto.webapp.util.webservices.WSPutVersioningSystemConfiguration;
import com.amalto.webapp.util.webservices.WSResolveSynchronizationItem;
import com.amalto.webapp.util.webservices.WSRole;
import com.amalto.webapp.util.webservices.WSRolePK;
import com.amalto.webapp.util.webservices.WSRolePKArray;
import com.amalto.webapp.util.webservices.WSString;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSSynchronizationGetItemXML;
import com.amalto.webapp.util.webservices.WSSynchronizationGetObjectXML;
import com.amalto.webapp.util.webservices.WSSynchronizationGetUnsynchronizedItemPKs;
import com.amalto.webapp.util.webservices.WSSynchronizationGetUnsynchronizedObjectsIDs;
import com.amalto.webapp.util.webservices.WSSynchronizationItem;
import com.amalto.webapp.util.webservices.WSSynchronizationItemPK;
import com.amalto.webapp.util.webservices.WSSynchronizationItemPKArray;
import com.amalto.webapp.util.webservices.WSSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanAction;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanPK;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanPKArray;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanStatus;
import com.amalto.webapp.util.webservices.WSSynchronizationPutItemXML;
import com.amalto.webapp.util.webservices.WSSynchronizationPutObjectXML;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSUniversePKArray;
import com.amalto.webapp.util.webservices.WSVersioningCommitItems;
import com.amalto.webapp.util.webservices.WSVersioningGetInfo;
import com.amalto.webapp.util.webservices.WSVersioningGetItemHistory;
import com.amalto.webapp.util.webservices.WSVersioningGetItemsVersions;
import com.amalto.webapp.util.webservices.WSVersioningGetObjectsVersions;
import com.amalto.webapp.util.webservices.WSVersioningGetUniverseVersions;
import com.amalto.webapp.util.webservices.WSVersioningInfo;
import com.amalto.webapp.util.webservices.WSVersioningItemHistory;
import com.amalto.webapp.util.webservices.WSVersioningItemsVersions;
import com.amalto.webapp.util.webservices.WSVersioningObjectsVersions;
import com.amalto.webapp.util.webservices.WSVersioningRestoreItemByRevision;
import com.amalto.webapp.util.webservices.WSVersioningRestoreItems;
import com.amalto.webapp.util.webservices.WSVersioningRestoreObjects;
import com.amalto.webapp.util.webservices.WSVersioningSystemConfiguration;
import com.amalto.webapp.util.webservices.WSVersioningTagItems;
import com.amalto.webapp.util.webservices.WSVersioningTagObjects;
import com.amalto.webapp.util.webservices.WSVersioningTagUniverse;
import com.amalto.webapp.util.webservices.WSVersioningUniverseVersions;


/**
 * The list of web services implemented as RMI local calls
 * 
 * @author Bruno Grieder
 *
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class XtentisRMIPort extends IXtentisRMIPort {


	/**
	 *  
	 */
	public XtentisRMIPort() {
		super();
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("XtentisRMIPort() Using RMI");
	}

	public WSRolePK deleteRole(WSDeleteRole wsRoleDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationItemPK deleteSynchronizationItem(
			WSDeleteSynchronizationItem wsSynchronizationItemDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationPlanPK deleteSynchronizationPlan(
			WSDeleteSynchronizationPlan wsSynchronizationPlanDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBoolean existsRole(WSExistsRole wsExistsRole)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBoolean existsSynchronizationItem(
			WSExistsSynchronizationItem wsExistsSynchronizationItem)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBoolean existsSynchronizationPlan(
			WSExistsSynchronizationPlan wsExistsSynchronizationPlan)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
			WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniverse getCurrentUniverse(
			WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray getObjectsForSynchronizationPlans(
			WSGetObjectsForSynchronizationPlans regex) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses regex)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
			RolePOJO pojo =
				ctrl.getRole(
					new RolePOJOPK(
							wsGetRole.getWsRolePK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationItem getSynchronizationItem(
			WSGetSynchronizationItem wsGetSynchronizationItem)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationItemPKArray getSynchronizationItemPKs(
			WSGetSynchronizationItemPKs regex) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationPlan getSynchronizationPlan(
			WSGetSynchronizationPlan wsGetSynchronizationPlan)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray getSynchronizationPlanItemsAlgorithms(
			WSGetSynchronizationPlanItemsAlgorithms regex)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray getSynchronizationPlanObjectsAlgorithms(
			WSGetSynchronizationPlanObjectsAlgorithms regex)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(
			WSGetSynchronizationPlanPKs regex) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniversePKArray getUniverseByRevision(
			WSGetUniverseByRevision wsUniverseByRevision)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex)
			throws RemoteException {
		// TODO Auto-generated method stub
		WSUniversePKArray array=new WSUniversePKArray();
		return array;
	}

	public WSVersioningSystemConfiguration getVersioningSystemConfiguration(
			WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationItemPK putSynchronizationItem(
			WSPutSynchronizationItem wsSynchronizationItem)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationPlanPK putSynchronizationPlan(
			WSPutSynchronizationPlan wsSynchronizationPlan)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSUniversePK putUniverse(WSPutUniverse wsUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSString putVersioningSystemConfiguration(
			WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationItem resolveSynchronizationItem(
			WSResolveSynchronizationItem wsResolveSynchronizationItem)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSString synchronizationGetItemXML(
			WSSynchronizationGetItemXML wsSynchronizationGetItemXML)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSString synchronizationGetObjectXML(
			WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(
			WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(
			WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSSynchronizationPlanStatus synchronizationPlanAction(
			WSSynchronizationPlanAction wsSynchronizationPlanAction)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSItemPK synchronizationPutItemXML(
			WSSynchronizationPutItemXML wsSynchronizationPutItemXML)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSString synchronizationPutObjectXML(
			WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningCommitItems(
			WSVersioningCommitItems wsVersioningCommitItems)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSVersioningInfo versioningGetInfo(
			WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSVersioningItemHistory versioningGetItemHistory(
			WSVersioningGetItemHistory wsVersioningGetItemHistory)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSVersioningItemsVersions versioningGetItemsVersions(
			WSVersioningGetItemsVersions wsVersioningGetItemsVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSVersioningObjectsVersions versioningGetObjectsVersions(
			WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSVersioningUniverseVersions versioningGetUniverseVersions(
			WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBoolean versioningRestoreItemByRevision(
			WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningRestoreItems(
			WSVersioningRestoreItems wsVersioningRestoreItems)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningRestoreObjects(
			WSVersioningRestoreObjects wsVersioningRestoreObjects)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningTagItems(
			WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningTagObjects(
			WSVersioningTagObjects wsVersioningTagObjects)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSBackgroundJobPK versioningTagUniverse(
			WSVersioningTagUniverse wsVersioningTagUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}




	
}
