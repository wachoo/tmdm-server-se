// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.itemsbrowser.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.webapp.core.util.Messages;
import com.amalto.webapp.core.util.MessagesFactory;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSGetTransformer;
import com.amalto.webapp.util.webservices.WSGetTransformerPKs;
import com.amalto.webapp.util.webservices.WSTransformer;
import com.amalto.webapp.util.webservices.WSTransformerPK;

public class SmartViewDescriptions {

    private static final long serialVersionUID = 1L;

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "com.amalto.webapp.v3.itemsbrowser.dwr.messages", SmartViewDescriptions.class.getClassLoader()); //$NON-NLS-1$

    private SmartViewProvider provider = new DefaultSmartViewProvider();

    private Map<String, List<SmartViewDescription>> descs;

    public static class SmartViewDescription {

        private String entity;

        private String name;

        private String displayName;

        private String isoLang;

        private String optName;

        private SmartViewDescription(String entity) {
            if (entity == null)
                throw new NullPointerException();
            this.entity = entity;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getIsoLang() {
            return isoLang;
        }

        public String getOptName() {
            return optName;
        }

        @Override
        public boolean equals(Object obj) {
            // Smart Views on same entity are considered equivalent if their option names are equal.
            // Meaning we don't bother of their isoLang
            if (obj instanceof SmartViewDescription) {
                SmartViewDescription other = (SmartViewDescription) obj;
                if (!entity.equals(other.entity))
                    return false;
                if (optName == null)
                    return other.optName == null;
                return optName.equals(other.optName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return optName == null ? "".hashCode() : optName.hashCode(); //$NON-NLS-1$
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public interface SmartViewProvider {

        public WSTransformerPK[] getWSTransformerPKs() throws XtentisWebappException, RemoteException;

        public String getDescription(WSTransformerPK transformerPK) throws XtentisWebappException, RemoteException;
    }

    private static class DefaultSmartViewProvider implements SmartViewProvider {

        public WSTransformerPK[] getWSTransformerPKs() throws XtentisWebappException, RemoteException {
            return Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();//$NON-NLS-1$;
        }

        public String getDescription(WSTransformerPK transformerPK) throws XtentisWebappException, RemoteException {
            WSTransformer wst = Util.getPort().getTransformer(new WSGetTransformer(transformerPK));
            return wst.getDescription();
        }
    }
    
    /**
     * For testing purpose only
     */
    protected void setProvider(SmartViewProvider provider) {
        this.provider = provider;
    }

    public void build(String concept, String defaultIsoLang) throws XtentisWebappException, RemoteException {
        String smRegex = "Smart_view_" + concept + "(_([^#]+))?(#(.+))?";//$NON-NLS-1$//$NON-NLS-2$
        Pattern smp = Pattern.compile(smRegex);

        // get process
        WSTransformerPK[] wstpks = provider.getWSTransformerPKs();
        for (int i = 0; i < wstpks.length; i++) {
            if (wstpks[i].getPk().matches(smRegex)) {
                SmartViewDescription smDesc = new SmartViewDescription(concept);
                smDesc.name = wstpks[i].getPk();
                Matcher matcher = smp.matcher(wstpks[i].getPk());
                while (matcher.find()) {
                    smDesc.isoLang = matcher.group(2);
                    smDesc.optName = matcher.group(4);
                }

                String description = provider.getDescription(wstpks[i]);
                // Try to extract the Smart View display information from its description first
                if (defaultIsoLang != null && defaultIsoLang.length() != 0) {
                    Pattern p = Pattern.compile(".*\\[" + defaultIsoLang.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);//$NON-NLS-1$//$NON-NLS-2$
                    smDesc.displayName = p.matcher(description).replaceAll("$1");//$NON-NLS-1$
                }
                if (smDesc.displayName == null || smDesc.displayName.length() == 0) {
                    if (description.length() != 0)
                        smDesc.displayName = description;
                    else if (smDesc.optName == null)
                        smDesc.displayName = MESSAGES.getMessage("smart.view.default.option"); //$NON-NLS-1$
                    else
                        smDesc.displayName = smDesc.optName;
                }
                add(smDesc);
            }
        }
    }

    private void add(SmartViewDescription smDesc) {
        if (descs == null)
            descs = new HashMap<String, List<SmartViewDescription>>();
        String key;
        if (smDesc.isoLang == null)
            key = ""; //$NON-NLS-1$
        else
            key = smDesc.isoLang.toLowerCase();
        List<SmartViewDescription> smList = descs.get(key);
        if (smList == null) {
            smList = new ArrayList<SmartViewDescription>();
            descs.put(key, smList);
        }
        smList.add(smDesc);
    }

    public Set<SmartViewDescription> get(String isoLang) {
        Set<SmartViewDescription> smSet = new HashSet<SmartViewDescription>();
        if (descs != null) {
            if (isoLang == null)
                isoLang = ""; //$NON-NLS-1$
            List<SmartViewDescription> smList = descs.get(isoLang.toLowerCase());
            if (smList != null)
                smSet.addAll(smList);
        }
        return smSet;
    }
}
