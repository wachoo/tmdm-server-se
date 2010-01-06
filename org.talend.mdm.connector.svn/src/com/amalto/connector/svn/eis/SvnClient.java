package com.amalto.connector.svn.eis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.TagStructureInfo;

public class SvnClient  {
	private static final long serialVersionUID = 1L;
	private String url;
	private String user;
	private String password;

	private boolean connected;
	private SVNRepository repository = null;
	// Cache management
	// -----------------







	/**
	 * Create a new instance
	 *
	 * @param url
	 * @param user
	 * @param password
	 */

	public SvnClient(String host, String user, String password) {
		setUrl(host);
		setUser(user);
		setPassword(password);
	}



	public void connect() throws SocketException, IOException {
		try {
	        /*
	         * Creates an instance of SVNRepository to work with the repository.
	         * All user's requests to the repository are relative to the
	         * repository location used to create this SVNRepository.
	         * SVNURL is a wrapper for URL strings that refer to repository locations.
	         */
	        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));

		    ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
		    repository.setAuthenticationManager(authManager);
	        connected =true;
	    } catch (SVNException svne) {
	        /*
	         * Perhaps a malformed URL is the cause of this exception
	         */
	    	connected = false;
			Logger.getLogger(SvnClient.class).error("Unable to connect to Svn server "+url+".");

	    }
	}


	public boolean isConnected() {
		boolean b = true;
		/*
		boolean b = super.isConnected();

		if (b) {
			try {
				super.getStatus();
				// String myString = super.getStatus();
				// System.out.println(">>>>" + myString);
			} catch (Exception e) {
				return false;
			}
		}
		*/
		return b;
	}


	// Standard encapsulation

	public String getUrl() {
		return url;
	}

	public void setUrl(String host) {
		this.url = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String debug() {
		String str = "";
		str += "\nconnected:" + this.isConnected();
		str += "\nhost:" + url;
		str += "\nuser:" + user;
		str += "\npassword:" + password;
		return str;
	}

	public void commit(String filePath, byte[]content, String comments) throws SVNException {
		//TODO
		//Check if file already exists
		//SVNProperties fileProperties = new SVNProperties();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    //using svnkit 1.3.0 ,using SVNProperties
	    //HashMap<String,String> fileProperties = new HashMap<String,String>();
	    SVNProperties fileProperties =new SVNProperties();
	    
        /*
         * Checks up if the specified path really corresponds to a file. If
         * doesn't the program exits. SVNNodeKind is that one who says what is
         * located at a path in a revision. -1 means the latest revision.
         */

	    int indexDir = filePath.indexOf("/",1);
	    String dirPath = filePath.substring(0, indexDir);
	    String relativeFilePath = filePath.substring(indexDir+1);

	    Logger.getLogger(SvnClient.class).debug("DirPath = "+dirPath+" relativeFilePath = "+relativeFilePath);

        SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
        if (nodeKind == SVNNodeKind.FILE) {
        	Logger.getLogger(SvnClient.class).info("File already exists. Try to get its content.");
        	repository.getFile(filePath, -1, fileProperties, baos);
        }

        SVNNodeKind dirKind = repository.checkPath(dirPath, -1);
        ISVNEditor editor = repository.getCommitEditor(comments, null);


        if (nodeKind == SVNNodeKind.NONE) {
        	Logger.getLogger(SvnClient.class).info("First commit for filePath = "+filePath);



    	    if ((dirKind != SVNNodeKind.DIR ) && (dirKind != SVNNodeKind.NONE)){
    	    	Logger.getLogger(SvnClient.class).error("Path "+dirPath+" doesn't match with a Svn directory.");
    	    	return;
    	    }

        	editor.openRoot(-1);
        	if (dirKind == SVNNodeKind.NONE) {
        		editor.addDir(dirPath, null, -1);
        		//editor.openDir(dirPath,-1);
        	}
        	else{
        		editor.openDir(dirPath,-1);
        	}

    		editor.addFile(filePath, null, -1);
    		
    		editor.applyTextDelta(filePath, null);
    		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(content), editor, true);
            
            editor.closeFile(filePath, checksum);

        	editor.closeDir();
        	editor.closeDir();

            editor.closeEdit();
            Logger.getLogger(SvnClient.class).info("First commit for filePath = "+filePath+" ended");

        } else if (nodeKind == SVNNodeKind.DIR) {
        	Logger.getLogger(SvnClient.class).error("Unable to commit file for filePath = "+filePath+" because a folder already exists with this name.");
        	return;
        } else if (nodeKind == SVNNodeKind.FILE) {
        	Logger.getLogger(SvnClient.class).info("A file already exists with this name.");
        	editor.openRoot(-1);
        	editor.openDir(dirPath,-1);
        	editor.openFile(filePath, -1);
        	editor.applyTextDelta(filePath, null);
        	SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(baos.toByteArray()), 0, new ByteArrayInputStream(content), editor, true);


            editor.closeFile(filePath, checksum);
            editor.closeDir();
            editor.closeDir();
            editor.closeEdit();
        	Logger.getLogger(SvnClient.class).info("Successfully update file.");
        }
	}


	/*
	 * We only branch from head revision. The filePath must be the absolute path of the item to branch.
	 * If the filePath is a dir, we will branch all the content of the directory.
	 * The branch name will be the name of the first directory
	 * in filePath concatened with "-"+tag
	 * */
	public void branch(String filePath, String comments, String tag) throws SVNException {
	    String absoluteSrcPath = repository.getRepositoryPath(filePath);

	    int indexDir = absoluteSrcPath.indexOf("/",1);

	    String dirPath = absoluteSrcPath;
	    if (indexDir!=-1)
	    	dirPath = absoluteSrcPath.substring(0, indexDir);

	    String destDirPath = dirPath+"-"+tag;

	    SVNNodeKind destDirKind = repository.checkPath(destDirPath, -1);

	    if ((destDirKind != SVNNodeKind.DIR ) && (destDirKind != SVNNodeKind.NONE)){
	    	Logger.getLogger(SvnClient.class).error("Path "+destDirPath+" doesn't match with a Svn directory.");
	    	return;
	    }

	    String relativeFilePath = "";
	    if (indexDir!=-1 && indexDir < absoluteSrcPath.length())
	    	relativeFilePath = absoluteSrcPath.substring(indexDir+1);


		Logger.getLogger(SvnClient.class).debug("checkPath("+destDirPath+"/"+relativeFilePath+", -1);");

    	SVNNodeKind destFileKind = repository.checkPath(destDirPath+"/"+relativeFilePath, -1);

		Logger.getLogger(SvnClient.class).debug("checkPath("+destDirPath+"/"+relativeFilePath+", -1) return "+destFileKind);


        long srcRevision = repository.getLatestRevision();

        ISVNEditor editor = repository.getCommitEditor(comments, null);

        editor.openRoot(-1);


        if (relativeFilePath.equals("")) {//Case of tagging a full directory
        	Logger.getLogger(SvnClient.class).debug("Tagging a full directory");
        	if (destDirKind == SVNNodeKind.NONE) {
            	Logger.getLogger(SvnClient.class).debug("Tagging a full directory. Directory "+destDirPath+" doesn't exists.");
        		editor.addDir(destDirPath, dirPath, srcRevision);
        	}
        	else {
            	Logger.getLogger(SvnClient.class).debug("Tagging a full directory. Directory "+destDirPath+" already exists.");
        		editor.deleteEntry(destDirPath,-1);
        		editor.addDir(destDirPath, dirPath, srcRevision);
        	}
        } else {//Case of tagging only one file
           	Logger.getLogger(SvnClient.class).debug("Tagging a single file.");
        	if (destDirKind == SVNNodeKind.NONE) {//Must add directory without copying all its content
               	Logger.getLogger(SvnClient.class).debug("Tagging a single file. Directory "+destDirPath+" doesn't exists.");
        		editor.addDir(destDirPath,null, -1);
        		Logger.getLogger(SvnClient.class).debug("Tagging a single file. Add directory "+destDirPath+".");
        	} else {
               	Logger.getLogger(SvnClient.class).debug("Tagging a single file. Directory "+destDirPath+" already exists.");
        		editor.openDir(destDirPath, -1);
        	}


        	if (destFileKind != SVNNodeKind.NONE && destFileKind != SVNNodeKind.FILE) {
        		Logger.getLogger(SvnClient.class).error("Path "+destDirPath+"/"+relativeFilePath+" doesn't match with a Svn file.");
    	    	return;
        	}
        	if (destFileKind == SVNNodeKind.NONE) {
               	Logger.getLogger(SvnClient.class).debug("Tagging a single file. File "+destDirPath+"/"+relativeFilePath+" doesn't exists.");
        		editor.addFile(destDirPath+"/"+relativeFilePath,dirPath+"/"+relativeFilePath, srcRevision);
        		editor.closeFile(destDirPath+"/"+relativeFilePath,null);
        	} else {
        		Logger.getLogger(SvnClient.class).debug("Tagging a single file. File "+destDirPath+"/"+relativeFilePath+" already exists.");
        		editor.deleteEntry(destDirPath+"/"+relativeFilePath,-1);
        		editor.addFile(destDirPath+"/"+relativeFilePath,dirPath+"/"+relativeFilePath, srcRevision);
        		editor.closeFile(destDirPath+"/"+relativeFilePath,null);
        	}


        }

        editor.closeDir();
        editor.closeDir();

        SVNCommitInfo commitInfo = editor.closeEdit();



        System.out.println("The directory was copied: " + commitInfo);


	    Logger.getLogger(SvnClient.class).debug("DirPath = "+dirPath+" relativeFilePath = "+relativeFilePath);


	}

	public void delete(String path, String tag) throws SVNException {

		if (path == null || "".equals(path)) {
			Logger.getLogger(SvnClient.class).error("Delete the whole Svn repository is not allowed.");
			return;
		}


	    int indexDir = path.indexOf("/",1);

	    String dirPath = path;
	    if (indexDir!=-1)
	    	dirPath = path.substring(0, indexDir);

	    if (tag!=null && (!"".equals(tag)))
	    	dirPath = dirPath+"-"+tag;

	    SVNNodeKind dirKind = repository.checkPath(dirPath, -1);

	    if ((dirKind != SVNNodeKind.DIR ) && (dirKind != SVNNodeKind.NONE)){
	    	Logger.getLogger(SvnClient.class).error("Path "+dirPath+" doesn't match with a Svn directory.");
	    	return;
	    }

	    String relativeFilePath = "";
	    if (indexDir!=-1 && indexDir < path.length())
	    	relativeFilePath = path.substring(indexDir+1);


		Logger.getLogger(SvnClient.class).debug("checkPath("+dirPath+"/"+relativeFilePath+", -1);");

    	SVNNodeKind destFileKind = repository.checkPath(dirPath+"/"+relativeFilePath, -1);

		Logger.getLogger(SvnClient.class).debug("checkPath("+dirPath+"/"+relativeFilePath+", -1) return "+destFileKind);


        long srcRevision = repository.getLatestRevision();

        ISVNEditor editor = repository.getCommitEditor("Delete "+dirPath+"/"+relativeFilePath, null);

        editor.openRoot(-1);


        if (relativeFilePath.equals("")) {//Case of deleting a full directory
        	Logger.getLogger(SvnClient.class).debug("Delete a full directory");
        	if (dirKind == SVNNodeKind.NONE) {
            	Logger.getLogger(SvnClient.class).debug("Directory "+dirPath+" doesn't exists.");
        	}
        	else {
            	Logger.getLogger(SvnClient.class).debug("Delete a full directory. Directory "+dirPath+" already exists.");
        		editor.deleteEntry(dirPath,-1);
        	}
        } else {//Case of deleting only one file
           	Logger.getLogger(SvnClient.class).debug("Deleting a single file.");
        	if (dirKind == SVNNodeKind.NONE) {//Must add directory without copying all its content
               	Logger.getLogger(SvnClient.class).debug("Deleting a single file. Directory "+dirPath+" doesn't exists.");
        	} else {
               	Logger.getLogger(SvnClient.class).debug("Deleting a single file. Directory "+dirPath+" already exists.");
        		editor.openDir(dirPath, -1);
        	}


        	if (destFileKind != SVNNodeKind.NONE && destFileKind != SVNNodeKind.FILE) {
        		Logger.getLogger(SvnClient.class).error("Path "+dirPath+"/"+relativeFilePath+" doesn't match with a Svn file.");
    	    	return;
        	}
        	if (destFileKind == SVNNodeKind.NONE) {
               	Logger.getLogger(SvnClient.class).debug("Deleting a single file. File "+dirPath+"/"+relativeFilePath+" doesn't exists.");
        	} else {
        		Logger.getLogger(SvnClient.class).debug("Deleting a single file. File "+dirPath+"/"+relativeFilePath+" already exists.");
        		editor.deleteEntry(dirPath+"/"+relativeFilePath,-1);
        	}

        	editor.closeDir();
        }


        editor.closeDir();

        SVNCommitInfo commitInfo = editor.closeEdit();



        System.out.println("The path was deleted: " + commitInfo);


	    Logger.getLogger(SvnClient.class).debug("DirPath = "+dirPath+" relativeFilePath = "+relativeFilePath);


	}


	public byte[] checkout(String filePath, String tag, long revision) throws SVNException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    //using svnkit 1.3.0 ,using SVNProperties
	    //HashMap<String,String> fileProperties = new HashMap<String,String>();
	    SVNProperties fileProperties =new SVNProperties();

	    if ((tag!=null) && !"".equals(tag)) {
			int indexDir = filePath.indexOf("/",1);

		    String dirPath = filePath;
		    if (indexDir!=-1)
		    	dirPath = filePath.substring(0, indexDir);

		    String taggedDirPath = dirPath+"-"+tag;

		    String relativeFilePath = "";

		    if (indexDir!=-1 && indexDir < filePath.length())
		    	relativeFilePath = filePath.substring(indexDir+1);

		    filePath = taggedDirPath+"/"+relativeFilePath;
	    }


        SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

        if (nodeKind == SVNNodeKind.NONE) {
        	Logger.getLogger(SvnClient.class).error("Try to checkout unexisting file "+filePath);
        	return null;
        } else if (nodeKind == SVNNodeKind.DIR) {
        	Logger.getLogger(SvnClient.class).error("Unable to checkout file for filePath = "+filePath+" because a folder already exists with this name.");
        	return null;
        } else if (nodeKind == SVNNodeKind.FILE) {
        	Logger.getLogger(SvnClient.class).info("File "+filePath+" exists.");
            repository.getFile(filePath, revision, fileProperties, baos);
            Logger.getLogger(SvnClient.class).debug("File "+filePath+" properties => "+fileProperties.toString());


        	Logger.getLogger(SvnClient.class).info("Successfully checkout file.");
        	return baos.toByteArray();
        }
        return null;
	}

	public String[] list(String filePath, String tag) throws SVNException {

		String originalPath = filePath;
		String [] result = null;
		if ((tag!=null) && !"".equals(tag)) {
			int indexDir = filePath.indexOf("/",1);

		    String dirPath = filePath;
		    if (indexDir!=-1)
		    	dirPath = filePath.substring(0, indexDir);

		    String taggedDirPath = dirPath+"-"+tag;


		    String relativeFilePath = "";
		    if (indexDir!=-1 && indexDir < filePath.length())
		    	relativeFilePath = filePath.substring(indexDir+1);

		    filePath = taggedDirPath+"/"+relativeFilePath;
		}

        SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

        if (nodeKind == SVNNodeKind.NONE) {
        	result = new String[0];
        	return result;
        } else if (nodeKind == SVNNodeKind.DIR) {
        	Collection entries = repository.getDir(filePath, -1, null, (Collection) null);
            Iterator iterator = entries.iterator();
            result = new String[entries.size()];
            int index = 0;
            while (iterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) iterator.next();
                result[index++] = entry.getName();
            }

        	return result;
        } else if (nodeKind == SVNNodeKind.FILE) {
        	result = new String [1];
        	result[0] = originalPath;
        	return result;
        }
        return null;
	}


	public HistoryInfos history(String filePath) throws SVNException {
		//TODO
		//Check if file already exists
		//SVNProperties fileProperties = new SVNProperties();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    HashMap<String,String> fileProperties = new HashMap<String,String>();
        /*
         * Checks up if the specified path really corresponds to a file. If
         * doesn't the program exits. SVNNodeKind is that one who says what is
         * located at a path in a revision. -1 means the latest revision.
         */
        SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

        if (nodeKind == SVNNodeKind.NONE) {
        	Logger.getLogger(SvnClient.class).debug("Try to checkout unexisting file "+filePath);
        	HistoryInfos infos = new HistoryInfos();
            infos.setFileName(filePath);
            String [] tmp = new String[0];

            infos.setAuthors(tmp);
            infos.setComments(tmp);
            infos.setDates(tmp);
            infos.setRevisions(tmp);
            infos.setTagNames(tmp);

        	return infos;
        } else {
        	Logger.getLogger(SvnClient.class).info("File "+filePath+" exists.");

            Collection entries = repository.log(new String [] {filePath},null,0,-1,false, false);

            HistoryInfos infos = new HistoryInfos();
            infos.setFileName(filePath);
            int size = entries.size();
            String [] authors = new String[size];
            String [] comments = new String[size];
            String [] dates = new String[size];
            String [] revisions = new String[size];
            String [] tags = new String[size];

            Iterator it = entries.iterator();
            int index = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            while (it.hasNext()) {
            	SVNLogEntry logEntry = (SVNLogEntry) it.next();
                System.out.println("---------------------------------------------");
                /*
                 * gets the revision number
                 */
                System.out.println("revision: " + logEntry.getRevision());
                revisions[index] = ""+logEntry.getRevision();
                /*
                 * gets the author of the changes made in that revision
                 */
                System.out.println("author: " + logEntry.getAuthor());
                authors[index] = ""+logEntry.getAuthor();
                /*
                 * gets the time moment when the changes were committed
                 */
                System.out.println("date: " + logEntry.getDate());
                dates[index] = sdf.format(logEntry.getDate());
                /*
                 * gets the commit log message
                 */
                System.out.println("log message: " + logEntry.getMessage());
                comments[index] = logEntry.getMessage();

                tags[index] = "";

                index++;
            }

            infos.setAuthors(authors);
            infos.setComments(comments);
            infos.setDates(dates);
            infos.setRevisions(revisions);
            infos.setTagNames(tags);


        	Logger.getLogger(SvnClient.class).info("Successfully get history.");
        	return infos;
        }
	}

	public HistoryInfos versions(String filePath) throws SVNException {
	    Logger.getLogger(SvnClient.class).debug("NEW Try to get versions for file "+filePath);
		//TODO
		//Check if file already exists
		//SVNProperties fileProperties = new SVNProperties();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    //HashMap<String,String> fileProperties = new HashMap<String,String>();

	    int indexRoot = filePath.indexOf("/");
	    String rootPath = filePath;
	    String itemPath = "";
	    if (indexRoot!=-1) {
	    	itemPath = rootPath.substring(indexRoot+1);
	    	rootPath = rootPath.substring(0, indexRoot);
	    	Logger.getLogger(SvnClient.class).debug("rootPath = "+rootPath+" and itemPath = "+itemPath);
	    }

	    HistoryInfos infos = new HistoryInfos();
        infos.setFileName(filePath);

	    Vector<String> authors = new Vector<String>();
	    Vector<String> comments = new Vector<String>();
	    Vector<String> dates = new Vector<String>();
	    Vector<String> revisions = new Vector<String>();
	    Vector<String> tags = new Vector<String>();

	   if (repository == null) {
	    	Logger.getLogger(SvnClient.class).error("Svn Client versions repository is null");
	    }
	    Collection entries = repository.getDir("", -1, null, (Collection) null);
	    Iterator it = entries.iterator();
	    while (it.hasNext()) {
	    	SVNDirEntry entry = (SVNDirEntry) it.next();
            System.out.println("/"+ entry.getName() + " (author: '" + entry.getAuthor()
                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");

            if (entry.getName().startsWith(rootPath+"-")) {
            	String version = entry.getName().substring(rootPath.length()+1);
            	String tmpFilePath = entry.getName()+"/"+itemPath;
            	SVNNodeKind nodeKind = repository.checkPath(tmpFilePath, -1);

            	if (nodeKind == SVNNodeKind.NONE) {
                	Logger.getLogger(SvnClient.class).debug("No version "+version+" for file "+itemPath);
                } else {
                	int revision = 0;

                	if (itemPath!=null && !"".equals(itemPath)) {
                		Logger.getLogger(SvnClient.class).debug("File "+itemPath+" exists in version "+version+".");
                	    //using svnkit 1.3.0 ,using SVNProperties
                	    //HashMap<String,String> fileProperties = new HashMap<String,String>();
                	    SVNProperties fileProperties =new SVNProperties();
                		repository.getFile(tmpFilePath, -1, fileProperties, null);

                		//String revisionS = fileProperties.get("svn:entry:committed-rev");
                		String revisionS = fileProperties.getStringValue("svn:entry:committed-rev");
                		revision = Integer.parseInt(revisionS);
                		Logger.getLogger(SvnClient.class).debug("File "+tmpFilePath+" last revision = "+revision);
                	}

                    Collection logEntries = repository.log(new String [] {tmpFilePath},null,-1,revision,false, false);



                    if (logEntries.size() == 0)
                    	Logger.getLogger(SvnClient.class).debug("Problem with "+tmpFilePath+" which exists in "+logEntries.size()+" version for head revision.");
                    else {
                    	Iterator it2 = logEntries.iterator();
                    	while (it2.hasNext()) {
	                    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	                    	SVNLogEntry logEntry = (SVNLogEntry) it2.next();
	                    	if (logEntry.getRevision() >= revision) {
		                        System.out.println("---------------------------------------------");
		                        /*
		                         * gets the revision number
		                         */
		                        Logger.getLogger(SvnClient.class).debug("revision: " + logEntry.getRevision());
		                        revisions.add(""+logEntry.getRevision());
		                        /*
		                         * gets the author of the changes made in that revision
		                         */
		                        System.out.println("author: " + logEntry.getAuthor());
		                        authors.add(""+logEntry.getAuthor());
		                        /*
		                         * gets the time moment when the changes were committed
		                         */
		                        System.out.println("date: " + logEntry.getDate());
		                        dates.add(sdf.format(logEntry.getDate()));
		                        /*
		                         * gets the commit log message
		                         */
		                        System.out.println("log message: " + logEntry.getMessage());
		                        comments.add(logEntry.getMessage());

		                        System.out.println("version : " + version);
		                        tags.add(version);
		                        break;
	                    	} else
	                    		Logger.getLogger(SvnClient.class).debug("Not added revision: " + logEntry.getRevision());
                    	}
                    }

                }
            }
        }

	    Logger.getLogger(SvnClient.class).info("authors.size = "+authors.size());
	    Logger.getLogger(SvnClient.class).info("comments.size = "+comments.size());
	    Logger.getLogger(SvnClient.class).info("dates.size = "+dates.size());
	    Logger.getLogger(SvnClient.class).info("revisions.size = "+revisions.size());
	    Logger.getLogger(SvnClient.class).info("tags.size = "+tags.size());

	    String [] authors2 = new String [authors.size()];
	    //Logger.getLogger(SvnClient.class).info("Successfully set authors STEP 1");
	    authors.toArray(authors2);
	    //Logger.getLogger(SvnClient.class).info("Successfully set authors STEP 2");
        infos.setAuthors(authors2);
        //Logger.getLogger(SvnClient.class).info("Successfully set authors ");

        String [] comments2 = new String [comments.size()];
        comments.toArray(comments2);
        infos.setComments(comments2);


        String [] dates2 = new String [dates.size()];
        dates.toArray(dates2);
        infos.setDates(dates2);

        String [] revisions2 = new String [revisions.size()];
        revisions.toArray(revisions2);
        infos.setRevisions(revisions2);

        String [] tags2 = new String [tags.size()];
        tags.toArray(tags2);
        infos.setTagNames(tags2);

        Logger.getLogger(SvnClient.class).info("HistoryInfos successfully setted.");


	    return infos;
	}
	
	public TagStructureInfo[] getTagStructureInfos(String tagRegex) throws SVNException {
		//TODO: care about universe revisions
		if(tagRegex==null||tagRegex.length()==0)tagRegex=".*";
		
		String fileRegex=".*-"+tagRegex;
	    Logger.getLogger(SvnClient.class).debug("NEW Try to get tag structure for file(s) "+fileRegex);

	   if (repository == null) {
	    	Logger.getLogger(SvnClient.class).error("Svn Client versions repository is null");
	    }
	   
	    Collection entries = repository.getDir("", -1, null, (Collection) null);
	    Iterator it = entries.iterator();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    
	    Map<String,TagStructureInfo> tagsMap=new HashMap<String,TagStructureInfo>();
	    while (it.hasNext()) {
	    	SVNDirEntry entry = (SVNDirEntry) it.next();
            System.out.println("/"+ entry.getName() + " (author: '" + entry.getAuthor()+ "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");

            if (entry.getName().matches(fileRegex)) {
            	System.out.println("---------------------------------------------");
            	
            	String cluster=entry.getName().substring(0,entry.getName().indexOf("-"));
            	String tag=entry.getName().substring(entry.getName().indexOf("-")+1);
            	
            	System.out.println("Cluster : " + cluster + " Tag : " + tag);
            	
            	//get last revision
            	int revision=0;
            	SVNProperties fileProperties =new SVNProperties();
        		repository.getFile(entry.getName(), -1, fileProperties, null);
        		String revisionS = fileProperties.getStringValue("svn:entry:committed-rev");
        		revision = Integer.parseInt(revisionS);
        		
            	Collection logEntries = repository.log(new String [] {entry.getName()},null,revision,revision,false, false);
            	
            	SVNLogEntry logEntry = (SVNLogEntry)logEntries.iterator().next();
            	
            	Logger.getLogger(SvnClient.class).debug("revision: " + logEntry.getRevision());
            	System.out.println("author: " + logEntry.getAuthor());
            	System.out.println("date: " + logEntry.getDate());
            	System.out.println("log message: " + logEntry.getMessage());
            	
            	if(tagsMap.get(tag)==null){
            		TagStructureInfo tagStructureInfo=new TagStructureInfo(tag,logEntry.getAuthor(),logEntry.getDate(),logEntry.getMessage());
            		tagStructureInfo.addCluster(cluster);
            		tagsMap.put(tag,tagStructureInfo);
            	}else{
            		TagStructureInfo tagStructureInfo=tagsMap.get(tag);
            		if(logEntry.getDate().after(tagStructureInfo.getLastDate())){
            			tagStructureInfo.setLastDate(logEntry.getDate());
            			tagStructureInfo.setLastAuthor(logEntry.getAuthor());
            			tagStructureInfo.setLastComment(logEntry.getMessage());
            		}
            		tagStructureInfo.addCluster(cluster);
            	}
            }
	    }
	    
	    TagStructureInfo[] tagStructureInfos=new TagStructureInfo[tagsMap.size()];
	    int i=0;
	    for (Iterator iterator = tagsMap.keySet().iterator(); iterator.hasNext();i++) {
			String tag = (String) iterator.next();
			tagStructureInfos[i]=tagsMap.get(tag);
		}
	    
	    return tagStructureInfos;
	}
	
}

