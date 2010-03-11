package org.talend.mdm.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.naming.InitialContext;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.jboss.security.Base64Encoder;

import sun.misc.BASE64Decoder;
import urn_com_amalto_xtentis_webservice.WSBase64KeyValue;
import urn_com_amalto_xtentis_webservice.WSConnectorFunction;
import urn_com_amalto_xtentis_webservice.WSConnectorInteraction;
import urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse;
import urn_com_amalto_xtentis_webservice.WSConnectorResponseCode;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;

public class XtentisJCAConnectorSupportWebserviceTestCase extends WebserviceTestCase {

	private transient ConnectionFactory cxFactory = null;

	public void testConnectorInteraction() {
		new WSConnectorInteraction().setFunction(WSConnectorFunction.START);
		new WSConnectorInteraction().setJNDIName("amalto/local/service/smtp");
		WSConnectorInteractionResponse response = new WSConnectorInteractionResponse();
		Connection conx = null;
		try {

			String JNDIName = new WSConnectorInteraction().getJNDIName();
			conx = getConnection(JNDIName);

			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();

			MappedRecord recordIn = new RecordFactoryImpl()
					.createMappedRecord(RecordFactoryImpl.RECORD_IN);

			WSConnectorFunction cf = new WSConnectorInteraction().getFunction();
			if ((WSConnectorFunction.GET_STATUS).equals(cf)) {
				interactionSpec
						.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			} else if ((WSConnectorFunction.PULL).equals(cf)) {
				interactionSpec
						.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			} else if ((WSConnectorFunction.PUSH).equals(cf)) {
				interactionSpec
						.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			} else if ((WSConnectorFunction.START).equals(cf)) {
				interactionSpec
						.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			} else if ((WSConnectorFunction.STOP).equals(cf)) {
				interactionSpec
						.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
			}

			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN,
					getMapFromKeyValues(new WSConnectorInteraction()
							.getParameters()));

			MappedRecord recordOut = (MappedRecord) interaction.execute(
					interactionSpec, recordIn);

			String code = (String) recordOut
					.get(RecordFactoryImpl.STATUS_CODE_OUT);
			HashMap map = (HashMap) recordOut
					.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);

			if ("OK".equals(code)) {
				response.setCode(WSConnectorResponseCode.OK);
			} else if ("STOPPED".equals(code)) {
				response.setCode(WSConnectorResponseCode.STOPPED);
			} else if ("ERROR".equals(code)) {
				response.setCode(WSConnectorResponseCode.ERROR);
			} else {
				throw new RemoteException("Unknown code: " + code);
			}
			response.setParameters(getKeyValuesFromMap(map));
			assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conx.close();
			} catch (Exception cx) {
				org.apache.log4j.Category.getInstance(this.getClass()).debug(
						"connectorInteraction() Connection close exception: "
								+ cx.getLocalizedMessage());
			}
		}

	}

	private Connection getConnection(String JNDIName) throws RemoteException {
		try {
			if (cxFactory == null)
				cxFactory = (ConnectionFactory) (new InitialContext())
						.lookup(JNDIName);
			return cxFactory.getConnection();
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	private HashMap getMapFromKeyValues(WSBase64KeyValue[] params)
			throws RemoteException {
		try {
			HashMap map = new HashMap();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null) {
						String key = params[i].getKey();
						byte[] bytes = (new BASE64Decoder())
								.decodeBuffer(params[i].getBase64StringValue());
						if (bytes != null) {
							ByteArrayInputStream bais = new ByteArrayInputStream(
									bytes);
							ObjectInputStream ois = new ObjectInputStream(bais);
							map.put(key, ois.readObject());
						} else {
							map.put(key, null);
						}
					}
				}
			}
			return map;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	private WSBase64KeyValue[] getKeyValuesFromMap(HashMap params)
			throws RemoteException {
		try {
			if (params == null)
				return null;
			WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
			Set keys = params.keySet();
			int i = 0;
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Object value = params.get(key);
				if (value != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					String base64Value = Base64Encoder.encode(baos
							.toByteArray());
					keyValues[i] = new WSBase64KeyValue();
					keyValues[i].setKey(key);
					keyValues[i].setBase64StringValue(base64Value);
					i++;
				}
			}
			return keyValues;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

}
