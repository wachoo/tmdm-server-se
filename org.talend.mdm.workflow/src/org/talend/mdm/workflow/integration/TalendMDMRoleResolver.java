package org.talend.mdm.workflow.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ow2.bonita.connector.core.RoleResolver;
import org.talend.mdm.workflow.client.TalendMDMUserFinder;

public class TalendMDMRoleResolver extends RoleResolver {

	private String port;
	private String host;
	private String version;

	@Override
	protected Set<String> getMembersSet(String roleName) throws Exception {
		final String url = "http://" + host + ":" + port +"/talend/TalendPort";
	    final Set<String> candidates = new HashSet<String>();

	    final TalendMDMUserFinder userFinder = new TalendMDMUserFinder(url, version);
	    final List<String> users = userFinder.find(roleName);

	    if (users != null) {
	      candidates.addAll(users);
	    }
	    
	    return candidates;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setVersion(String Version) {
		this.version = (version == null ? "" : version);
	}

}
