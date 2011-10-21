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

package com.amalto.core.jobox.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

public class JobClassLoader extends URLClassLoader {

	public JobClassLoader() {
		//We had better use the same class loader hierarchy as the war package
        // fix bug 0022967, use current thread class loader
        super(new URL[0], Thread.currentThread().getContextClassLoader());
	}

	public void addPath(String paths) {
		if (paths == null || paths.length() <= 0) {
			return;
		}
        String separator = System.getProperty("path.separator"); //$NON-NLS-1$
		String[] pathToAdds = paths.split(separator);
        for (String pathToAdd1 : pathToAdds) {
            if (pathToAdd1 != null && pathToAdd1.length() > 0) {
                try {
                    File pathToAdd = new File(pathToAdd1).getCanonicalFile();
                    addURL(pathToAdd.toURI().toURL());
                    Logger.getLogger(this.getClass()).info("Added " + pathToAdd.toURI().toURL() + " to " + this.toString() + ". ");
                } catch (IOException e) {
                    throw new JoboxException(e);
                }
            }
        }
	}

}
