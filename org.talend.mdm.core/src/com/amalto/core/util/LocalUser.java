package com.amalto.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
/**
 * Information about the logged user obtained by looking at the JACC Context<br/>
 * <br/>
 * To retrieve the user, call {@link #getLocalUser()}<br/>
 * <br/>
 * Information include <ul>
 * <li>The username</li>
 * <li>The Roles</li>
 * <li>The Universe</li>
 * </ul>
 *   
 * @author Bruno Grieder
 */
public class LocalUser {
	
	/*
	 * A very special user that is triggered by scheduled, timeout or startup processes
	 */
	public final static String UNAUTHENTICATED_USER = "anonymous"; 
	
	/**
	 * A cache of local users
	 */
	private static LinkedHashMap<Integer,LocalUser> localUserCache = new LinkedHashMap<Integer, LocalUser>();
	private static LinkedHashMap<Integer,Long> localUserTimeoutCache = new LinkedHashMap<Integer, Long>();
	
	private String username;
	private HashSet<String> roles = new HashSet<String>();
	private UniversePOJO universe;
	private String userXML;
	
	public HashSet<String> getRoles() {
		return roles;
	}
	public void setRoles(HashSet<String> roles) {
		this.roles = roles;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public UniversePOJO getUniverse() {
    	return universe;
    }
	public void setUniverse(UniversePOJO universe) {
    	this.universe = universe;
    }
	/**
	 * The User in XML form as stored in the DB
	 * @return
	 * 		The user in the DB XML form
	 */
	public String getUserXML() {
    	return userXML;
    }
	private void setUserXML(String userXML) {
    	this.userXML = userXML;
    }


	private HashMap<String,HashSet<String>> readWritePermissions = new HashMap<String, HashSet<String>>();
	private HashMap<String,HashSet<String>> readOnlyPermissions = new HashMap<String, HashSet<String>>();
	private HashSet<String> adminPermissions = new HashSet<String>();
	/** By default user are 'admin' on ItemPOJOs */
	private boolean isItemPOJOsAdmin = true;
	
	private HashSet<String> cachedReadOnlyPermissions = new HashSet<String>();
	private HashSet<String> cachedReadWritePermissions = new HashSet<String>();
	
	
	/**
     * Fetch the current user and its roles -  check XtentisLoginModule
     * @return The Local User
     */
    public static LocalUser getLocalUser() throws XtentisException{
//    	org.apache.log4j.Logger.getLogger(LocalUser.class).trace("getLocalUser() ");
    	
//    	Set<String> jkeys = PolicyContext.getHandlerKeys();
//    	for (Iterator<String> iterator = jkeys.iterator(); iterator.hasNext(); ) {
//			String key = iterator.next();
//			org.apache.log4j.Logger.getLogger(LocalUser.class).info("getLocalUser() "+key);
//		}
    	
    	
    	//garbage collect the cache for users that no been active for 10 minutes
    	long time = System.currentTimeMillis();
		Set<Integer> keys = new HashSet<Integer>(localUserTimeoutCache.keySet());
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = iterator.next();
			Long lastAccess = localUserTimeoutCache.get(key);
			if (lastAccess != null) {
    			if (time-lastAccess.longValue() > 10 * 60 * 1000) {
    				LocalUser user = localUserCache.remove(key);
    				localUserTimeoutCache.remove(key);
    				org.apache.log4j.Logger.getLogger(LocalUser.class).debug(
    					"getLocalUser() Removed user '"+(user == null ? "" : user.getUsername())+"' from cache"
    				);
    			}
			}
		}			
    	
    	//Extract roles and username
    	String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";       		
		Subject subject;
		try {
			subject = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
		} catch (PolicyContextException e1) {
			String err = "Unable find the local user: the JACC Policy Context cannot be accessed: "+e1.getMessage();
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err,e1);
			throw new XtentisException(err);
		}

