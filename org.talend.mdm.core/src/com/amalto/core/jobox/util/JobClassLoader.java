package com.amalto.core.jobox.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class JobClassLoader extends URLClassLoader {

	public JobClassLoader() {
		//super(new URL[0], ClassLoader.getSystemClassLoader());
		//We had better use the same class loader hierarchy as the war package
        // super(new URL[0], JobClassLoader.class.getClassLoader());
        // fix bug 0022967, use current thread classloader
        super(new URL[0], Thread.currentThread().getContextClassLoader());
	}

	public void addPath(String paths) {
		if (paths == null || paths.length() <= 0) {
			return;
		}
        String separator = System.getProperty("path.separator"); //$NON-NLS-1$
		String[] pathToAdds = paths.split(separator);
		for (int i = 0; i < pathToAdds.length; i++) {
			if (pathToAdds[i] != null && pathToAdds[i].length() > 0) {
				try {
					File pathToAdd = new File(pathToAdds[i]).getCanonicalFile();
					addURL(pathToAdd.toURL());
					org.apache.log4j.Logger.getLogger(this.getClass()).info("Added "+pathToAdd.toURL()+" to "+this.toString()+". ");
				} catch (IOException e) {
					// Constants.exceptionHandling(e);
					e.printStackTrace();
				}
			}
		}
	}

}
