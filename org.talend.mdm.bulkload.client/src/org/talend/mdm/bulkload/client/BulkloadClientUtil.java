package org.talend.mdm.bulkload.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Bulkload amount items client
 * @author achen
 *
 */
public class BulkloadClientUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		if((!args[0].startsWith("http://") && args.length>0) && (args.length != 9 || args.length!=7 || args.length!=6)) {
			usage();
			return;
		}
		String url= args[0];
		String username=args[1];
		String password=args[2];
		String cluster= args[3];
		String concept= args[4];
		String datamodel= args[5];
		boolean validate= false;
		boolean smartpk= false;
		List<String> itemdata= new ArrayList<String>();
		if(args.length==9) {
			 validate= Boolean.valueOf(args[6]);
			 smartpk= Boolean.valueOf(args[7]);	
			 itemdata=getItemXmls(args[8]);
		}
		if(args.length==7) {
			itemdata=getItemXmls(args[6]);
		}
		if(args.length==6) {//get itemdata from System.in
			byte[] buf=new byte[System.in.available()];
			System.in.read(buf);
			itemdata=getItemXmls(new String(buf));
		}
		try {
			bulkload(url,cluster, concept,datamodel, validate, smartpk, itemdata,username,password,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<String> getItemXmls(String itemdata)throws Exception {
		Element root=parse(itemdata).getDocumentElement();
		List<String> items=new ArrayList<String>();
		for(int i=0; i<root.getChildNodes().getLength(); i++) {
			Node node=root.getChildNodes().item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				items.add(nodeToString(node, true));
			}
		}
		return items;
	}
	
	private static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException{
       	StringWriter sw = new StringWriter();
       	Transformer transformer = TransformerFactory.newInstance().newTransformer();       	
       	if (omitXMLDeclaration)
       		transformer.setOutputProperty("omit-xml-declaration","yes");
       	else
       		transformer.setOutputProperty("omit-xml-declaration","no");
       	transformer.setOutputProperty("indent","yes");
       	transformer.transform(
				new DOMSource(n),
				new StreamResult(sw)
				);
       	if (sw==null) return null;       	
		return sw.toString().replaceAll("\r\n", "\n");
	}
	
	private static Document parse(String xmlString) throws ParserConfigurationException,IOException, SAXException{
		//parse
		Document d=null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//Schema validation based on schemaURL
		factory.setNamespaceAware(true);
		factory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();

		d = builder.parse(new InputSource(new StringReader(xmlString)));

		return d;
    }	
	private static void usage() {
		String usage="Usage:\n"+
		"\t java -jar bulkloadclient.jar <url> <username> <password> <datacontainer> <concept> <datamodel> [validate] [smartpk] [itemdata] \n"+
		"\t example1: java -jar bulkloadclient.jar http://localhost:8080/datamanager/loadServlet admin talend Order Country Order <itemdata><Country><isoCode>zh</isoCode><label>china</label><Continent>Asia</Continent></Country></itemdata>"+
		"\t example2(on linux): cat myfile.xml|java -jar bulkloadclient.jar http://localhost:8080/datamanager/loadServlet admin talend Order Country Order";
		
		System.out.println(usage);
	}

    public static void bulkload(String url, String cluster, String concept, String datamodel, boolean validate, boolean smartpk, InputStream itemdata,
                                String username, String password, String universe) throws Exception {
        HostConfiguration config = new HostConfiguration();
        URI uri = new URI(url, false, "utf-8");
        config.setHost(uri);

        NameValuePair[] parameters = {new NameValuePair("cluster", cluster),
                new NameValuePair("concept", concept),
                new NameValuePair("datamodel", datamodel),
                new NameValuePair("validate", String.valueOf(validate)),
                new NameValuePair("action", "load"),
                new NameValuePair("smartpk", String.valueOf(smartpk))};

        HttpClient client = new HttpClient();
        String user = universe == null || universe.trim().length() == 0 ? username : universe + "/" + username;
        client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        client.getParams().setAuthenticationPreemptive(true);

        PutMethod putMethod = new PutMethod();
        // This setPath call is *really* important (if not set, request will be sent to the JBoss root '/')
        putMethod.setPath(url);
        try {
            // Configuration
            putMethod.setRequestHeader("Content-Type", "text/xml");
            putMethod.setQueryString(parameters);
            putMethod.setContentChunked(true);
            // Set the content of the PUT request
            putMethod.setRequestEntity(new InputStreamRequestEntity(itemdata));

            client.executeMethod(config, putMethod);
        } finally {
            putMethod.releaseConnection();
        }
    }

    public static InputStreamMerger bulkload(String url, String cluster, String concept, String dataModel, boolean validate, boolean smartPK, String username, String password, String universe) {
        InputStreamMerger merger = new InputStreamMerger();

        Runnable loadRunnable = new AsyncLoadRunnable(url, cluster, concept, dataModel, validate, smartPK, merger, username, password, universe);
        Thread loadThread = new Thread(loadRunnable);
        loadThread.start();

        return merger;
    }

    /**
     *
     * @param URL
     * @param cluster
     * @param concept
     * @param datamodel
     * @param validate
     * @param smartpk
     * @param itemdata
     * @param username
     * @param password
     * @param universe
     * @return
     * @throws Exception
     * @deprecated Consider using {@link #bulkload(String, String, String, String, boolean, boolean, java.io.InputStream, String, String, String)}
     */
	public static String bulkload(String URL, String cluster,String concept,String datamodel, boolean validate, boolean smartpk, List<String> itemdata,
			String username, String password,String universe) throws Exception {
		HttpClient client = new HttpClient();
		HttpClientParams params = new HttpClientParams();
		// params.setSoTimeout(1000);
		// params.setConnectionManagerTimeout(200);
		client.setParams(params);
		String user=universe==null||universe.trim().length()==0?username:universe+"/"+username;
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(user, password));

		URI uri = new URI(URL, false, "utf-8");
		HostConfiguration config = new HostConfiguration();
		config.setHost(uri);

		PostMethod postMethod = new PostMethod(URL);
		HttpMethodParams reqParams = postMethod.getParams();  
		reqParams.setContentCharset("UTF-8"); 
		List<NameValuePair> list=new ArrayList<NameValuePair>();
		NameValuePair[] data = { new NameValuePair("cluster", cluster),
				new NameValuePair("concept", concept),
				new NameValuePair("datamodel", datamodel),
				new NameValuePair("validate", String.valueOf(validate)),
				new NameValuePair("smartpk", String.valueOf(smartpk))};
		list.addAll(Arrays.asList(data));
		for(int i=0; i<itemdata.size(); i++) {
			list.add(new NameValuePair("itemdata"+i, itemdata.get(i)));
		}
		postMethod.setRequestBody((NameValuePair[])list.toArray(new NameValuePair[list.size()]));
	 
		
		// post method
		int statusCode = 0;
		try {
			statusCode = client.executeMethod(config,postMethod);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (statusCode >= 400)
			return null;
		String str = "";
		try {
			str = postMethod.getResponseBodyAsString();
		} catch (IOException e) {

		}
		System.out.println(str);

		postMethod.releaseConnection();
		return str;
	}

    private static class AsyncLoadRunnable implements Runnable {
        private final String url;
        private final String cluster;
        private final String concept;
        private final String dataModel;
        private final boolean validate;
        private final boolean smartPK;
        private final InputStream inputStream;
        private final String userName;
        private final String password;
        private final String universe;

        public AsyncLoadRunnable(String url, String cluster, String concept, String dataModel, boolean validate, boolean smartPK, InputStream inputStream, String userName, String password, String universe) {
            this.url = url;
            this.cluster = cluster;
            this.concept = concept;
            this.dataModel = dataModel;
            this.validate = validate;
            this.smartPK = smartPK;
            this.inputStream = inputStream;
            this.userName = userName;
            this.password = password;
            this.universe = universe;
        }

        public void run() {
            try {
                System.out.println("Start bulkload");
                bulkload(url, cluster, concept, dataModel, validate, smartPK, inputStream, userName, password, universe);
                System.out.println("End bulkload");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
