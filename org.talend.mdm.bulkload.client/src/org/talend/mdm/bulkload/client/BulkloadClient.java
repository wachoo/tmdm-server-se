package org.talend.mdm.bulkload.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;

public class BulkloadClient {
	String url;
	String username;
	String password;
	String universe;
	String cluster;
	String concept;
	String datamodel;
	BulkloadOptions options;
	
	public BulkloadClient(String url, String username,String password,String universe,String cluster,String concept,String datamodel) {
		this.url=url;
		this.username=username;
		this.password=password;
		this.universe=universe;
		this.cluster=cluster;
		this.concept=concept;
		this.datamodel=datamodel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUniverse() {
		return universe;
	}

	public void setUniverse(String universe) {
		this.universe = universe;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getDatamodel() {
		return datamodel;
	}

	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}

	public BulkloadOptions getOptions() {
		return options;
	}

	public void setOptions(BulkloadOptions options) {
		this.options = options;
	}
	public void load(List<String > items) {
		doLoad(items);
	}
	/**
	 * load from a huge xml string
	 * @param xmlString
	 */
	public void load(String xmlString)throws Exception {
		List<String > items=BulkloadClientUtil.getItemXmls(xmlString);			
		doLoad(items);
	}
	
	private void doLoad(List<String> items) {
		if(items.size()>options.getArraySize()) {
			int loop=items.size()/options.getArraySize();
			int left=items.size()-options.getArraySize()*loop;
			for(int i=0; i<loop; i++) {
				List<String> subItems=items.subList(i*options.getArraySize(), (i+1)*options.getArraySize());
				try {
					BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), subItems, username, password,universe);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(left>0) {
				List<String> subItems=items.subList(loop*options.getArraySize(), loop*options.getArraySize()+left);
				try {
					BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), subItems, username, password,universe);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			try {
				BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), items, username, password,universe);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * load from File
	 * @param inputXmlFile
	 */
	public void load(Reader inputXmlFile) {
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(inputXmlFile);
			StringBuffer sb=new StringBuffer();
			String line=reader.readLine();
			while(line!=null) {
				sb=sb.append(line);
				line=reader.readLine();
			}
			List<String > items=BulkloadClientUtil.getItemXmls(sb.toString());			
			doLoad(items);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					
				}
		}
	}
	
	public static void main(String[] args) {
		//test
		//FileReader reader=new FileReader(file)
		URL url=BulkloadClient.class.getResource("test.xml");
		try {
			BufferedInputStream in=((BufferedInputStream)url.getContent());
			byte[] buf=new byte[in.available()];
			in.read(buf);
			String xml=new String(buf);
			BulkloadClient client=new BulkloadClient("http://localhost:8080/datamanager/loadServlet","admin","talend",null,"Order","Country","Order");
			client.setOptions(new BulkloadOptions());
			client.load(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
