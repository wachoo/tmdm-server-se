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
package org.talend.mdm.commmon.util.datamodel.management;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSType;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ReusableType {

    private XSType xsType;

    private String name;

    private String parentName;

    private boolean isAbstract;

    // TODO: translate it from technique to business logic
    // mainly maintain the relationships among different business concepts

    public ReusableType(XSType xsType) {
        super();
        this.xsType = xsType;
        name = xsType.getName();
        parentName = xsType.getBaseType().getName();// Is this the best way?
        isAbstract = xsType.asComplexType().isAbstract();
    }

    @Deprecated
    public ReusableType(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parentName;
    }
    
    
    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public XSParticle getXsParticle() {
        return xsType.asComplexType().getContentType().asParticle();
    }

    public List<XSParticle> getAllChildren(XSParticle particle) {
        List<XSParticle> particles = new ArrayList<XSParticle>();
        XSParticle[] children = null;
        if (particle == null) {
            children = xsType.asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
        } else {
            if (particle.getTerm().asModelGroup() != null)
                children = particle.getTerm().asModelGroup().getChildren();
            else {
                particles.add(particle);
                return particles;
            }
        }

        if (children != null) {
            for (XSParticle pt : children) {
                particles.addAll(getAllChildren(pt));
            }
        }

        return particles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReusableType other = (ReusableType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[name=" + name + "]";
    }

}
