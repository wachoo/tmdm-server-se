package org.talend.mdm.bulkload.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BulkloadClient {
	private static final String LOAD_ERR="An error occured:";
	private static final Pattern LOAD_ERR_PATTERN=Pattern.compile("(.*?)<h1>"+LOAD_ERR+"(.*?)</h1>(.*?)");
	String url;
	String username;
	String password;
	String universe;
	String cluster;
	String concept;
	String datamodel;
	BulkloadOptions options=new BulkloadOptions();
	
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

    /**
     * @param items
     * @throws Exception
     * @deprecated Consider using {@link #load(java.io.InputStream)}
     */
	public void load(List<String > items) throws Exception{
		doLoad(items);
	}
	/**
	 * load from a huge xml string
	 * @param xmlString
     * @deprecated Consider using {@link #load(java.io.InputStream)}
	 */
	public void load(String xmlString)throws Exception {
		List<String > items=BulkloadClientUtil.getItemXmls(xmlString);			
		doLoad(items);
	}

    /**
     * <p>
     * Loads XML documents in MDM using an InputStream. All documents should follow each other.
     * For instance this InputStream can be used with this method.
     * </p>
     *
     * <code>
     * InputStream is = new ByteArrayInputStream("<doc></doc><doc></doc><doc></doc>".getBytes());
     * </code>
     *
     * <p>
     * This method blocks until all documents in <code>xmlDocuments</code> are read. For non blocking use cases
     * see {@link #load()}.
     * </p>
     *
     * @param xmlDocuments A stream that contains several XML documents.
     * @throws Exception Thrown in case of communication error
     */
    public void load(InputStream xmlDocuments) throws Exception {
        BulkloadClientUtil.bulkload(url,
                cluster,
                concept,
                datamodel,
                options.isValidate(),
                options.isSmartpk(),
                xmlDocuments,
                username,
                password,
                universe);
    }

    /**
     * <p>
     * Loads XML documents in MDM using an InputStream created for the load. This method creates a {@link Thread} that
     * wait for {@link InputStreamMerger#push(java.io.InputStream)} to get called.
     * </p>
     *
     * <code>
     * BulkloadClient client = ...<br/>
     * InputStreamMerger is = client.load();<br/>
     * for(...) {<br/>
     *      is.push(new ByteArrayInputStream("...".getBytes());<br/>
     * }
     * </code>
     *
     * @throws Exception Thrown in case of communication error
     * @see InputStreamMerger
     * @return A {@link InputStreamMerger} that allow asynchronous push to bulkload client.
     */
    public InputStreamMerger load() throws Exception {
        return BulkloadClientUtil.bulkload(url,
                cluster,
                concept,
                datamodel,
                options.isValidate(),
                options.isSmartpk(),
                options.getArraySize(),
                username,
                password,
                universe);
    }
	
	private void doLoad(List<String> items)throws Exception {
		if(items.size()>options.getArraySize()) {
			int loop=items.size()/options.getArraySize();
			int left=items.size()-options.getArraySize()*loop;
			for(int i=0; i<loop; i++) {
				List<String> subItems=items.subList(i*options.getArraySize(), (i+1)*options.getArraySize());
				String result=BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), subItems, username, password,universe);
				if(result!=null && result.indexOf(LOAD_ERR)!=-1){						
					Matcher m=LOAD_ERR_PATTERN.matcher(result);
					if(m.matches()){
						throw new Exception(m.group(2));
					}
				}
			}
			if(left>0) {
				List<String> subItems=items.subList(loop*options.getArraySize(), loop*options.getArraySize()+left);
				String result=BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), subItems, username, password,universe);
				if(result!=null && result.indexOf(LOAD_ERR)!=-1){						
					Matcher m=LOAD_ERR_PATTERN.matcher(result);
					if(m.matches()){
						throw new Exception(m.group(2));
					}
				}
			}
		}else {			
			String result=BulkloadClientUtil.bulkload(url, cluster, concept, datamodel, options.isValidate(), options.isSmartpk(), items, username, password,universe);
			if(result!=null && result.indexOf(LOAD_ERR)!=-1){						
				Matcher m=LOAD_ERR_PATTERN.matcher(result);
				if(m.matches()){
					throw new Exception(m.group(2));
				}
			}			
		}
	}
	
	/**
	 * load from File
	 * @param inputXmlFile
     * @deprecated Consider using {@link #load(java.io.InputStream)}
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
}
