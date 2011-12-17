package talend.core.transformer.plugin.v2.tiscall.test;


import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import talend.core.transformer.plugin.v2.tiscall.CompiledParameters;
import talend.core.transformer.plugin.v2.tiscall.webservices.Args;
import talend.core.transformer.plugin.v2.tiscall.webservices.WSxml;
import talend.core.transformer.plugin.v2.tiscall.webservices.WSxmlService;

public class TISCallTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void callTIStest() {
		CompiledParameters parameters = new CompiledParameters();
		parameters.setUrl("http://localhost:8080/talend/TalendPort");
		parameters.setUsername("admin");
		parameters.setPassword("xtentis");
		
		String text = "<test>jhgjhgjgh</test>";
		
		URL url = null;
        try {
	        url = new URL("file:///Users/bgrieder/Documents/workspace/talend.core.transformer.plugin.v2/src/wsdl/tis.wsdl");
        } catch (MalformedURLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        Assert.fail("callTIStest: "+e.getMessage());
        }
		
		WSxmlService service = new WSxmlService(url, new QName("http://talend.org", "WSxmlService"));
		WSxml port = service.getWSxml();
		
		BindingProvider bp = (BindingProvider)port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parameters.getUrl());
		bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, parameters.getUsername());
		bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, parameters.getPassword());


		Args args = new Args();
		args.getItem().add(text);
	
		String result = port.runJob(args).getItem().get(0).getItem().get(0);
		

		System.out.println(result);
		
	}
	
}