		//This should not happen but it does
		if (subject==null) {
			String err = "No user in the JACC Policy Context!";
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err);
			throw new XtentisException(err);
		}
		
		//update last access
		Integer subjectHash = subject.hashCode();
		localUserTimeoutCache.put(subjectHash, new Long(time));
		
		//Check if we have it in Cache
		LocalUser cachedUser = localUserCache.get(subjectHash);
		if (cachedUser != null) {
			if (org.apache.log4j.Logger.getLogger(LocalUser.class).isTraceEnabled())
				org.apache.log4j.Logger.getLogger(LocalUser.class).trace(
					"Reusing cached User '"+cachedUser.getUsername()+"' (hash "+subjectHash+")"
				);
			return cachedUser;
		} else {
			if (org.apache.log4j.Logger.getLogger(LocalUser.class).isTraceEnabled())
				org.apache.log4j.Logger.getLogger(LocalUser.class).trace(
					"Not in cache - building a new local user from JACC subject\n "+subject
				);

		}
		
   		//hum - all this to get a local user....
   		LocalUser user = new LocalUser();
		
		//JBoss stores the Identity and the RoleSet of the Login Module in the Subject Principals
		Set<Principal> set = subject.getPrincipals();
		for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
			Principal principal = iter.next();
			if (principal instanceof Group) {
				Group group = (Group) principal;
				//@see XtentisMDMLoginModule
				if("Roles".equals(group.getName())) {
					Enumeration<? extends Principal> m = group.members();
					while (m.hasMoreElements()) {
						String role = m.nextElement().getName();
						user.getRoles().add(role);
					}
				} else if("Universe".equals(group.getName())) {
					if (group.members().hasMoreElements()) {
						String universeName = group.members().nextElement().getName();
						UniversePOJO upojo = ObjectPOJO.load(null, UniversePOJO.class, new UniversePOJOPK(universeName));
						if (upojo == null) {
							String err ="The universe '"+universeName+"' cannot be loaded";
							org.apache.log4j.Logger.getLogger(LocalUser.class).error(err);
							throw new XtentisException(err);
						}
						user.setUniverse(upojo);
					} else {
						//head
						user.setUniverse(new UniversePOJO()); 
					}
				} else if("XtentisUser".equals(group.getName())) {
					if (group.members().hasMoreElements()) {
						String marshaled = group.members().nextElement().getName();
						user.setUserXML(marshaled);
					}
				} else if("Username".equals(group.getName())) {
					if (group.members().hasMoreElements()) {
						user.setUsername(group.members().nextElement().getName());
					}
				}
			}
		}//for
		
		
		//Roles
		user.readWritePermissions = new HashMap<String, HashSet<String>>();
		user.readOnlyPermissions = new HashMap<String, HashSet<String>>();
		user.adminPermissions = new HashSet<String>();
		
		//Determine the role revision id for the user
		String roleRevisionID = user.getUniverse().getXtentisObjectsRevisionIDs().get(
			ObjectPOJO.getObjectName(RolePOJO.class)
		);
		
		for (Iterator<String> iterator = user.getRoles().iterator(); iterator.hasNext(); ) {
			String role = iterator.next();
			
			if ("administration".equals(role) || "authenticated".equals(role)) continue;
			
			if ("disabled".equals(role)) {
				String err = "The user '"+user.getUsername()+"' is disabled";
				org.apache.log4j.Logger.getLogger(LocalUser.class).error(err);
				throw new XtentisException(err);
			}
			
			RolePOJO rolePOJO = ObjectPOJO.load(roleRevisionID, RolePOJO.class, new RolePOJOPK(role));
			if (rolePOJO == null) {
				String err = "The role '"+role+"' is unknowwn in revision "+(roleRevisionID == null ? "[HEAD]" : "'"+roleRevisionID+"'");
				org.apache.log4j.Logger.getLogger(LocalUser.class).error(err);
				throw new XtentisException(err);
			}
			HashMap<String, RoleSpecification> roleSpecifications = rolePOJO.getRoleSpecifications();
			Set<String> objectTypes = roleSpecifications.keySet();
			for (Iterator<String> iterator2 = objectTypes.iterator(); iterator2.hasNext(); ) {
				String objectType = iterator2.next();
				RoleSpecification specification = rolePOJO.getRoleSpecifications().get(objectType);
				if (specification.isAdmin()) {
					user.adminPermissions.add(objectType);
				} else {
					//Items are "admin" by default - if we have a spec, then the user is not admin
					if ("Item".equals(objectType)) user.isItemPOJOsAdmin = false;
					//loop over the instance patterns for this object
					Set<String> instancePatterns  = specification.getInstances().keySet();
					for (Iterator<String> iterator3 = instancePatterns.iterator(); iterator3.hasNext(); ) {
						String instancePattern = iterator3.next();
						RoleInstance roleInstance = specification.getInstances().get(instancePattern);
						if (roleInstance.isWriteable()) {
							HashSet<String> patterns = user.readWritePermissions.get(objectType);
							if (patterns == null) {
								patterns = new HashSet<String>();
								user.readWritePermissions.put(objectType, patterns);
							}
							patterns.add(instancePattern);
						} else {
    						HashSet<String> patterns = user.readOnlyPermissions.get(objectType);
    						if (patterns == null) {
    							patterns = new HashSet<String>();
    							user.readOnlyPermissions.put(objectType, patterns);
    						}
    						patterns.add(instancePattern);
						}
					}
				}
			}
		}
		
		//dump user role values
		String res = "PERMISSIONS for user '"+user.getUsername()+"'\n";
		res+=	"\n" +
				"--Roles: ";
		for (Iterator<String> iterator2 = user.getRoles().iterator(); iterator2.hasNext(); ) {
			String role = iterator2.next();
			res+=" '"+role+"'";
		}
		res+=	"\n" +
				"\n" +
				"--Admin permissions: ";
		for (Iterator<String> iterator2 = user.adminPermissions.iterator(); iterator2.hasNext(); ) {
			String objectType = iterator2.next();
			res+=" '"+objectType+"'";
		}
		res+=	"\n" +
		"\n" +
		"--Read/write permissions:";
		for (Iterator<String> iterator2 = user.readWritePermissions.keySet().iterator(); iterator2.hasNext(); ) {
			String objectType = iterator2.next();
			res+="\n    '"+objectType+"':";
			HashSet<String> patterns = user.readWritePermissions.get(objectType);
			for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
				String pattern = iterator.next();
				res+=" '"+pattern+"'";
			}
		}
		res+=	"\n" +
		"\n" +
		"--Read only permissions:";
		for (Iterator<String> iterator2 = user.readOnlyPermissions.keySet().iterator(); iterator2.hasNext(); ) {
			String objectType = iterator2.next();
			res+="\n    '"+objectType+"':";
			HashSet<String> patterns = user.readOnlyPermissions.get(objectType);
			for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
				String pattern = iterator.next();
				res+=" '"+pattern+"'";
			}
		}
		res+=	"\n" +
		"\n" +
		"--Is Items admin? : "+user.isItemPOJOsAdmin;
		org.apache.log4j.Logger.getLogger(LocalUser.class).info(res);
		
		//update cache
		localUserCache.put(subjectHash, user);

		return user;
			

    }
    
    public static void resetLocalUsers() throws XtentisException{
    	 localUserCache.clear();
    	 localUserTimeoutCache.clear();
    	 org.apache.log4j.Logger.getLogger(LocalUser.class).info("Reset the state of LocalUser Cache! ");
	}
    

    /**
     * Logs out the user by removing it from the cache an invalidating the session
     * @throws XtentisException
     */
    public void logout() throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("logout() "+getUsername()+" in "+getUniverse().getName());
    	
    	//Extract roles and username
    	String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";       		
		Subject subject;
		try {
			subject = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
		} catch (PolicyContextException e1) {
			String err = "Unable find the local user: the JACC Policy Context cannot be accessed: "+e1.getMessage();
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err,e1);
			throw new XtentisException(err);
		}
		
		//update last access
		Integer subjectHash = subject.hashCode();
		localUserTimeoutCache.remove(subjectHash);
		localUserCache.remove(subjectHash);
		
		String SERVLET_CONTEXT_KEY = "javax.servlet.http.HttpServletRequest";
		HttpServletRequest request;
		try {
			request = (HttpServletRequest) PolicyContext.getContext(SERVLET_CONTEXT_KEY);
		} catch (PolicyContextException e1) {
			String err = "Unable find the local servlet request: the JACC Policy Context cannot be accessed: "+e1.getMessage();
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err,e1);
			throw new XtentisException(err);
		}
		if (request != null) request.getSession().invalidate();
    	
    }
    
    
    /*****************************************************************************************
     * 
     * Roles Checking
     * 
     * ****************************************************************************************/
    
    /**
     * Check is the user is Admin wrt the Object Type
     * @param objectTypeClass
     * @return true or false
     * @throws XtentisException
     */
    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("userIsAdmin() "+this.getUsername()+": "+objectTypeClass);
    	
    	//by default users are "admin" on ItemPOJOs, unless something is specified in read and write
    	if (objectTypeClass.equals(ItemPOJO.class)) return isItemPOJOsAdmin;
    	
    	//admins are admins
    	if (roles.contains("administration")) return true;

    	//Xtentis Objects
    	String objectType = ObjectPOJO.getObjectName(objectTypeClass);
    	return adminPermissions.contains(objectType);
    }

    /**
     * Checks if the user can change the instance of the object specified
     * Ability to change implies ability to read
     * @param objectTypeClass
     * @param instanceId
     * @return true is the user can change
     * @throws XtentisException
     */
    public boolean userCanWrite(Class<?> objectTypeClass, String instanceId) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("userCanWrite() "+this.getUsername()+": "+objectTypeClass);
    	
    	//background pojos do not have their permissions checked
    	if (objectTypeClass.equals(BackgroundJobPOJO.class)) return true;
    	
    	//ItemPOJOS are special: by default they can be written and read and are not kept in cache
    	if (objectTypeClass.equals(ItemPOJO.class)) {
    		if (isAdmin(ItemPOJO.class)) return true;
    		HashSet<String> patterns = readWritePermissions.get("Item");
    		for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
    			String pattern = iterator.next();
    			if (instanceId.matches(pattern)) {
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	//An Object POJO - get its name and calculate its hashCode
    	String objectType = ObjectPOJO.getObjectName(objectTypeClass);
    	String objectHash = hashMD5(objectType+"-->"+instanceId);
    	
		//if the user administer this object's type, we are done
		if (isAdmin(objectTypeClass)) {
			cachedReadWritePermissions.add(objectHash);
			return true;
		}
    	
		//check if this Object instance is known in cache 
		if (cachedReadWritePermissions.contains(objectHash)) return true;
    	
    	//Match readWritePatterns against the object instance
    	HashSet<String> patterns = readWritePermissions.get(objectType);
    	if (patterns==null) return false;
    	
    	for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
			String pattern = iterator.next();
			if (instanceId.matches(pattern)) {
				cachedReadWritePermissions.add(objectHash);
				return true;
			}
		}
    	
    	return false;
    	
    }


    /**
     * Checks if the user can read the instance of the object specified
     * @param objectTypeClass
     * @param instanceId
     * @return true is the user can read
     * @throws XtentisException
     */
    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(LocalUser.class).trace("userCanRead() User '"+this.getUsername()+"' on type '"+objectTypeClass+"'");
    	
    	//Background POJOS do not need permission check
    	if (objectTypeClass.equals(BackgroundJobPOJO.class)) return true;
    	
    	//ItemPOJOS are special: by default they can be written and read and are not kept in cache
    	if (objectTypeClass.equals(ItemPOJO.class)) {
    		if (userCanWrite(ItemPOJO.class, instanceId)) return true;
    		HashSet<String> patterns = readOnlyPermissions.get("Item");
        	for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
    			String pattern = iterator.next();
    			if (instanceId.matches(pattern)) {
    				return true;
    			}
    		}
        	return false;
    	} 

    	//An object POJO - calculate its hash and type 
    	String objectType = ObjectPOJO.getObjectName(objectTypeClass);
    	String objectHash = hashMD5(objectType+"-->"+instanceId);
    	
    	//check if know in readAndWrite cache
    	if (userCanWrite(objectTypeClass, instanceId)) {
    		//add it to this cache too to speed up next calls
    		cachedReadOnlyPermissions.add(objectHash);
    		return true;
    	}

    	//check if known in cache for items not being ItemPOJOPKs
    	if (cachedReadOnlyPermissions.contains(objectHash)) return true;
    	
    	//Check against patterns
    	HashSet<String> patterns = readOnlyPermissions.get(objectType);
    	if (patterns==null) return false;
    	
    	for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
			String pattern = iterator.next();
			if (instanceId.matches(pattern)) {
				cachedReadOnlyPermissions.add(objectHash);
				return true;
			}
		}
    	return false;
    	
    }
    
    private static String hashMD5(String input) throws XtentisException{
    	try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return new String(digest.digest(input.getBytes("UTF-8")),"UTF-8");
		} catch (NoSuchAlgorithmException e) {
			throw new XtentisException("MD5 is not supported on this platform");
		} catch (UnsupportedEncodingException e) {
			throw new XtentisException("UTF-8 is not supported on this platform");
		}
    }

    
}
