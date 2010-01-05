package com.amalto.webapp.v3.usersandroles.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSGetRole;
import com.amalto.webapp.util.webservices.WSGetRolePKs;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSRole;
import com.amalto.webapp.util.webservices.WSRolePK;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.XtentisPort;
import com.amalto.webapp.v3.usersandroles.bean.User;

public class UsersDWR {

	public UsersDWR() {
		super();
	}

	//FIXME: This should be read from the xtentisLoginModule
	private String dataclusterPK = "PROVISIONING";
	

	public ListRange getUsers(String regex, String language) throws Exception {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("getUsers() regex "+regex+" - language "+language);
		try {

			WSWhereItem wi = new WSWhereItem();
			if ((regex == null) || ("*".equals(regex)) || "".equals(regex)) {
				wi = null;
			} else {
				WSWhereCondition wc = new WSWhereCondition("User",
						WSWhereOperator.CONTAINS, regex,
						WSStringPredicate.NONE, true);
				wi.setWhereCondition(wc);
			}
			
			String[] results = Util.getPort().getItems(
					new WSGetItems(
							new WSDataClusterPK(dataclusterPK),"User", wi, 0, 0, Integer.MAX_VALUE)
					).getStrings();
//			if ((results == null) || (results.length == 0))
//				return null;

			ArrayList<User> names = new ArrayList<User>();
			for (int i = 0; i < results.length; i++) {
//				results[i] = results[i].replaceAll("\\s*__h(.*?)h__\\s*", "$1");
				User user = User.parse(results[i]);
				names.add(user);
			}
			
			ListRange listRange = new ListRange();
			listRange.setData(names.toArray(new User[names.size()]));
			listRange.setTotalSize(names.size());
			
			return listRange;
		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	public User getUser(String pk) throws Exception {
		try {
			String xml = Util.getPort().getItem(
					new WSGetItem(new WSItemPK(new WSDataClusterPK(
							dataclusterPK), "User", new String[] { pk })))
					.getContent();
			User user = User.parse(xml);
			return user;

		} catch (Exception e) {
			Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL)
					.matcher(e.getLocalizedMessage());
			if (m.matches())
				throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	public HashMap<String,String> getRoles(String language) throws Exception {
		try {
			HashMap<String,String> map =new HashMap<String,String>();
			map.put("administration", "This is a role for superadmin. ");
			
			Pattern p = Pattern.compile(".*\\["+language.toUpperCase()+":(.*?)\\].*",Pattern.DOTALL);
			WSRolePK[] wsr = Util.getPort().getRolePKs(new WSGetRolePKs("*"))
					.getWsRolePK();
			for (int i = 0; i < wsr.length; i++) {
				WSRole role = Util.getPort().getRole(new WSGetRole(wsr[i]));
				String desc = p.matcher(role.getDescription()).replaceAll("$1");
				System.out.println("desc "+wsr[i].getPk()+" "+desc);
				if("".equals(desc) || desc==null) desc = wsr[i].getPk();
				map.put(wsr[i].getPk(),desc);
			}
			return map;

		} catch (Exception e) {
			Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL)
					.matcher(e.getLocalizedMessage());
			if (m.matches())
				throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}


	public String deleteUser(String name) throws Exception {
		try {

			XtentisPort port = Util.getPort();

			return port.deleteItem(
					new WSDeleteItem(new WSItemPK(new WSDataClusterPK(
							dataclusterPK), "User", new String[] { name })))
					.getIds()[0];

		} catch (Exception e) {
			Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL)
					.matcher(e.getLocalizedMessage());
			if (m.matches())
				throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	public String saveUser(User user, String oldPassword) throws Exception {
		try {
			if (oldPassword != null) {
				if(user.getPassword()==null || "".equals(user.getPassword())){
					user.setPassword(oldPassword);
				}
				else if (!oldPassword.equals(Util.md5AsHexString(user.getPassword(),"utf-8"))) {
					user.setPassword(Util.md5AsHexString(user.getPassword(),"utf-8"));
				}
			} 
			else {
				user.setPassword(Util.md5AsHexString(user.getPassword(),"utf-8"));
			}
			
			return Util.getPort().putItem(
					new WSPutItem(
							new WSDataClusterPK(dataclusterPK), user.serialize(), 
							new WSDataModelPK("PROVISIONING"),false)
						).getIds()[0];


		} catch (Exception e) {
			Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL)
					.matcher(e.getLocalizedMessage());
			if (m.matches())
				throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

}
