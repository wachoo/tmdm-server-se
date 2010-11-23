// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client.resources.icons;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;
@SuppressWarnings("deprecation")
public interface MenuIcons extends ImageBundle {
  
  @Resource("browse.png")
  AbstractImagePrototype browse();
  
  @Resource("default_icon.gif")
  AbstractImagePrototype default_icon();
  
  @Resource("grouping_hier.png")
  AbstractImagePrototype grouping_hier();
  
  @Resource("reporting.png")
  AbstractImagePrototype reporting();
  
  @Resource("trash.png")
  AbstractImagePrototype trash();
  
  @Resource("updatereport.png")
  AbstractImagePrototype updatereport();
  
}
