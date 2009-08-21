package com.amalto.webapp.v3.usersandroles.bean;

import java.io.IOException;

import org.w3c.dom.Element;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;


public class Role {

	String name;

	String description;

	String role_to_assign;

	String[] applications;



	public Role() {
	}

	/**
	 * @return Returns the applications.
	 */
	public String[] getApplications() {
		return applications;
	}

	/**
	 * @param applications The applications to set.
	 */
	public void setApplications(String[] applications) {
		this.applications = applications;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the role_to_assign.
	 */
	public String getRole_to_assign() {
		return role_to_assign;
	}

	/**
	 * @param role_to_assign The role_to_assign to set.
	 */
	public void setRole_to_assign(String role_to_assign) {
		this.role_to_assign = role_to_assign;
	}



	/**
	 * Role format
	 
	 <role>
	 	<name/>
	 	<description/>
	 	<role_to_assign/>
	 	<applications>
 			<name/>
	 	</applications>
	 </role>
	 	
	 */
	
	
	public String serialize() {
		String role = 
			"<Role>" +
			"    <name>"+name+"</name>"+
			"    <description>"+description+"</description>"+
			"    <role_to_assign>"+(role_to_assign == null ? "" : role_to_assign)+"</role_to_assign>";
		if ((applications!=null) && (applications.length!=0)) {
			role+=
				"    <applications>";
			for (int i = 0; i < applications.length; i++) {
				role+="<name>"+applications[i]+"</name>";
			}
			role+=
				"    </applications>";
		}
		role+=
		"</Role>";			

		
		return role;
		
		
	}
	
	public static Role parse(String xml) throws XtentisWebappException{
		Role role = new Role();
		parse(xml, role);
		return role;
	}
	
	
	public static void parse(String xml, Role role) throws XtentisWebappException{
				
		try {
			Element result = Util.parse(xml).getDocumentElement();
			role.setName(Util.getFirstTextNode(result, "//name"));
			role.setDescription(Util.getFirstTextNode(result, "//description"));
			role.setRole_to_assign(Util.getFirstTextNode(result, "//role_to_assign"));
			role.setApplications(Util.getTextNodes(result, "//applications/name"));
						
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisWebappException("Failed to parse role: " +": "+e.getLocalizedMessage());
		}
		
	}
	
	
	 private void writeObject(java.io.ObjectOutputStream out)   throws IOException {
		 out.write(serialize().getBytes("UTF-8"));
	 }
	 
	 private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException {
		 try {
			 String xml = in.readUTF();
			 if ((xml == null) || ("".equals(xml))) return;
			 parse(xml, this);
		 } catch (XtentisWebappException x) {throw new IOException(x.getLocalizedMessage());}
	 }
	
	
	

	
	/****************************************************************
	 * Original role stuff from Role - not used here
	 *
	 */
	
	
//	/* (non-Javadoc)
//	 * @see org.jboss.portal.core.model.Role#getUsers()
//	 */
//	public Set getUsers() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.portal.core.model.Role#setDisplayName(java.lang.String)
//	 */
//	public void setDisplayName(String name) {
//		this.description = name;
//	}
//
//	
//	/* (non-Javadoc)
//	 * @see org.jboss.portal.core.model.Role#getDisplayName()
//	 */
//	public String getDisplayName() {
//		return this.description;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.portal.core.model.Role#getID()
//	 */
//	public Integer getID() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//

	

}
