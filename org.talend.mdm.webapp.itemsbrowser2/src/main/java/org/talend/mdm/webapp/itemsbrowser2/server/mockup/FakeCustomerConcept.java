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
package org.talend.mdm.webapp.itemsbrowser2.server.mockup;

import java.util.ArrayList;
import java.util.List;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class FakeCustomerConcept {
    
    private List<String> foreignKeyPaths;
    
    
    /**
     * DOC HSHU FakeCustomerConcept constructor comment.
     */
    public FakeCustomerConcept() {
        this.foreignKeyPaths=new ArrayList<String>();
        foreignKeyPaths.add("/customer/address");
    }


    
    public List<String> getForeignKeyPaths() {
        return foreignKeyPaths;
    }
    
}
